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
import java.util.*;

/**
 * Этот класс содержит в себе методы для работы с файлами расписания ПТГХ.
 *
 * @author  Ипатов Никита
 */
public class XMLStoLessonsConverter
    implements IConverter{

    private static final int FIRST_ROW_GAP_1 = 5;
    private static final int FIST_ROW_GAP_2 = 105;
    private static final int SCHEDULE_CELL_HEIGHT_1 = 2;
    private static final int SCHEDULE_CELL_HEIGHT_2 = 4;

    public List<Lesson> convertFirstCorpus(Workbook excelFile){
        List<Lesson> lessons = new ArrayList<>();
        DateConverters dateConverters = new DateConverters();

        for(int i = 0; i < excelFile.getNumberOfSheets(); i++){
            Sheet sheet = excelFile.getSheetAt(i);
            String date = sheet.getSheetName() + "." + Calendar.getInstance().get(Calendar.YEAR);
            NavigableMap<Integer, String> groups = new TreeMap<>();
            Row groupsRow = sheet.getRow(3);
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
                for(int j = sheet.getFirstRowNum() + FIRST_ROW_GAP_1;
                    j < sheet.getLastRowNum();
                    j += SCHEDULE_CELL_HEIGHT_1){
                    for(int k : groupBounds){
                        //bottom of schedule are group names, breaking here
                        if(sheet.getRow(j).getCell(k).getStringCellValue().equals(groups.get(k)))
                            break scheduleFilling;
                        Lesson lesson = new Lesson();
                        lesson.setDate(dateConverters.convertFirstCorpusDate(date));
                        lesson.setGroup(Objects.requireNonNull(groups.get(k)));
                        lesson.setLessonNumber(getCellContentsAsString(sheet, j, 1).trim());
                        lesson.setTimes(getCellContentsAsString(sheet, j + 1, 1).trim());
                        lesson.setSubject(getCellContentsAsString(sheet, j, k).trim());
                        lesson.setTeacher(getCellContentsAsString(sheet, j + 1, k).trim());
                        Integer nextGroupBound = groupBounds.higher(k);
                        String roomNumber;
                        if(nextGroupBound != null){
                            roomNumber = getCellContentsAsString(sheet, j, nextGroupBound - 1).trim();
                        }
                        else{
                            roomNumber = getCellContentsAsString(sheet, j, k + 2).trim();
                            if(roomNumber.equals(""))
                                roomNumber = getCellContentsAsString(sheet, j, k + 3).trim();
                        }
                        lesson.setRoomNumber(roomNumber);
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

        for(int i = 0; i < excelFile.getNumberOfSheets(); i++) {
            Sheet sheet = excelFile.getSheetAt(i);
            String date = sheet.getSheetName().trim();
            NavigableMap<Integer, String> groups = new TreeMap<>();
            Row groupsRow = sheet.getRow(3);
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

            //start filling schedule from top to bottom and from left to right
            scheduleFilling : {
                NavigableSet<Integer> groupBounds = groups.navigableKeySet();
                for(int j = sheet.getFirstRowNum() + FIST_ROW_GAP_2;
                    j < sheet.getLastRowNum();
                    j += SCHEDULE_CELL_HEIGHT_2){
                    for(int k : groupBounds){
                        //bottom of schedule are group names, breaking here
                        if(sheet.getRow(j).getCell(k).getStringCellValue().equals(groups.get(k)))
                            break scheduleFilling;
                        Lesson lesson = new Lesson();
                        lesson.setDate(dateConverters.convertSecondCorpusDate(date));
                        lesson.setGroup(Objects.requireNonNull(groups.get(k)));
                        lesson.setLessonNumber(getCellContentsAsString(sheet, j, 1).trim());
                        lesson.setTimes(getCellContentsAsString(sheet, j + 1, 1).trim());
                        String lessonSubject = getCellContentsAsString(sheet, j, k) + " " +
                                getCellContentsAsString(sheet, j + 1, k);
                        lesson.setSubject(lessonSubject.trim());
                        lesson.setTeacher(getCellContentsAsString(sheet, j + 2, k).trim());
                        Integer nextGroupBound = groupBounds.higher(k);
                        String roomNumber;
                        if(nextGroupBound != null){
                            roomNumber = getCellContentsAsString(sheet, j, nextGroupBound - 1) + " "
                            + getCellContentsAsString(sheet, j + 1, nextGroupBound - 1) + " "
                            + getCellContentsAsString(sheet, j + 2, nextGroupBound - 1);
                        }
                        else{
                            roomNumber = getCellContentsAsString(sheet, j, k + 3) + " " +
                                    getCellContentsAsString(sheet, j + 1, k + 3) + " " +
                                    getCellContentsAsString(sheet, j + 2, k + 3);
                        }
                        lesson.setRoomNumber(roomNumber.trim());
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
     * @param sheet лист эксель
     * @param row номер ряда ячейки
     * @param column номер столбца ячейки
     * @return содержимое ячейки в виде строки
     */
    private static String getCellContentsAsString(Sheet sheet, int row, int column){
        Cell cell = sheet.getRow(row)
                .getCell(column);
        if(cell == null)
            return "";
        switch (cell.getCellTypeEnum()){
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