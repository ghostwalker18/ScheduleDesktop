/*import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


public class DataProvider {

    private Document doc;
    private Context context;
    private  final String collegeSiteURL = "https://ptgh.onego.ru/9006/";
    public final static String pathToMondayTimes = "images/mondayTimes.jpg";
    public final static String pathToOtherTimes = "images/otherTimes.jpg";
    public final static String pathToSchedule = "schedule/schedule.json";

    public DataProvider(Context context) {
        try {
            this.context = context;
            doc = Jsoup.connect(collegeSiteURL).get();
        } catch (IOException e) {
            return;
        }
    }

    public synchronized File getMondayTimes() throws Exception{
        return getFile("images", "mondayTimes.jpg", "a[title='Расписание звонков на ПОНЕДЕЛЬНИК']");
    }

    public synchronized File getOtherTimes() {
        return getFile("images", "otherTimes.jpg", "a[title='Основное расписание звонков']");
    }

    public synchronized String getSchedule(String saveTemporaryFileDirectoryPath) throws IOException, JSONException{
        String selector = "h2:contains(Расписание занятий и объявления:) + div > table > tbody";
        String link = doc.select(selector).get(0)
                .select("tr").get(1)
                .select("td").get(1)
                .select("p > a").get(1)
                .attr("href");
        File fullSaveDirectoryPath = new File(this.context.getFilesDir() + "/" + "schedule");
        if (!fullSaveDirectoryPath.exists())
            fullSaveDirectoryPath.mkdir();
        File scheduleExcelFile = new File(fullSaveDirectoryPath, "temp");
        scheduleExcelFile = receiveFileFromNetwork(scheduleExcelFile, link);
        FileInputStream stream = new FileInputStream(scheduleExcelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(stream);
        //Assuming that the schedule is on the first list
        XSSFSheet sheet = workbook.getSheetAt(0);
        JSONObject schedule = new JSONObject();
        schedule.put("createTime", "");
        JSONArray groups = new JSONArray();
        //Assuming that the group's names are in row 1
        XSSFRow group_names = sheet.getRow(0);
        short group_names_len = group_names.getLastCellNum();
        //Set group indexation from cell D1
        int group_index = 3;
        //Empty cells by some reason return null
        while (group_index < group_names_len - 1) {
            //Right cell of the each group's name is named "каб"
            if (group_names.getCell(group_index + 1).getStringCellValue().equals("каб")) {
                JSONObject group = new JSONObject();
                JSONArray weekDays = new JSONArray();
                String group_name = group_names.getCell(group_index).getStringCellValue();
                group.put("name", group_name);
                //Starting looking through group's column
                int row_number = 0;
                //Going down the column until first empty
                while (sheet.getRow(row_number) != null && sheet.getRow(row_number).getCell(group_index) != null) {
                    String date = sheet.getRow(row_number).getCell(0).getStringCellValue();
                    if (!date.equals("")) {
                        JSONObject weekDay = new JSONObject();
                        String[] dateSplited = date.split("\\(");
                        weekDay.put("weekday", dateSplited[1].substring(0, dateSplited[1].length() - 1));
                        weekDay.put("date", dateSplited[0]);
                        row_number++;
                        JSONArray subjects = new JSONArray();
                        XSSFRow g = sheet.getRow(row_number);
                        while (sheet.getRow(row_number) != null && sheet.getRow(row_number).getCell(group_index) != null &&
                                !sheet.getRow(row_number).getCell(group_index)
                                        .getStringCellValue().equals(group_name)) {
                            XSSFCell subjectNameCell = sheet.getRow(row_number).getCell(group_index);
                            if (!subjectNameCell.getStringCellValue().equals("")) {
                                JSONObject subject = new JSONObject();
                                subject.put("name", subjectNameCell.getStringCellValue());
                                XSSFCell subjectNumberCell = sheet.getRow(row_number).getCell(1);
                                if (subjectNumberCell.getCellTypeEnum() == CellType.NUMERIC)
                                    subject.put("number", Double.toString(subjectNumberCell.getNumericCellValue()));
                                else
                                    subject.put("number", subjectNumberCell.getStringCellValue());
                                XSSFCell subjectTimeCell = sheet.getRow(row_number).getCell(2);
                                subject.put("time", subjectTimeCell.getStringCellValue());
                                XSSFCell subjectRoomCell = sheet.getRow(row_number).getCell(group_index + 1);
                                if (subjectRoomCell.getCellTypeEnum() == CellType.NUMERIC)
                                    subject.put("room", Double.toString(subjectRoomCell.getNumericCellValue()));
                                else
                                    subject.put("room", subjectRoomCell.getStringCellValue());
                                subjects.put(subject);
                            }
                            row_number++;
                        }
                        weekDay.put("subjects", subjects);
                        weekDays.put(weekDay);
                    } else {
                        row_number++;
                    }
                }
                group.put("schedule", weekDays);
                groups.put(group);
            }
            group_index++;
        }
        schedule.put("schedule", groups);
        return schedule.toString();
    }

    private synchronized File getFile(String saveDirectoryPath, String name, String selector){
        File fullSaveDirectoryPath = new File(this.context.getFilesDir() + "/" + saveDirectoryPath);
        if(!fullSaveDirectoryPath.exists())
            fullSaveDirectoryPath.mkdir();
        File file = new File(fullSaveDirectoryPath, name);
        return receiveFileFromNetwork(file, doc.select(selector).first().attr("href"));
    }

    private synchronized File receiveFileFromNetwork(File fileToSave, String urlString){
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            //Rewriting file is acceptable
            if(!fileToSave.exists())
                fileToSave.createNewFile();
            try (InputStream stream = connection.getInputStream();
                 BufferedInputStream reader = new BufferedInputStream(stream);
                 FileOutputStream fileStream = new FileOutputStream(fileToSave)) {
                int current = 0;
                byte[] buffer = new byte[1024];
                while ((current = reader.read(buffer)) != -1) {
                    fileStream.write(buffer, 0, current);
                }
            } catch (IOException e) {
                String s = e.getMessage();
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return fileToSave;
    }
}*/
