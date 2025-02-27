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

import com.ghostwalker18.scheduledesktop.converters.DateConverters;
import com.ghostwalker18.scheduledesktop.models.Lesson;
import com.ghostwalker18.scheduledesktop.models.Note;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;

/**
 * Этот класс представляет собой реализацию базы данных приложения
 * на основе Hibernate.
 *
 * @author  Ипатов Никита
 */
public class AppDatabaseHibernateImpl
        extends AppDatabase {
    private final SessionFactory sessionFactory;
    private NoteDao noteDao;
    private LessonDao lessonDao;

    @Override
    public LessonDao lessonDao() {
        if (lessonDao != null) {
            return lessonDao;
        } else {
            synchronized(this) {
                if(lessonDao == null) {
                    lessonDao = new LessonDaoHibernateImpl(this);
                }
                return lessonDao;
            }
        }
    }

    @Override
    public NoteDao noteDao() {
        if (noteDao != null) {
            return noteDao;
        } else {
            synchronized(this) {
                if(noteDao == null) {
                    noteDao = new NoteDaoHibernateImpl(this);
                }
                return noteDao;
            }
        }
    }

    public AppDatabaseHibernateImpl(File dbFile){
        super(dbFile);
        final StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml");
        if(dbFile != null && dbFile.exists())
            registry.applySetting("connection.url", "jdbc:sqlite:" + dbFile.getAbsolutePath());

        MetadataSources sources = new MetadataSources(registry.build());
        sources.addAnnotatedClass(Lesson.class)
                .addAnnotatedClass(Note.class)
                .addPackage(this.getClass().getPackage());
        MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
        metadataBuilder.applyAttributeConverter(DateConverters.class);

        Metadata metadata = metadataBuilder.build();

        sessionFactory = metadata.getSessionFactoryBuilder()
                .build();
    }

    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }
}