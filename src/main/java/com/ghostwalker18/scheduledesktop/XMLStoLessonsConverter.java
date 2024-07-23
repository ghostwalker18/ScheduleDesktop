package com.ghostwalker18.scheduledesktop;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

public class XMLStoLessonsConverter {
    public static List<Lesson> convert(XSSFWorkbook excelFile){
        List<Lesson> lessons = new ArrayList<>();
        DateConverters dateConverters = new DateConverters();

        for(int i = 0; i < excelFile.getNumberOfSheets(); i++){
            XSSFSheet sheet = excelFile.getSheetAt(i);
            String date = sheet.getSheetName() + "." + Calendar.getInstance().get(Calendar.YEAR);
            NavigableMap<Integer, String> groups = new TreeMap<>();
            XSSFRow groupsRow = sheet.getRow(3);
            if(groupsRow == null)
                break;
            for(int j = groupsRow.getFirstCellNum() + 2; j < groupsRow.getLastCellNum(); j++){
                XSSFCell groupRowCell = groupsRow.getCell(j);
                if(groupRowCell == null )
                    continue;
                if(!groupRowCell.getStringCellValue().equals(""))
                    groups.put(j, groupRowCell.getStringCellValue());
            }

            scheduleFilling : {
                NavigableSet<Integer> groupBounds = groups.navigableKeySet();
                for(int j = sheet.getFirstRowNum() + 5; j < sheet.getLastRowNum(); j +=2){
                    for(int k : groupBounds){
                        if(sheet.getRow(j).getCell(k).getStringCellValue().equals(groups.get(k)))
                            break scheduleFilling;
                        Lesson lesson = new Lesson();
                        lesson.setDate(dateConverters.convertToEntityAttribute(date));
                        lesson.setGroup(Objects.requireNonNull(groups.get(k)));
                        lesson.setLessonNumber(sheet.getRow(j)
                                .getCell(1)
                                .getStringCellValue());
                        lesson.setTimes(sheet.getRow(j + 1)
                                .getCell(1)
                                .getStringCellValue());
                        lesson.setSubject(sheet.getRow(j)
                                .getCell(k)
                                .getStringCellValue());
                        lesson.setTeacher(sheet.getRow(j + 1)
                                .getCell(k)
                                .getStringCellValue());
                        Integer nextGroupBound = groupBounds.higher(k);
                        if(nextGroupBound != null){
                            lesson.setRoomNumber(getCellContentsAsString(sheet, j, nextGroupBound - 1));
                        }
                        else{
                            String roomNumber = getCellContentsAsString(sheet, j, k + 2);
                            if(!roomNumber.equals(""))
                                lesson.setRoomNumber(roomNumber);
                            else
                                lesson.setRoomNumber(getCellContentsAsString(sheet, j, k + 3));
                        }
                        //Required for primary key
                        if(!lesson.getSubject().equals(""))
                            lessons.add(lesson);
                    }
                }
            }
        }
        return lessons;
    }

    private static String getCellContentsAsString(XSSFSheet sheet, int row, int column){
        CellType cellType = sheet.getRow(row)
                .getCell(column)
                .getCellTypeEnum();
        switch (cellType){
            case STRING:
                return sheet.getRow(row)
                        .getCell(column)
                        .getStringCellValue();
            case NUMERIC:
                return String.valueOf((int)sheet.getRow(row)
                        .getCell(column)
                        .getNumericCellValue());
            default:
                return "";
        }
    }
}
