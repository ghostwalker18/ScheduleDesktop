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
import java.lang.reflect.Field;
import java.util.*;

/**
 * Этот класс представляет собой реализацию интерфейса LessonDAO на основе Hibernate.
 *
 * @author Ипатов Никита
 */
public class LessonDaoHibernateImpl
        implements LessonDao {
    private AppDatabaseHibernateImpl db;
    private final BehaviorSubject<List<String>> getTeachersResult = BehaviorSubject.create();
    private final BehaviorSubject<List<String>> getGroupsResult = BehaviorSubject.create();

    public LessonDaoHibernateImpl(AppDatabaseHibernateImpl db){
        this.db = db;
        //Trigger for database update
        //If was modified: redo all cashed queries
        db.getInvalidationTracker().subscribe(e -> {
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
        BehaviorSubject<List<Lesson>>  queryResult = GetLessonsForGroupWithTeacherQuery.cacheQuery(date, group, teacher);
        String hql = "from Lesson where groupName = :groupName and teacher like :teacherName and date = :date order by lessonTimes";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("groupName", group);
                query.setParameter("teacherName", "%" + teacher + "%");
                List<Lesson> lessons = query.list();
                queryResult.onNext(query.list());
            }
        });
        return queryResult;
    }

    @Override
    public Observable<List<Lesson>> getLessonsForGroup(Calendar date, String group) {
        BehaviorSubject<List<Lesson>>  queryResult = GetLessonsForGroupQuery.cacheQuery(date, group);

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
        BehaviorSubject<List<Lesson>> queryResult = GetLessonsForTeacherQuery.cacheQuery(date, teacher);
        String hql = "from Lesson where teacher like :teacherName and date = :date order by lessonTimes";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Lesson> query = session.createQuery(hql, Lesson.class);
                query.setParameter("date", date);
                query.setParameter("teacherName", "%" + teacher + "%");
                List<Lesson> lessons = query.list();
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
    private static class GetLessonsForGroupQuery{
        public static final Map<GetLessonsForGroupArgs, BehaviorSubject<List<Lesson>>> cachedResults = new HashMap<>();

        private static class GetLessonsForGroupArgs extends QueryArgs{
            public final Calendar date;
            public final String group;

            GetLessonsForGroupArgs(Calendar date, String group){
                this.date = date;
                this.group = group;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetLessonsForGroupArgs>t_equals(o);
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

        private static class GetLessonsForTeacherArgs extends QueryArgs{
            public final Calendar date;
            public final String teacher;

            GetLessonsForTeacherArgs(Calendar date, String teacher){
                this.date = date;
                this.teacher = teacher;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetLessonsForTeacherArgs>t_equals(o);
            }
        }

        public static BehaviorSubject<List<Lesson>> cacheQuery(Calendar date, String teacher){
            if(!cachedResults.containsKey(new GetLessonsForTeacherArgs(date, teacher)))
                cachedResults.put(new GetLessonsForTeacherArgs(date, teacher), BehaviorSubject.create());
            return cachedResults.get(new GetLessonsForTeacherArgs(date, teacher));
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetLessonsForGroupWithTeacher
     */
    private static class GetLessonsForGroupWithTeacherQuery{
        public static final Map<GetLessonsForGroupWithTeacherArgs,
                BehaviorSubject<List<Lesson>>> cachedResults = new HashMap<>();

        private static class GetLessonsForGroupWithTeacherArgs extends QueryArgs{
            public final Calendar date;
            public final String group;
            public final String teacher;

            GetLessonsForGroupWithTeacherArgs(Calendar date, String group, String teacher){
                this.date = date;
                this.group = group;
                this.teacher = teacher;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetLessonsForGroupWithTeacherArgs>t_equals(o);
            }
        }

        public static BehaviorSubject<List<Lesson>> cacheQuery(Calendar date, String group, String teacher){
            if(!cachedResults.containsKey(new GetLessonsForGroupWithTeacherArgs(date, group, teacher)))
                cachedResults.put(new GetLessonsForGroupWithTeacherArgs(date, group, teacher), BehaviorSubject.create());
            return cachedResults.get(new GetLessonsForGroupWithTeacherArgs(date, group, teacher));
        }
    }

    private abstract static class QueryArgs{
        private Object getFieldValue(Field field) {
            try{
                return field.get(this);
            } catch (Exception e){
                return null;
            }
        }

        protected  <T extends QueryArgs> boolean t_equals(Object o){
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            T that = (T) o;
            boolean res = true;
            Field[] fieldsThat = that.getClass().getFields();
            Field[] fieldsThis = this.getClass().getFields();
            for(int i = 0; i < fieldsThis.length; i++){
                res &= getFieldValue(fieldsThis[i]).equals(getFieldValue(fieldsThat[i]));
            }
            return res;
        }

        @Override
        public int hashCode(){
            Object[] fieldValues = Arrays.stream(this.getClass().getFields()).map(field -> getFieldValue(field)).toArray();
            return Objects.hash(fieldValues);
        }
    }
}