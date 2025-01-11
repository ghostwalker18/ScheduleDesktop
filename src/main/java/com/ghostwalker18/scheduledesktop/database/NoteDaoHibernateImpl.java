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

import com.ghostwalker18.scheduledesktop.models.Note;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Этот класс представляет собой реализацию интерфейса NoteDAO на основе Hibernate.
 *
 * @author Ипатов Никита
 */
public class NoteDaoHibernateImpl
        implements NoteDao {
    private final AppDatabaseHibernateImpl db;
    private final GetNoteQuery getNoteCache = new GetNoteQuery();
    private final GetNotesQuery getNotesCache = new GetNotesQuery();
    private final GetNotesForDatesQuery getNotesForDatesCache = new GetNotesForDatesQuery();
    private final GetNotesByKeywordQuery getNotesByKeywordCache = new GetNotesByKeywordQuery();

    public NoteDaoHibernateImpl(AppDatabaseHibernateImpl db){
        this.db = db;
        this.db.getInvalidationTracker().subscribe(changed -> {
            if(changed){
                for(GetNoteQuery.GetNoteArgs args : getNoteCache.getCache().keySet()){
                    getNote(args.id);
                }
                for(GetNotesQuery.GetNotesArgs args : getNotesCache.getCache().keySet()){
                    getNotes(args.date, args.group);
                }
                for(GetNotesForDatesQuery.GetNotesForDatesArgs args : getNotesForDatesCache.getCache().keySet()){
                    getNotesForDays(args.dates, args.group);
                }
                for(GetNotesByKeywordQuery.GetNotesByKeywordArgs args : getNotesByKeywordCache.getCache().keySet()){
                    getNotesByKeyword(args.keyword, args.group);
                }
            }
        });
    }

    @Override
    public Observable<Note> getNote(Integer id) {
        BehaviorSubject<Note> queryResult = getNoteCache.cacheQuery(GetNoteQuery.GetNoteArgs.class, id);
        String hql = "from Note where id = :id";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Note> query = session.createQuery(hql, Note.class);
                query.setParameter("id", id);
                queryResult.onNext(query.list().get(0));
            }
        });
        return queryResult;
    }

    @Override
    public Observable<List<Note>> getNotes(Calendar date, String group) {
        BehaviorSubject<List<Note>> queryResult = getNotesCache.cacheQuery(
                GetNotesQuery.GetNotesArgs.class, date, group
        );
        String hql = "from Note where date = :date and group = :group";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Note> query = session.createQuery(hql, Note.class);
                query.setParameter("date", date);
                query.setParameter("group", group);
                queryResult.onNext(query.list());
            }
        });
        return queryResult;
    }

    @Override
    public Observable<List<Note>> getNotesForDays(Calendar[] dates, String group) {
        BehaviorSubject<List<Note>> queryResult = getNotesForDatesCache.cacheQuery(
                GetNotesForDatesQuery.GetNotesForDatesArgs.class, dates, group
        );
        String hql = "from Note where date in (:dates) order by noteDate";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Note> query = session.createQuery(hql, Note.class);
                query.setParameterList("dates", Arrays.asList(dates));
                query.setParameter("group", group);
                queryResult.onNext(query.list());
            }
        });
        return queryResult;
    }

    @Override
    public Observable<List<Note>> getNotesByKeyword(String keyword, String group) {
        BehaviorSubject<List<Note>> queryResult = getNotesByKeywordCache.cacheQuery(
                GetNotesByKeywordQuery.GetNotesByKeywordArgs.class, keyword, group
        );
        String hql = "from Note where (text like :keyword or theme like :keyword) and group = :group" +
                "order by noteDate desc";
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Query<Note> query = session.createQuery(hql, Note.class);
                query.setParameter("keyword", "%" + keyword + "%");
                query.setParameter("group", group);
                queryResult.onNext(query.list());
            }
        });
        return queryResult;
    }

    @Override
    public void insert(Note note) {
        db.runQuery(() -> {
            try (Session session = db.getSessionFactory().openSession()) {
                Transaction transaction = session.getTransaction();
                transaction.begin();
                transaction.commit();
                db.getInvalidationTracker().onNext(true);
            }
        });
    }

    @Override
    public void update(Note note) {
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Transaction transaction = session.getTransaction();
                transaction.begin();
                session.update(note);
                transaction.commit();
                db.getInvalidationTracker().onNext(true);
            }
        });
    }

    @Override
    public void delete(Note note) {
        db.runQuery(()->{
            try(Session session = db.getSessionFactory().openSession()){
                Transaction transaction = session.getTransaction();
                transaction.begin();
                session.remove(note);
                transaction.commit();
                db.getInvalidationTracker().onNext(true);
            }
        });
    }

    /**
     * Этот класс используется для кэширования запросов GetNote
     */
    private static class GetNoteQuery
            extends QueryCache<GetNoteQuery.GetNoteArgs, Note>{
        public static class GetNoteArgs extends QueryCache.QueryArgs{
            public final Integer id;

            public GetNoteArgs(Integer id){
                this.id = id;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetNoteArgs>t_equals(o);
            }
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetNotes
     */
    private static class GetNotesQuery
            extends QueryCache<GetNotesQuery.GetNotesArgs, List<Note>>{
        public static class GetNotesArgs extends QueryCache.QueryArgs{
            public final Calendar date;
            public final String group;

            public GetNotesArgs(GregorianCalendar date, String group){
                this.date = date;
                this.group = group;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetNotesArgs>t_equals(o);
            }
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetNotesForDates
     */
    private static class GetNotesForDatesQuery
            extends QueryCache<GetNotesForDatesQuery.GetNotesForDatesArgs, List<Note>>{
        public static class GetNotesForDatesArgs extends QueryCache.QueryArgs{
            public final Calendar[] dates;
            public final String group;

            public GetNotesForDatesArgs(Calendar[] dates, String group){
                this.dates = dates;
                this.group = group;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetNotesForDatesArgs>t_equals(o);
            }
        }
    }

    /**
     * Этот класс используется для кэширования запросов GetNotesByKeyword
     */
    private static class GetNotesByKeywordQuery
            extends QueryCache<GetNotesByKeywordQuery.GetNotesByKeywordArgs, List<Note>>{
        public static class GetNotesByKeywordArgs extends QueryCache.QueryArgs{
            public final String keyword;
            public final String group;

            public GetNotesByKeywordArgs(String keyword, String group){
                this.keyword = keyword;
                this.group = group;
            }

            @Override
            public boolean equals(Object o){
                return super.<GetNotesByKeywordArgs>t_equals(o);
            }
        }
    }
}