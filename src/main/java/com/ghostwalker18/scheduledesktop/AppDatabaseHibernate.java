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

import com.sun.xml.bind.v2.TODO;
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
    private final PublishSubject<Boolean> onDataBaseUpdate = PublishSubject.create();

    //TODO:optimize it (future hint: Reflexion API is a key)!!
    private final PublishSubject<List<String>> getTeachersResult = PublishSubject.create();
    private final PublishSubject<List<String>> getGroupsResult = PublishSubject.create();
    private final PublishSubject<List<Lesson>> getLessonsForGroupWithTeacherResult = PublishSubject.create();
    private final PublishSubject<List<Lesson>> getLessonsForGroupResult = PublishSubject.create();
    private final PublishSubject<List<Lesson>> getLessonsForTeacherResult = PublishSubject.create();

    private Calendar getLessonsForGroupWithTeacherDate = null;
    private String getLessonsForGroupWithTeacherTeacher = null;
    private String getLessonsForGroupWithTeacherGroup = null;

    private Calendar getLessonsForGroupDate = null;
    private String getLessonsForGroupGroup = null;

    private Calendar getLessonsForTeacherDate = null;
    private String getLessonsForTeacherTeacher = null;

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

        onDataBaseUpdate.subscribe(e->{
            getTeachers();
            getGroups();
            if(getLessonsForGroupWithTeacherDate != null &&
                    getLessonsForGroupWithTeacherGroup != null &&
                    getLessonsForGroupWithTeacherTeacher != null)
                getLessonsForGroupWithTeacher(getLessonsForGroupWithTeacherDate,
                    getLessonsForGroupWithTeacherGroup,
                    getLessonsForGroupWithTeacherTeacher);
            if(getLessonsForTeacherDate != null && getLessonsForTeacherTeacher != null)
                getLessonsForTeacher(getLessonsForTeacherDate, getLessonsForTeacherTeacher);
            if(getLessonsForGroupDate != null && getLessonsForGroupGroup != null)
                getLessonsForGroup(getLessonsForGroupDate, getLessonsForGroupGroup);
        });
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
        onDataBaseUpdate.onNext(true);
    }

    public void update(Lesson lesson){
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                session.update(lesson);
            }
        }).start();
        onDataBaseUpdate.onNext(true);
    }

    public Observable<List<String>> getTeachers(){
        String hql = "select distinct teacher from Lesson order by teacher asc";
        new Thread(() -> {
            try(Session session = sessionFactory.openSession()){
                Query<String> query = session.createQuery(hql, String.class);
                getTeachersResult.onNext(query.list());
            }
        }).start();
        return getTeachersResult;
    }

    public Observable<List<String>> getGroups(){
        String hql = "select distinct groupName from Lesson order by groupName asc";
        new Thread(() -> {
            try(Session session = sessionFactory.openSession()){
                Query<String> query = session.createQuery(hql, String.class);
                getGroupsResult.onNext(query.list());
            }
        }).start();
        return getGroupsResult;
    }

    public Observable<List<Lesson>> getLessonsForGroupWithTeacher(Calendar date, String group, String teacher){
        getLessonsForGroupWithTeacherDate = date;
        getLessonsForGroupWithTeacherGroup = group;
        getLessonsForGroupWithTeacherTeacher = teacher;

        String hql = "from Lesson where groupName = :groupName and teacher like :teacherName and date = :date";
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                query.setParameter("teacherName", teacher);
                getLessonsForGroupWithTeacherResult.onNext(query.list());
            }
        }).start();
        return getLessonsForGroupWithTeacherResult;
    }

    public Observable<List<Lesson>> getLessonsForGroup(Calendar date, String group){
        getLessonsForGroupDate = date;
        getLessonsForGroupGroup = group;

        String hql = "from Lesson where teacher like :teacherName and date = :date";
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                getLessonsForGroupResult.onNext(query.list());
            }
        }).start();
        return getLessonsForGroupResult;
    }

    public Observable<List<Lesson>> getLessonsForTeacher(Calendar date, String teacher){
        getLessonsForTeacherDate = date;
        getLessonsForTeacherTeacher = teacher;

        String hql = "from Lesson where groupName = :groupName and date = :date";
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("teacherName", teacher);
                getLessonsForTeacherResult.onNext(query.list());
            }
        }).start();
        return getLessonsForTeacherResult;
    }
}