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

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import java.util.Calendar;
import java.util.List;

/**
 * Этот класс представляет собой реализацию базы данных приложения
 * на основе Hibernate.
 *
 * @author  Ипатов Никита
 */
public class AppDatabaseHibernate implements AppDatabase{
    private static AppDatabaseHibernate instance = null;
    private final SessionFactory sessionFactory;

    public static AppDatabaseHibernate getInstance(){
        if(instance == null)
            instance = new AppDatabaseHibernate();
        return instance;
    }

    private AppDatabaseHibernate(){
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
    }

    public void insertMany(List<Lesson> lessons){
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Transaction transaction = session.getTransaction();
                transaction.begin();
                for(Lesson lesson : lessons){
                    session.merge(lesson);
                };
                transaction.commit();
            }
        }).start();
    }

    public void update(Lesson lesson){
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                session.update(lesson);
            }
        }).start();
    }

    public Observable<List<String>> getTeachers(){
        String hql = "select distinct teacher from Lesson order by teacher asc";
        final PublishSubject<List<String>> teachers = PublishSubject.create();
        new Thread(() -> {
            try(Session session = sessionFactory.openSession()){
                Query<String> query = session.createQuery(hql, String.class);
                teachers.onNext(query.list());
            }
        }).start();
        return teachers;
    }

    public Observable<List<String>> getGroups(){
        String hql = "select distinct groupName from Lesson order by groupName asc";
        final PublishSubject<List<String>> groups = PublishSubject.create();
        new Thread(() -> {
            try(Session session = sessionFactory.openSession()){
                Query<String> query = session.createQuery(hql, String.class);
                groups.onNext(query.list());
            }
        }).start();
        return groups;
    }

    public Observable<List<Lesson>> getLessonsForGroupWithTeacher(Calendar date, String group, String teacher){
        String hql = "from Lesson where groupName = :groupName and teacher like :teacherName and date = :date";
        final PublishSubject<List<Lesson>> lessons = PublishSubject.create();
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                query.setParameter("teacherName", teacher);
                lessons.onNext(query.list());
            }
        }).start();
        return lessons;
    }

    public Observable<List<Lesson>> getLessonsForGroup(Calendar date, String group){
        String hql = "from Lesson where teacher like :teacherName and date = :date";
        final PublishSubject<List<Lesson>> lessons = PublishSubject.create();
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                lessons.onNext(query.list());
            }
        }).start();
        return lessons;
    }

    public Observable<List<Lesson>> getLessonsForTeacher(Calendar date, String teacher){
        String hql = "from Lesson where groupName = :groupName and date = :date";
        final PublishSubject<List<Lesson>> lessons = PublishSubject.create();
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("teacherName", teacher);
                lessons.onNext(query.list());
            }
        }).start();
        return lessons;
    }
}