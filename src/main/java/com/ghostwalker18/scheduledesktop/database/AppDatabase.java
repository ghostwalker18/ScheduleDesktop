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

import io.reactivex.rxjava3.subjects.PublishSubject;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Этот класс представляет собой абстрактный прототип базы данных приложения.
 *
 * @author Ипатов Никита
 */
public abstract class AppDatabase {

    public abstract LessonDao lessonDao();
    public abstract NoteDao noteDao();
    private static AppDatabase instance = null;
    private static final  String DATABASE_NAME = "database";
    private final ExecutorService queryExecutorService = Executors.newCachedThreadPool();
    private final PublishSubject<Boolean> onDataBaseUpdated = PublishSubject.create();

    public static AppDatabase getInstance(Class<? extends AppDatabase> type){
        try{
            if(instance == null)
                instance = type.getConstructor().newInstance();
        } catch (Exception ignored){}
        return instance;
    }

    /**
     * Этот метод позволяет выполнить запрос к БД в отдельном пуле потоков.
     * @param job запрос для асинхронного выполнения.
     * @return
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
     * @return
     */
    public static File exportDBFile(){
        return null;
    }

    /**
     * Этот метод заменяет файлы БД приложения импортированными из стороннего источника.
     * @param dbFile архив с файлами БД
     */
    public static void importDBFile(File dbFile){

    }
}