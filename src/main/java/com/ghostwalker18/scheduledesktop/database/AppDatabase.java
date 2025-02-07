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

import com.ghostwalker18.scheduledesktop.models.Lesson;
import com.ghostwalker18.scheduledesktop.models.Note;
import com.sun.istack.NotNull;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Этот класс представляет собой абстрактный прототип базы данных приложения.
 *
 * @author Ипатов Никита
 */
public abstract class AppDatabase {

    /**
     * Этот метод возвращает DAO для доступа к занятиям.
     * @return LessonDAO
     */
    public abstract LessonDao lessonDao();

    /**
     * Этот метод возвращает DAO для доступа к заметкам.
     * @return NoteDAO
     */
    public abstract NoteDao noteDao();
    private static AppDatabase instance = null;
    protected static final  String DATABASE_NAME = "database";
    private final ExecutorService queryExecutorService = Executors.newCachedThreadPool();
    private final PublishSubject<Boolean> onDataBaseUpdated = PublishSubject.create();
    protected File dbFile;

    /**
     * Этот метод позволяет получить экземпляр данного класса встроенной БД приложения.
     * @param type класс встроенной БД
     * @param dbFile файл БД (опционально)
     * @return встроенная БД
     */
    public static AppDatabase getInstance(Class<? extends AppDatabase> type, File... dbFile){
        try{
            if(instance == null)
                if(dbFile != null)
                    instance = type.getConstructor(File.class).newInstance(dbFile[0]);
                else
                    instance = type.getConstructor(File.class).newInstance((File) null);
        } catch (Exception ignored){/*Not required*/}
        return instance;
    }

    protected AppDatabase(File dbFile){
        this.dbFile = dbFile;
    }

    /**
     * Этот метод позволяет выполнить запрос к БД в отдельном пуле потоков.
     * @param job запрос для асинхронного выполнения.
     */
    public Future runQuery(Runnable job){
        return queryExecutorService.submit(job);
    }

    /**
     * Этот метод возвращает трекер изменений БД.
     * @return трекер изменений БД
     */
    public PublishSubject<Boolean> getInvalidationTracker(){
        return onDataBaseUpdated;
    }

    /**
     * Этот метод позволяет получить архивированные файлы БД приложения для ее экспорта.
     * @return файл БД приложения
     */
    public  File exportDBFile(@NotNull String dataType){
        AppDatabase exportDB = AppDatabase.getInstance(instance.getClass());
        exportDB.lessonDao().deleteAllLessonsSync();
        exportDB.noteDao().deleteAllNotesSync();
        if(dataType.equals("schedule") || dataType.equals("schedule_and_notes")){
            List<Lesson> lessons = instance.lessonDao().getAllLessonsSync();
            exportDB.lessonDao().insertManySync(lessons);
        }
        if(dataType.equals("notes") || dataType.equals("schedule_and_notes")){
            List<Note> notes = instance.noteDao().getAllNotesSync();
            exportDB.noteDao().insertManySync(notes);
        }
        return null;
    }

    /**
     * Этот метод заменяет файлы БД приложения импортированными из стороннего источника.
     * @param dbFile архив с файлами БД
     * @param dataType тип данных для импорта (расписание и т.п)
     * @param importPolicy политика импорта - слияние или замена данных.
     */
    public  void importDBFile(File dbFile, String dataType, String importPolicy){
        AppDatabase importDB = AppDatabase.getInstance(instance.getClass(), dbFile);
        if(dataType.equals("schedule") || dataType.equals("schedule_and_notes")){
            if(importPolicy.equals("replace"))
                instance.lessonDao().deleteAllLessonsSync();
            List<Lesson> lessons = importDB.lessonDao().getAllLessonsSync();
            instance.lessonDao().insertManySync(lessons);
        }
        if(dataType.equals("notes") || dataType.equals("schedule_and_notes")){
            if(importPolicy.equals("replace"))
                instance.noteDao().deleteAllNotesSync();
            List<Note> notes = importDB.noteDao().getAllNotesSync();
            instance.noteDao().insertManySync(notes);
        }
    }
}