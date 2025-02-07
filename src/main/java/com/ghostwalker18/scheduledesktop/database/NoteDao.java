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


package com.ghostwalker18.scheduledesktop.database;

import com.ghostwalker18.scheduledesktop.models.Note;
import io.reactivex.rxjava3.core.Observable;
import java.util.Calendar;
import java.util.List;

/**
 * Интерфейс DAO для работы с таблицой БД, содержащей сведения о заметках к занятиям.
 *
 * @author  Ипатов Никита
 * @see Note
 */
public interface NoteDao {

    /**
     * Этот метод позволяет получить заметку из базы данных по ее ID.
     * @param id идентификатор заметки
     * @return заметка
     */
    @Query(sqlStatement = "SELECT * FROM tblNote WHERE id = :id")
    Observable<Note> getNote(Integer id);

    /**
     * Этот метод позволяет получить заметки для заданной группы и дня.
     * @param date день
     * @param group группа
     * @return список заметок
     */
    @Query(sqlStatement = "SELECT * FROM tblNote WHERE noteDate = :date AND noteGroup = :group")
    Observable<List<Note>> getNotes(Calendar date, String group);

    /**
     * Этот метод позволяет получить заметки для заданных группы и дней.
     * @param dates дни
     * @param group группа
     * @return список заметок
     */
    @Query(sqlStatement = "SELECT * FROM tblNote WHERE noteDate IN (:dates) AND noteGroup = :group " +
            "ORDER BY noteDate")
    Observable<List<Note>> getNotesForDays(Calendar[] dates, String group);

    /**
     * Этот метод позволяет получить заметки, содержащие в теме или тексте заданное слова.
     * @param keyword ключевое слово
     * @return список заметок
     */
    @Query(sqlStatement = "SELECT * FROM tblNote WHERE (noteText LIKE '%' || :keyword || '%' OR " +
            "noteTheme LIKE '%' || :keyword || '%') AND noteGroup = :group " +
            "ORDER BY noteDate DESC")
    Observable<List<Note>> getNotesByKeyword(String keyword, String group);

    /**
     * Этот метод позволяет синхронно получить все содержимое заметок (например, для экспорта).
     * @return все содержимое tblNote
     */
    @Query(sqlStatement = "SELECT * FROM tblNote")
    List<Note> getAllNotesSync();

    /**
     * Этот метод позволяет синхронно удалить все содержимое tblNote
     */
    @Query(sqlStatement = "DELETE FROM tblNote")
    void deleteAllNotesSync();

    /**
     * Этот метод позволяет внести заметку в БД.
     * @param note заметка
     */
    void insert(Note note);

    /**
     * Этот метод позволяет синхронно вставить элементы Note в БД.
     * @param notes заметки
     */
    void insertManySync(List<Note> notes);

    /**
     * Этот метод позволяет обновить заметку из БД.
     * @param note заметка
     */
    void update(Note note);

    /**
     * Этот метод позволяет удалить заметку из БД.
     * @param note заметка
     */
    void delete(Note note);
}