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

package com.ghostwalker18.scheduledesktop.models;

import com.ghostwalker18.scheduledesktop.converters.DateConverters;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * POJO-класс для описания единичной сущности расписания - урока.
 * Используется в ORM.
 * Содержит поля для даты, порядкового номера, номера(названия) кабинета,
 * времени проведения, группы, преподавателя, предмета.
 *
 * @author  Ипатов Никита
 */

@Entity
@IdClass(Lesson.LessonPK.class)
@Table(name="tblSchedule")
public class Lesson {
    @Id
    private Calendar date;
    @Id
    private String lessonNumber;
    @Id
    private String groupName;
    @Id
    private String subject;
    @Column(name="roomNumber")
    private String roomNumber;
    @Column(name="lessonTimes")
    private String times;
    @Column(name="teacherName")
    private String teacher;

    public Lesson() {
        date = Calendar.getInstance();
    }

    public Lesson( Calendar date, String lessonNumber, String roomNumber, String times,
                   String group, String subject, String teacher) {
        this.date = date;
        this.lessonNumber = lessonNumber;
        this.roomNumber = roomNumber;
        this.times = times;
        this.groupName = group;
        this.subject = subject;
        this.teacher = teacher;
    }

    public Calendar getDate() {
        return date;
    }

    public String getLessonNumber() {
        return lessonNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTimes() {
        return times;
    }

    public String getGroup() {
        return groupName;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setLessonNumber(String lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setGroup(String group) {
        this.groupName = group;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    /**
     * Вспомогательный класс для задания первичного ключа для ORM.
     */
    public static class LessonPK
            implements Serializable {
        @Convert(converter = DateConverters.class)
        @Column(name="lessonDate")
        protected Calendar date;
        @Column(name="lessonNumber")
        protected String lessonNumber;
        @Column(name="groupName")
        protected String groupName;
        @Column(name="subjectName")
        protected String subject;

        public LessonPK(){}

        public LessonPK(Calendar date, String lessonNumber, String groupName, String subject){
            this.date = date;
            this.lessonNumber = lessonNumber;
            this.groupName = groupName;
            this.subject = subject;
        }

        public String getLessonNumber(){
            return lessonNumber;
        }

        public String getGroupName(){
            return groupName;
        }

        public String getSubject(){
            return subject;
        }

        public Calendar getDate(){
            return date;
        }

        public void setLessonNumber(String lessonNumber) {
            this.lessonNumber = lessonNumber;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public void setDate(Calendar date) {
            this.date = date;
        }

        @Override
        public boolean equals(Object o){
            if(this == o)
                return true;
            if(o == null || o.getClass() != this.getClass())
                return false;
            LessonPK that = (LessonPK) o;
            return this.date == that.date
                    && Objects.equals(this.lessonNumber, that.lessonNumber)
                    && Objects.equals(this.subject, that.subject)
                    && Objects.equals(this.groupName, that.groupName);
        }

        @Override
        public int hashCode(){
            return Objects.hash(date, lessonNumber, subject, groupName);
        }
    }
}