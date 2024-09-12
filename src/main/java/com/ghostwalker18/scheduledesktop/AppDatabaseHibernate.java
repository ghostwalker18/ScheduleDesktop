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
import io.reactivex.rxjava3.subjects.BehaviorSubject;
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
import java.util.*;

/**
 * Этот класс представляет собой реализацию базы данных приложения
 * на основе Hibernate.
 *
 * @author  Ипатов Никита
 */
public class AppDatabaseHibernate
        implements IAppDatabase{
    private static AppDatabaseHibernate instance = null;
    private final SessionFactory sessionFactory;
    private final PublishSubject<Boolean> onDataBaseUpdate = PublishSubject.create();

    //TODO:optimize it (future hint: Reflexion API is a key)!!
    private final BehaviorSubject<List<String>> getTeachersResult = BehaviorSubject.create();
    private final BehaviorSubject<List<String>> getGroupsResult = BehaviorSubject.create();

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

        //Trigger for database update
        //If was modified: redo all cashed queries
        onDataBaseUpdate.subscribe(e -> {
            getTeachers();
            getGroups();
            for(GetLessonsForGroupQuery.GetLessonsForGroupArgs args : GetLessonsForGroupQuery.cachedResults.keySet()){
                getLessonsForGroup(args.date, args.group);
            }
            for(GetLessonsForTeacherQuery.GetLessonsForTeacherArgs args : GetLessonsForTeacherQuery.cachedResults.keySet()){
                getLessonsForTeacher(args.date, args.teacher);
            }
            for(GetLessonsForGroupWithTeacherQuery.GetLessonsForGroupWithTeacherArgs args : GetLessonsForGroupWithTeacherQuery.cachedResults.keySet()){
                getLessonsForGroupWithTeacher(args.date, args.group, args.teacher);
            }
        });
    }

    public void insertMany(List<Lesson> lessons){
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Transaction transaction = session.getTransaction();
                transaction.begin();
                int counter = 0;
                for(Lesson lesson : lessons){
                    counter++;
                    session.merge(lesson);
                    if(counter % 32 == 0){//same as the JDBC batch size
                        //flush a batch of inserts and release memory:
                        session.flush();
                        session.clear();
                    }
                }
                transaction.commit();
                onDataBaseUpdate.onNext(true);
            }
        }).start();
    }

    public void update(Lesson lesson){
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Transaction transaction = session.getTransaction();
                transaction.begin();
                session.update(lesson);
                transaction.commit();
                onDataBaseUpdate.onNext(true);
            }
        }).start();

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
        BehaviorSubject<List<Lesson>>  queryResult = GetLessonsForGroupWithTeacherQuery.cacheQuery(date, group, teacher);

        String hql = "from Lesson where groupName = :groupName and teacher like :teacherName and date = :date";
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                query.setParameter("teacherName", "%" + teacher + "%");
                queryResult.onNext(query.list());
            }
        }).start();
        return queryResult;
    }

    public Observable<List<Lesson>> getLessonsForGroup(Calendar date, String group){
        BehaviorSubject<List<Lesson>>  queryResult = GetLessonsForGroupQuery.cacheQuery(date, group);

        String hql = "from Lesson where groupName = :groupName and date = :date";
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                queryResult.onNext(query.list());
            }
        }).start();
        return queryResult;
    }

    public Observable<List<Lesson>> getLessonsForTeacher(Calendar date, String teacher){
        BehaviorSubject<List<Lesson>> queryResult = GetLessonsForTeacherQuery.cacheQuery(date, teacher);

        String hql = "from Lesson where teacher like :teacherName and date = :date";
        new Thread(()->{
            try(Session session = sessionFactory.openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("teacherName", "%" + teacher + "%");
                queryResult.onNext(query.list());
            }
        }).start();
        return queryResult;
    }

    //TODO:optimize it (future hint: Reflexion API is a key)!!
    /**
     * Этот класс используется для кэширования запросов GetLessonsForGroup
     */
    private static class GetLessonsForGroupQuery{
        public static final Map<GetLessonsForGroupArgs, BehaviorSubject<List<Lesson>>> cachedResults = new HashMap<>();

        private static class GetLessonsForGroupArgs{
            public Calendar date;
            public String group;

            GetLessonsForGroupArgs(Calendar date, String group){
                this.date = date;
                this.group = group;
            }

            @Override
            public boolean equals(Object o){
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;
                GetLessonsForGroupArgs that = (GetLessonsForGroupArgs) o;
                return that.date == this.date && that.group.equals(this.group);
            }

            @Override
            public int hashCode(){
                return Objects.hash(date, group);
            }
        }

        public static BehaviorSubject<List<Lesson>> cacheQuery(Calendar date, String group){
            if(!cachedResults.containsKey(new GetLessonsForGroupArgs(date, group)))
                cachedResults.put(new GetLessonsForGroupArgs(date, group), BehaviorSubject.create());
            return  cachedResults.get(new GetLessonsForGroupArgs(date, group));
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetLessonsForTeacher
     */
    private static class GetLessonsForTeacherQuery{
        public static final Map<GetLessonsForTeacherArgs, BehaviorSubject<List<Lesson>>> cachedResults = new HashMap<>();

        private static class GetLessonsForTeacherArgs{
            public Calendar date;
            public String teacher;

            GetLessonsForTeacherArgs(Calendar date, String teacher){
                this.date = date;
                this.teacher = teacher;
            }

            @Override
            public boolean equals(Object o){
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;
                GetLessonsForTeacherArgs that = (GetLessonsForTeacherArgs) o;
                return that.date == this.date && that.teacher.equals(this.teacher);
            }

            @Override
            public int hashCode(){
                return Objects.hash(date, teacher);
            }
        }

        public static BehaviorSubject<List<Lesson>> cacheQuery(Calendar date, String teacher){
            if(!cachedResults.containsKey(new GetLessonsForTeacherArgs(date, teacher)))
                cachedResults.put(new GetLessonsForTeacherArgs(date, teacher), BehaviorSubject.create());
            return cachedResults.get(new GetLessonsForTeacherQuery.GetLessonsForTeacherArgs(date, teacher));
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetLessonsForGroupWithTeacher
     */
    private static class GetLessonsForGroupWithTeacherQuery{
        public static final Map<GetLessonsForGroupWithTeacherArgs,
                BehaviorSubject<List<Lesson>>> cachedResults = new HashMap<>();

        private static class GetLessonsForGroupWithTeacherArgs{
            public Calendar date;
            public String group;
            public String teacher;

            GetLessonsForGroupWithTeacherArgs(Calendar date, String group, String teacher){
                this.date = date;
                this.group = group;
                this.teacher = teacher;
            }

            @Override
            public boolean equals(Object o){
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;
                GetLessonsForGroupWithTeacherArgs that = (GetLessonsForGroupWithTeacherArgs) o;
                return that.date == this.date && that.group.equals(this.group) && that.teacher.equals(this.teacher);
            }

            @Override
            public int hashCode(){
                return Objects.hash(date, teacher);
            }
        }

        public static BehaviorSubject<List<Lesson>> cacheQuery(Calendar date, String group, String teacher){
            if(!cachedResults.containsKey(new GetLessonsForGroupWithTeacherArgs(date, group, teacher)))
                cachedResults.put(new GetLessonsForGroupWithTeacherArgs(date, group, teacher), BehaviorSubject.create());
            return cachedResults.get(new GetLessonsForGroupWithTeacherArgs(date, group, teacher));
        }
    }
}