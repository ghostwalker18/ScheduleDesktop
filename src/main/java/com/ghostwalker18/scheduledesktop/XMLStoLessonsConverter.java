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

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    /**
     * Этот метод используется для обработки файла расписания первого корпуса на Первомайском пр.
     *
     * @param excelFile эксель файл расписания для первого корпуса
     * @return лист объектов класса Lesson
     */
    public List<Lesson> convertFirstCorpus(XSSFWorkbook excelFile){
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
                for(int j = sheet.getFirstRowNum() + FIRST_ROW_GAP_1;
                    j < sheet.getLastRowNum();
                    j += SCHEDULE_CELL_HEIGHT_1){
                    for(int k : groupBounds){
                        if(sheet.getRow(j).getCell(k).getStringCellValue().equals(groups.get(k)))
                            break scheduleFilling;
                        Lesson lesson = new Lesson();
                        lesson.setDate(dateConverters.convertFirstCorpusDate(date));
                        lesson.setGroup(Objects.requireNonNull(groups.get(k)));
                        lesson.setLessonNumber(sheet.getRow(j)
                                .getCell(1)
                                .getStringCellValue()
                                .trim());
                        lesson.setTimes(sheet.getRow(j + 1)
                                .getCell(1)
                                .getStringCellValue()
                                .trim());
                        lesson.setSubject(sheet.getRow(j)
                                .getCell(k)
                                .getStringCellValue()
                                .trim());
                        lesson.setTeacher(sheet.getRow(j + 1)
                                .getCell(k)
                                .getStringCellValue()
                                .trim());
                        Integer nextGroupBound = groupBounds.higher(k);
                        if(nextGroupBound != null){
                            lesson.setRoomNumber(getCellContentsAsString(sheet, j, nextGroupBound - 1).trim());
                        }
                        else{
                            String roomNumber = getCellContentsAsString(sheet, j, k + 2).trim();
                            if(!roomNumber.equals(""))
                                lesson.setRoomNumber(roomNumber);
                            else
                                lesson.setRoomNumber(getCellContentsAsString(sheet, j, k + 3).trim());
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

    /**
     * Этот метод используется для обработки файла расписания второго корпуса на ул.Мурманской.
     *
     * @param excelFile эксель файл расписания для второго корпуса
     * @return лист объектов класса Lesson
     */
    public List<Lesson> convertSecondCorpus(XSSFWorkbook excelFile){
        List<Lesson> lessons = new ArrayList<>();
        DateConverters dateConverters = new DateConverters();

        for(int i = 0; i < excelFile.getNumberOfSheets(); i++) {
            XSSFSheet sheet = excelFile.getSheetAt(i);
            String date = sheet.getSheetName().trim();
            NavigableMap<Integer, String> groups = new TreeMap<>();
            XSSFRow groupsRow = sheet.getRow(3);
            if (groupsRow == null)
                break;
            for (int j = groupsRow.getFirstCellNum() + 2; j < groupsRow.getLastCellNum(); j++) {
                XSSFCell groupRowCell = groupsRow.getCell(j);
                //if cells are united, only first cell in union is not null
                if (groupRowCell == null)
                    continue;
                if (!groupRowCell.getStringCellValue().trim().equals("") &&
                        !groupRowCell.getStringCellValue().trim().equals("Группа") &&
                        !groupRowCell.getStringCellValue().trim().equals("День недели")
                )
                    groups.put(j, groupRowCell.getStringCellValue());
            }

            scheduleFilling : {
                NavigableSet<Integer> groupBounds = groups.navigableKeySet();
                for(int j = sheet.getFirstRowNum() + FIST_ROW_GAP_2;
                    j < sheet.getLastRowNum();
                    j += SCHEDULE_CELL_HEIGHT_2){
                    for(int k : groupBounds){
                        if(sheet.getRow(j).getCell(k).getStringCellValue().equals(groups.get(k)))
                            break scheduleFilling;
                        Lesson lesson = new Lesson();
                        lesson.setDate(dateConverters.convertSecondCorpusDate(date));
                        lesson.setGroup(Objects.requireNonNull(groups.get(k)));
                        lesson.setLessonNumber(sheet.getRow(j)
                                .getCell(1)
                                .getStringCellValue()
                                .trim());
                        lesson.setTimes(sheet.getRow(j + 1)
                                .getCell(1)
                                .getStringCellValue()
                                .trim());
                        String lessonSubject = sheet.getRow(j)
                                .getCell(k)
                                .getStringCellValue()
                                .trim();
                        if(sheet.getRow(j+1)
                                        .getCell(k)
                                                .getStringCellValue() != null){
                            lessonSubject += sheet.getRow(j+1)
                                    .getCell(k)
                                    .getStringCellValue()
                                    .trim();
                        }
                        lesson.setSubject(lessonSubject);
                        lesson.setTeacher(sheet.getRow(j + 2)
                                .getCell(k)
                                .getStringCellValue()
                                .trim());
                        Integer nextGroupBound = groupBounds.higher(k);
                        if(nextGroupBound != null){
                            lesson.setRoomNumber(getCellContentsAsString(sheet, j, nextGroupBound - 1)
                            + getCellContentsAsString(sheet, j + 1, nextGroupBound - 1));
                        }
                        else{
                            String roomNumber = getCellContentsAsString(sheet, j, k + 3) +
                                    getCellContentsAsString(sheet, j + 1, k + 3);
                            if(!roomNumber.equals(""))
                                lesson.setRoomNumber(roomNumber);
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

    /**
     * Этот метод используется для получения содержимого ячейки в виде строки.
     *
     * @param sheet лист эксель
     * @param row номер ряда ячейки
     * @param column номер столбца ячейки
     * @return содержимое ячейки в виде строки
     */
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