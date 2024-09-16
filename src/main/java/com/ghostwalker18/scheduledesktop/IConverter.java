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

import org.apache.poi.ss.usermodel.Workbook;
import java.util.List;

/**
 * Этот интерфейс описывет методы для преобразования эксель-файлов расписания ПТГХ в коллекции
 * элементов Lesson.
 *
 * @author Ипатов Никита
 */
public interface IConverter {
    /**
     * Этот метод используется для обработки файла расписания первого корпуса на ул.Мурманской.
     *
     * @param excelFile эксель файл расписания для первого корпуса
     * @return лист объектов класса Lesson
     */
    List<Lesson> convertFirstCorpus(Workbook excelFile);

    /**
     * Этот метод используется для обработки файла расписания второго корпуса на Первомайском пр.
     *
     * @param excelFile эксель файл расписания для второго корпуса
     * @return лист объектов класса Lesson
     */
    List<Lesson> convertSecondCorpus(Workbook excelFile);
}