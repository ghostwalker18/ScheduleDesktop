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

import com.ghostwalker18.scheduledesktop.*;
import com.ghostwalker18.scheduledesktop.models.Lesson;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

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
        return lessonDao;
    }

    @Override
    public NoteDao noteDao() {
        return noteDao;
    }

    public AppDatabaseHibernateImpl(){
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Lesson.class)
                .addPackage(this.getClass().getPackage());
        MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
        metadataBuilder.applyAttributeConverter(DateConverters.class);

        Metadata metadata = metadataBuilder.build();

        sessionFactory = metadata.getSessionFactoryBuilder()
                .build();
        lessonDao = new LessonDaoHibernateImpl(this);
        noteDao = new NoteDaoHibernateImpl(this);
    }

    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }
}