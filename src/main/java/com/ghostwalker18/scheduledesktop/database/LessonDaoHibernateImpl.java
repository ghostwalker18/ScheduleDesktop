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
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.*;

/**
 * Этот класс представляет собой реализацию интерфейса LessonDAO на основе Hibernate.
 *
 * @author Ипатов Никита
 */
public class LessonDaoHibernateImpl
        implements LessonDao {
    private final AppDatabaseHibernateImpl db;
    private final BehaviorSubject<List<String>> getTeachersResult = BehaviorSubject.create();
    private final BehaviorSubject<List<String>> getGroupsResult = BehaviorSubject.create();
    private final GetLessonsForGroupQuery GCache = new GetLessonsForGroupQuery();
    private final GetLessonsForTeacherQuery TCache = new GetLessonsForTeacherQuery();
    private final GetLessonsForGroupWithTeacherQuery GTCache = new GetLessonsForGroupWithTeacherQuery();


    public LessonDaoHibernateImpl(AppDatabaseHibernateImpl db){
        this.db = db;
        //Trigger for database update
        //If was modified: redo all cashed queries
        this.db.getInvalidationTracker().subscribe(changed -> {
            if(changed){
                getTeachers();
                getGroups();
                for(GetLessonsForGroupQuery.GetLessonsForGroupArgs args : GCache.getCache().keySet()){
                    getLessonsForGroup(args.date, args.group);
                }
                for(GetLessonsForTeacherQuery.GetLessonsForTeacherArgs args : TCache.getCache().keySet()){
                    getLessonsForTeacher(args.date, args.teacher);
                }
                for(GetLessonsForGroupWithTeacherQuery.GetLessonsForGroupWithTeacherArgs args : GTCache.getCache().keySet()){
                    getLessonsForGroupWithTeacher(args.date, args.group, args.teacher);
                }
            }
        });
    }
    @Override
    public Observable<List<String>> getTeachers() {
        String hql = "select distinct teacher from Lesson order by teacher asc";
        db.runQuery(() -> {
            try(Session session = db.getSessionFactory().openSession()){
                Query<String> query = session.createQuery(hql, String.class);
                getTeachersResult.onNext(query.list());
            }
        });
        return getTeachersResult;
    }

    @Override
    public Observable<List<String>> getGroups() {
        String hql = "select distinct groupName from Lesson order by groupName asc";
        db.runQuery(() -> {
            try(Session session = db.getSessionFactory().openSession()){
                Query<String> query = session.createQuery(hql, String.class);
                getGroupsResult.onNext(query.list());
            }
        });
        return getGroupsResult;
    }

    @Override
    public Observable<List<Lesson>> getLessonsForGroupWithTeacher(Calendar date, String group, String teacher) {
        BehaviorSubject<List<Lesson>>  queryResult = GTCache.cacheQuery(
                GetLessonsForGroupWithTeacherQuery.GetLessonsForGroupWithTeacherArgs.class, date, group, teacher);
        String hql = "from Lesson where groupName = :groupName and teacher like :teacherName and date = :date" +
                "order by lessonTimes";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                query.setParameter("teacherName", "%" + teacher + "%");
                queryResult.onNext(query.list());
            }
        });
        return queryResult;
    }

    @Override
    public Observable<List<Lesson>> getLessonsForGroup(Calendar date, String group) {
        BehaviorSubject<List<Lesson>>  queryResult = GCache.cacheQuery(
                GetLessonsForGroupQuery.GetLessonsForGroupArgs.class, date, group);
        String hql = "from Lesson where groupName = :groupName and date = :date order by lessonTimes";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                queryResult.onNext(query.list());
            }
        });
        return queryResult;
    }

    @Override
    public Observable<List<Lesson>> getLessonsForTeacher(Calendar date, String teacher) {
        BehaviorSubject<List<Lesson>> queryResult = TCache.cacheQuery(
                GetLessonsForTeacherQuery.GetLessonsForTeacherArgs.class, date, teacher);
        String hql = "from Lesson where teacher like :teacherName and date = :date order by lessonTimes";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("teacherName", "%" + teacher + "%");
                queryResult.onNext(query.list());
            }
        });
        return queryResult;
    }

    @Override
    public Observable<List<String>> getSubjectsForGroup(String group) {
        return null;
    }

    @Override
    public void insertMany(List<Lesson> lessons) {
        db.runQuery(() -> {
            try (Session session = db.getSessionFactory().openSession()) {
                Transaction transaction = session.getTransaction();
                transaction.begin();
                int counter = 0;
                for (Lesson lesson : lessons) {
                    counter++;
                    session.merge(lesson);
                    if (counter % 32 == 0) {//same as the JDBC batch size
                        //flush a batch of inserts and release memory:
                        session.flush();
                        session.clear();
                    }
                }
                transaction.commit();
                db.getInvalidationTracker().onNext(true);
            }
        });
    }

    @Override
    public void update(Lesson lesson) {
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Transaction transaction = session.getTransaction();
                transaction.begin();
                session.update(lesson);
                transaction.commit();
                db.getInvalidationTracker().onNext(true);
            }
        });
    }

    /**
     * Этот класс используется для кэширования запросов GetLessonsForGroup
     */
    private static class GetLessonsForGroupQuery
            extends QueryCache<GetLessonsForGroupQuery.GetLessonsForGroupArgs, List<Lesson>>{

        public static class GetLessonsForGroupArgs extends QueryCache.QueryArgs{
            public final Calendar date;
            public final String group;

            public GetLessonsForGroupArgs(GregorianCalendar date, String group){
                this.date = date;
                this.group = group;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetLessonsForGroupArgs>t_equals(o);
            }
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetLessonsForTeacher
     */
    private static class GetLessonsForTeacherQuery
            extends QueryCache<GetLessonsForTeacherQuery.GetLessonsForTeacherArgs, List<Lesson>> {

         public static class GetLessonsForTeacherArgs extends QueryCache.QueryArgs{
            public final Calendar date;
            public final String teacher;

            public GetLessonsForTeacherArgs(GregorianCalendar date, String teacher){
                this.date = date;
                this.teacher = teacher;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetLessonsForTeacherArgs>t_equals(o);
            }
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetLessonsForGroupWithTeacher
     */
    private static class GetLessonsForGroupWithTeacherQuery
            extends QueryCache<GetLessonsForGroupWithTeacherQuery.GetLessonsForGroupWithTeacherArgs, List<Lesson>>{

        public  static class GetLessonsForGroupWithTeacherArgs extends QueryCache.QueryArgs{
            public final Calendar date;
            public final String group;
            public final String teacher;

            public GetLessonsForGroupWithTeacherArgs(GregorianCalendar date, String group, String teacher){
                this.date = date;
                this.group = group;
                this.teacher = teacher;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetLessonsForGroupWithTeacherArgs>t_equals(o);
            }
        }
    }
}