package com.ghostwalker18.scheduledesktop;

import com.sun.istack.NotNull;
import io.reactivex.rxjava3.core.Observable;
import java.util.Calendar;
import java.util.List;

/**
 * Этот интерфейс представляет собой базу данных приложения.
 *
 * @author  Ипатов Никита
 */
public interface IAppDatabase {
    /**
     * Этот метод позволяет вставить элементы Lesson в БД.
     * @param lessons занятия
     */
    void insertMany(List<Lesson> lessons);

    /**
     * Этот метод позволяет обновить элемент Lesson В БД.
     * @param lesson занятие
     */
    void update(Lesson lesson);

    /**
     * Этот метод позволяет получить список учителей из БД.
     * @return списко учителей
     */
    Observable<List<String>> getTeachers();

    /**
     * Этот метод позволяет получить списко групп из БД.
     * @return список групп
     */
    Observable<List<String>> getGroups();

    /**
     * Этот метод позволяет позволяет получить список занятий на заданную дату у заданной группы,
     * которые проводит заданный преподаватель.
     * @param date дата
     * @param group группа
     * @param teacher преподаватель
     * @return список занятий
     */
    Observable<List<Lesson>> getLessonsForGroupWithTeacher(@NotNull Calendar date,
                                                           @NotNull String group,
                                                           @NotNull String teacher);

    /**
     * Этот метод позволяет получить список занятий на заданный день у заданной группы.
     * @param date дата
     * @param group группа
     * @return список занятий
     */
    Observable<List<Lesson>> getLessonsForGroup(@NotNull Calendar date, @NotNull String group);

    /**
     * Этот метод позволяет получить список занятий на заданный день у заданного преподавателя.
     * @param date дата
     * @param teacher преподаватель
     * @return список занятий
     */
    Observable<List<Lesson>> getLessonsForTeacher(@NotNull Calendar date, @NotNull String teacher);

    NoteDao noteDao();
}