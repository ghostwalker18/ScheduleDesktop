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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Этот класс содержит в себе методы для работы с файлами расписания ПТГХ.
 *
 * @author  Ипатов Никита
 */
public class XMLStoLessonsConverter
    implements IConverter{
    private static  int FIRST_ROW_GAP_1;
    private static final int GROUPS_ROW_1 = 3;
    private static final int SCHEDULE_HEIGHT_1 = 24;
    private static final int SCHEDULE_CELL_HEIGHT_1 = 4;

    private static final int FIRST_ROW_GAP_2 = 5;
    private static final int GROUPS_ROW_2 = 3;
    private static final int SCHEDULE_CELL_HEIGHT_2 = 2;

    public List<Lesson> convertFirstCorpus(Workbook excelFile){
        List<Lesson> lessons = new ArrayList<>();
        DateConverters dateConverters = new DateConverters();

        for(int i = 0; i < excelFile.getNumberOfSheets(); i++) {
            Sheet sheet = excelFile.getSheetAt(i);
            RowCache cache = RowCache.builder()
                    .setSheet(sheet)
                    .setSize(10)
                    .build();

            String dateString = sheet.getSheetName().trim();
            Calendar date = dateConverters.convertFirstCorpusDate(dateString);

            NavigableMap<Integer, String> groups = new TreeMap<>();
            Row groupsRow = cache.getRow(GROUPS_ROW_1);
            //checking if there is a schedule at the list
            if (groupsRow == null)
                break;
            //getting groups` names
            for (int j = groupsRow.getFirstCellNum() + 2; j < groupsRow.getLastCellNum(); j++) {
                Cell groupRowCell = groupsRow.getCell(j);
                //if cells are united, only first cell in union is not null
                if (groupRowCell == null)
                    continue;
                if (!groupRowCell.getStringCellValue().trim().equals("") &&
                        !groupRowCell.getStringCellValue().trim().equals("Группа") &&
                        !groupRowCell.getStringCellValue().trim().equals("День недели")
                ){
                    String group = groupRowCell.getStringCellValue().trim();
                    //mistake protection
                    groups.put(j, group.replaceAll("\\s+", "").trim());
                }
            }

            //searching for first row gap where schedule starts
            for(int j = GROUPS_ROW_1 + 2; j < sheet.getLastRowNum(); j++){
                if(!cache.getRow(j).getZeroHeight()){
                    FIRST_ROW_GAP_1 = j;
                    break;
                }
            }

            //start filling schedule from top to bottom and from left to right
            scheduleFilling : {
                NavigableSet<Integer> groupBounds = groups.navigableKeySet();
                for(int j = sheet.getFirstRowNum() + FIRST_ROW_GAP_1;
                    j < FIRST_ROW_GAP_1 + SCHEDULE_HEIGHT_1;
                    j += SCHEDULE_CELL_HEIGHT_1){
                    for(int k : groupBounds){
                        Lesson lesson = new Lesson();
                        lesson.setDate(date);
                        lesson.setGroup(Objects.requireNonNull(groups.get(k)));
                        lesson.setLessonNumber(getCellContentsAsString(cache, j, 1).trim());
                        lesson.setTimes(prepareTimes(
                                getCellContentsAsString(cache, j + 1, 1).trim()));
                        String lessonSubject = getCellContentsAsString(cache, j, k) + " " +
                                getCellContentsAsString(cache, j + 1, k);
                        lesson.setSubject(prepareSubject(lessonSubject.trim()));
                        lesson.setTeacher(prepareTeacher(getCellContentsAsString(cache, j + 2, k).trim()));
                        Integer nextGroupBound = groupBounds.higher(k);
                        String roomNumber;
                        if(nextGroupBound != null){
                            roomNumber = getCellContentsAsString(cache, j, nextGroupBound - 1).trim() + " "
                                    + getCellContentsAsString(cache, j + 1, nextGroupBound - 1).trim() + " "
                                    + getCellContentsAsString(cache, j + 2, nextGroupBound - 1).trim();
                        }
                        else{
                            roomNumber = getCellContentsAsString(cache, j, k + 3).trim() + " "
                                    + getCellContentsAsString(cache, j + 1, k + 3).trim() + " "
                                    + getCellContentsAsString(cache, j + 2, k + 3).trim();
                        }
                        lesson.setRoomNumber(prepareRoomNumber(roomNumber));
                        //Required for primary key
                        if(!lesson.getSubject().equals(""))
                            lessons.add(lesson);
                    }
                }
            }
        }

        return lessons;
    }

    public List<Lesson> convertSecondCorpus(Workbook excelFile){
        List<Lesson> lessons = new ArrayList<>();
        DateConverters dateConverters = new DateConverters();

        for(int i = 0; i < excelFile.getNumberOfSheets(); i++){
            Sheet sheet = excelFile.getSheetAt(i);
            RowCache cache = RowCache.builder()
                    .setSheet(sheet)
                    .setSize(5)
                    .build();

            String dateString = sheet.getSheetName() + "." + Calendar.getInstance().get(Calendar.YEAR);
            Calendar date = dateConverters.convertSecondCorpusDate(dateString);

            NavigableMap<Integer, String> groups = new TreeMap<>();
            Row groupsRow = cache.getRow(GROUPS_ROW_2);
            //checking if there is a schedule at the list
            if(groupsRow == null)
                break;
            //getting groups` names
            for(int j = groupsRow.getFirstCellNum() + 2; j < groupsRow.getLastCellNum(); j++){
                Cell groupRowCell = groupsRow.getCell(j);
                //if cells are united, only first cell in union is not null
                if(groupRowCell == null )
                    continue;
                if(!groupRowCell.getStringCellValue().trim().equals("")){
                    String group = groupRowCell.getStringCellValue().trim();
                    //mistake protection
                    groups.put(j, group.replaceAll("\\s+", ""));
                }
            }

            //start filling schedule from top to bottom and from left to right
            scheduleFilling : {
                NavigableSet<Integer> groupBounds = groups.navigableKeySet();
                for(int j = sheet.getFirstRowNum() + FIRST_ROW_GAP_2;
                    j < sheet.getLastRowNum();
                    j += SCHEDULE_CELL_HEIGHT_2){
                    for(int k : groupBounds){
                        //bottom of schedule are group names, breaking here
                        if(cache.getRow(j).getCell(k).getStringCellValue().equals(groups.get(k)))
                            break scheduleFilling;
                        Lesson lesson = new Lesson();
                        lesson.setDate(date);
                        lesson.setGroup(Objects.requireNonNull(groups.get(k)));
                        lesson.setLessonNumber(getCellContentsAsString(cache, j, 1).trim());
                        lesson.setTimes(prepareTimes(
                                getCellContentsAsString(cache, j + 1, 1).trim()));
                        lesson.setSubject(prepareSubject(getCellContentsAsString(cache, j, k).trim()));
                        lesson.setTeacher(prepareTeacher(getCellContentsAsString(cache, j + 1, k).trim()));
                        Integer nextGroupBound = groupBounds.higher(k);
                        String roomNumber;
                        if(nextGroupBound != null){
                            roomNumber = getCellContentsAsString(cache, j, nextGroupBound - 1).trim();
                        }
                        else{
                            roomNumber = getCellContentsAsString(cache, j, k + 2).trim();
                            if(roomNumber.equals(""))
                                roomNumber = getCellContentsAsString(cache, j, k + 3).trim();
                        }
                        lesson.setRoomNumber(prepareRoomNumber(roomNumber));
                        //Required for primary key
                        if(!lesson.getSubject().equals(""))
                            lessons.add(lesson);
                    }
                }
            }
        }

        return lessons;
    }

    /**
     * Этот метод используется для получения содержимого ячейки в виде строки.
     *
     * @param cache лист эксель
     * @param row номер ряда ячейки
     * @param column номер столбца ячейки
     * @return содержимое ячейки в виде строки
     */
    private static String getCellContentsAsString(RowCache cache, int row, int column){
        Cell cell = cache.getRow(row)
                .getCell(column);
        if(cell == null)
            return "";
        switch (cell.getCellType()){
            case STRING:
                return cache.getRow(row)
                        .getCell(column)
                        .getStringCellValue();
            case NUMERIC:
                return String.valueOf((int)cache.getRow(row)
                        .getCell(column)
                        .getNumericCellValue());
            default:
                return "";
        }
    }

    /**
     * Этот метод используется для преобразования строки с временем занятия к формату ХХ.ХХ
     * @param times время
     * @return время
     */
    private static String prepareTimes(String times){
        if(times.startsWith("0") || times.startsWith("1") || times.startsWith("2") || times.equals(""))
            return times;
        return "0" + times;
    }

    /**
     * Этот метод используется для приведения строки с именем преподавателя к удобочитаемому виду.
     * @param teacher имя преподавателя
     * @return обработанное имя преподавателя
     */
    private static String prepareTeacher(String teacher){
        return teacher.replaceAll("\\s+", " ")
                .replaceAll("/", "");
    }

    /**
     * Этот метод используется для приведения строки с номером кабинета к удобочитаемому виду.
     * @param roomNumber номер кабинета
     * @return обработанный номер кабинета
     */
    private static String prepareRoomNumber(String roomNumber){
        return roomNumber.replaceAll("\\s*/\\s*", "/")
                .replaceAll("\\s+", " ");
    }

    /**
     * Этот метод используется для приведения строки с названием предмета к удобочитаемому виду.
     * @param subject название предмета
     * @return обработанное название предмета
     */
    private static String prepareSubject(String subject){
        return subject.replaceAll("\\s+", " ");
    }
}