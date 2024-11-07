/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghostwalker18.scheduledesktop;

import com.github.pjfanning.xlsx.StreamingReader;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import okhttp3.ResponseBody;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;

/**
 * Этот класс представляет собой репозиторий данных приложения.
 *
 * @author  Ипатов Никита
 */
public class ScheduleRepository {
    public static final String mondayTimesURL = "https://r1.nubex.ru/s1748-17b/47698615b7_fit-in~1280x800~filters:no_upscale()__f44488_08.jpg";
    public static final String otherTimesURL = "https://r1.nubex.ru/s1748-17b/320e9d2d69_fit-in~1280x800~filters:no_upscale()__f44489_bb.jpg";
    private static ScheduleRepository repository = null;
    private final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private final IScheduleNetworkAPI api;
    private final IAppDatabase db;
    private IConverter converter = new XMLStoLessonsConverter();
    private final Preferences preferences = Application.getPreferences();
    private final String BASE_URI = "https://ptgh.onego.ru/9006/";
    private final String MAIN_SELECTOR = "h2:contains(Расписание занятий и объявления:) + div > table > tbody";
    public final static String mondayTimesPath = "mondayTimes.jpg";
    public final static String otherTimesPath = "otherTimes.jpg";
    private final List<Pair<String, File>> scheduleFiles = new LinkedList<>();
    private final BehaviorSubject<BufferedImage> mondayTimes = BehaviorSubject.create();
    private final BehaviorSubject<BufferedImage> otherTimes = BehaviorSubject.create();
    private final ReplaySubject<Status> status = ReplaySubject.create();
    private final ExecutorService updateExecutorService = Executors.newFixedThreadPool(4);
    private final List<Future<?>> updateFutures = new ArrayList<>();

    /**
     * Этот класс используетс для отображения статуса обновления репозитория.
     */
    public static class Status{
        public String text;
        public int progress;

        public Status(String text, int progress){
            this.text = text;
            this.progress = progress;
        }
    }

    /**
     * Этот метод используется для получения доступа к репозиторию.
     *
     * @return синглтон репозитория приложения
     */
    public static ScheduleRepository getRepository(){
        if(repository == null)
                repository = new ScheduleRepository();
        return repository;
    }

    private ScheduleRepository(){
        db = AppDatabaseHibernate.getInstance();
        api = new Retrofit.Builder()
                .baseUrl(BASE_URI)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build()
                .create(IScheduleNetworkAPI.class);
    }

    /**
     * Этот метод используется для получения состояния,
     * в котором находится процесс обновления репозитория.
     *
     * @return статус состояния
     */
    public Observable<Status> getStatus(){
        return status;
    }

    /**
     * Этот метод используется для получения буфферизированого файла изображения
     * расписания звонков на понедельник.
     *
     * @return фото расписания звонков на понедельник
     */
    public Observable<BufferedImage> getMondayTimes(){
        return mondayTimes;
    }

    /**
     * Этот метод используется для получения буфферизированого файла изображения
     * расписания звонков со вторника по пятницу.
     *
     * @return фото расписания звонков со вторника по пятницу
     */
    public Observable<BufferedImage> getOtherTimes(){
        return otherTimes;
    }

    /**
     * Этот метод обновляет репозиторий приложения.
     * Метод использует многопоточность и может вызывать исключения в других потоках.
     * Требуется интернет соединение.
     */
    public void update(){
        boolean allJobsDone = true;
        for(Future<?> future : updateFutures){
            allJobsDone &= future.isDone();
        }
        if(allJobsDone){
            updateFutures.clear();
            updateFutures.add(updateExecutorService.submit(this::updateFirstCorpus));
            updateFutures.add(updateExecutorService.submit(this::updateSecondCorpus));
            updateFutures.add(updateExecutorService.submit(this::updateTimes));
        }
    }

    /**
     * Этот метод возвращает всех учителей, упоминаемых в расписании.
     *
     * @return список учителей
     */
    public Observable<List<String>> getTeachers(){
        return db.getTeachers();
    }

    /**
     * Этот метод возвращает все группы, упоминаемые в расписании.
     *
     * @return список групп
     */
    public Observable<List<String>> getGroups(){
        return db.getGroups();
    }

    /**
     * Этот метод возращает список занятий в этот день у группы у данного преподавателя.
     * Если группа не указана, то возвращается список занятий у преподавателя в этот день.
     * Если учитель не указан, то возвращается список занятй у группы в этот день.
     *
     * @param date день
     * @param teacher преподаватель
     * @param group группа
     * @return спискок занятий
     */
    @NotNull
    public Observable<List<Lesson>> getSchedule(Calendar date, @Nullable String teacher, @Nullable String group){
        if (teacher != null && group != null)
            return db.getLessonsForGroupWithTeacher(date, group, teacher);
        else if (teacher != null)
            return db.getLessonsForTeacher(date, teacher);
        else if (group != null)
            return db.getLessonsForGroup(date, group);
        else return BehaviorSubject.createDefault(new LinkedList<>());
    }

    /**
     * Этот метод получает ссылки с сайта ПАСТ,
     * по которым доступно расписание для корпуса на Первомайском проспекте.
     *
     * @return список ссылок
     */
    @NotNull
    public  List<String> getLinksForSecondCorpusSchedule(){
        List<String> links = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(BASE_URI).get();
            Elements linkElements = doc.select(MAIN_SELECTOR).get(0)
                    .select("tr").get(1)
                    .select("td").get(1)
                    .select("a");
            for(Element linkElement : linkElements){
                if(linkElement.attr("href").endsWith(".xlsx"))
                    links.add(linkElement.attr("href"));
            }
            return links;
        }
        catch (IOException e){
            return links;
        }
    }

    /**
     * Этот метод получает ссылки с сайта ПАСТ,
     * по которым доступно расписание для корпуса на Мурманской улице.
     *
     * @return список ссылок
     */
    public List<String> getLinksForFirstCorpusSchedule(){
        List<String> links = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(BASE_URI).get();
            Elements linkElements = doc.select(MAIN_SELECTOR).get(0)
                    .select("tr").get(1)
                    .select("td").get(0)
                    .select("a");
            for(Element linkElement : linkElements){
                if(linkElement.attr("href").endsWith(".xlsx"))
                    links.add(linkElement.attr("href"));
            }
            return links;
        }
        catch(IOException e){
            return links;
        }
    }

    /**
     * Этот метод возвращает пары название файла / содержимое файла для всех скачанных файлов расписания.
     * @return
     */
    public List<Pair<String, File>> getScheduleFiles(){
        return scheduleFiles;
    }

    /**
     * Этот метод предназначен для сохранения последней выбранной группы перед закрытием приложения.
     * @param group группа для сохранения
     */
    public void saveGroup(String group){
        preferences.put("savedGroup", group);
    }

    /**
     * Этот метод возвращает сохраненную группу.
     * @return группа
     */
    public String getSavedGroup(){
        return preferences.get("savedGroup", platformStrings.getString("combox_placeholder"));
    }

    /**
     * Этот метод позволяет получить имя скачиваемого файла из ссылки на него.
     * @param link ссылка на файл
     * @return имя файла
     */
    private String getNameFromLink(String link){
        String[] parts = link.split("/");
        return parts[parts.length - 1];
    }

    private void updateTimes(){
        File mondayTimesFile = new File(mondayTimesPath);
        File otherTimesFile = new File(otherTimesPath);
        if(!preferences.getBoolean("doNotUpdateTimes", true)
                || !mondayTimesFile.exists()
                || !otherTimesFile.exists()){
            Call<ResponseBody> mondayTimesResponse = api.getMondayTimes();
            mondayTimesResponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.body() != null){
                        try{
                            BufferedImage image = ImageIO.read(response.body().byteStream());
                            mondayTimes.onNext(image);
                            ImageIO.write(image, "jpg", mondayTimesFile);
                        }
                        catch (Exception ignored){/*Not required*/}
                        finally {
                            response.body().close();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
            Call<ResponseBody> otherTimesResponse = api.getOtherTimes();
            otherTimesResponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.body() != null){
                        try{
                            BufferedImage image = ImageIO.read(response.body().byteStream());
                            otherTimes.onNext(image);
                            ImageIO.write(image, "jpg", otherTimesFile);
                        }
                        catch (Exception ignored){/*Not required*/}
                        finally {
                            response.body().close();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        }
        else {
            try {
                BufferedImage bitmap1 = ImageIO.read(mondayTimesFile);
                mondayTimes.onNext(bitmap1);
                BufferedImage bitmap2 = ImageIO.read(otherTimesFile);
                otherTimes.onNext(bitmap2);
            }
            catch(Exception ignored){/*Not required*/}
        }
    }

    private void updateFirstCorpus(){
        List<String> scheduleLinks = getLinksForFirstCorpusSchedule();
        if(scheduleLinks.size() == 0)
            status.onNext(new Status(strings.getString("schedule_download_error"), 0));
        for(String link : scheduleLinks){
            status.onNext(new Status(strings.getString("schedule_download_status"), 10));
            api.getScheduleFile(link).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.body() != null){
                        status.onNext(new Status(strings.getString("schedule_parsing_status"),
                                33));
                        try(InputStream stream = response.body().byteStream()){
                            File scheduleFile = Files
                                    .createTempFile(null, ".tmp")
                                    .toFile();
                            scheduleFile.deleteOnExit();
                            Files.copy(stream, scheduleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            scheduleFiles.add(new Pair<>(getNameFromLink(link), scheduleFile));
                            ZipSecureFile.setMinInflateRatio(0.0075);
                            Workbook excelFile = StreamingReader.builder()
                                    .rowCacheSize(10)
                                    .bufferSize(4096)
                                    .open(scheduleFile);
                            List<Lesson> lessons = converter.convertFirstCorpus(excelFile);
                            excelFile.close();
                            db.insertMany(lessons);
                            status.onNext(new Status(strings.getString("processing_completed_status"),
                                    100));
                        }
                        catch (Exception e){
                            status.onNext(new Status(strings.getString("schedule_parsing_error"),
                                    0));
                        }
                        response.body().close();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    status.onNext(new Status(strings.getString("schedule_download_error"), 0));
                }
            });
        }
    }

    private void updateSecondCorpus(){
        List<String> scheduleLinks = getLinksForSecondCorpusSchedule();
        if(scheduleLinks.size() == 0)
            status.onNext(new Status(strings.getString("schedule_download_error"), 0));
        for(String link : scheduleLinks){
            status.onNext(new Status(strings.getString("schedule_download_status"), 10));
            api.getScheduleFile(link).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.body() != null){
                        status.onNext(new Status(strings.getString("schedule_parsing_status"),
                                33));
                        try(InputStream stream = response.body().byteStream()){
                            File scheduleFile = Files
                                    .createTempFile(null, ".tmp")
                                    .toFile();
                            scheduleFile.deleteOnExit();
                            Files.copy(stream, scheduleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            scheduleFiles.add(new Pair<>(getNameFromLink(link), scheduleFile));
                            ZipSecureFile.setMinInflateRatio(0.0075);
                            Workbook excelFile = StreamingReader.builder()
                                    .rowCacheSize(10)
                                    .bufferSize(4096)
                                    .open(scheduleFile);
                            List<Lesson> lessons = converter.convertSecondCorpus(excelFile);
                            excelFile.close();
                            db.insertMany(lessons);
                            status.onNext(new Status(strings.getString("processing_completed_status"),
                                    100));
                        }
                        catch (Exception e){
                            status.onNext(new Status(strings.getString("schedule_parsing_error"),
                                    0));
                        }
                        response.body().close();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    status.onNext(new Status(strings.getString("schedule_download_error"), 0));
                }
            });
        }
    }


}