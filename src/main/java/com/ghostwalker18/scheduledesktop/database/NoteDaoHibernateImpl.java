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

import com.ghostwalker18.scheduledesktop.database.AppDatabaseHibernateImpl;
import com.ghostwalker18.scheduledesktop.database.NoteDao;
import com.ghostwalker18.scheduledesktop.models.Note;
import io.reactivex.rxjava3.core.Observable;
import java.util.Calendar;

/**
 * Этот класс представляет собой реализацию интерфейса NoteDAO на основе Hibernate.
 *
 * @author Ипатов Никита
 */
public class NoteDaoHibernateImpl
        implements NoteDao {
    private AppDatabaseHibernateImpl db;

    public NoteDaoHibernateImpl(AppDatabaseHibernateImpl db){
        this.db = db;
    }
    @Override
    public Observable<Note> getNote(Integer id) {
        return null;
    }

    @Override
    public Observable<Note[]> getNotes(Calendar date, String group) {
        return null;
    }

    @Override
    public Observable<Note[]> getNotesForDays(Calendar[] dates, String group) {
        return null;
    }

    @Override
    public Observable<Note[]> getNotesByKeyword(String keyword, String group) {
        return null;
    }

    @Override
    public void insert(Note note) {

    }

    @Override
    public void update(Note note) {

    }

    @Override
    public void delete(Note note) {

    }
}