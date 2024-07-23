package com.ghostwalker18.scheduledesktop;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@IdClass(Lesson.LessonPK.class)
@Table(name="tblSchedule")
public class Lesson {
    @Id
    @Convert(converter = DateConverters.class)
    @Column(name="date")
    private Calendar date;
    @Id
    @Column(name="lessonNumber")
    private String lessonNumber;
    @Column(name="roomNumber")
    private String roomNumber;
    @Column(name="lessonTimes")
    private String times;
    @Id
    @Column(name="groupName")
    @Nationalized
    private String group;
    @Id
    @Column(name="subjectName")
    @Nationalized
    private String subject;
    @Column(name="teacherName")
    @Nationalized
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
        this.group = group;
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
        return group;
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
        this.group = group;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public static class LessonPK implements Serializable {
        protected Calendar date;
        protected String lessonNumber;
        protected String group;
        protected String subject;

        public LessonPK(){};

        public LessonPK(Calendar date, String lessonNumber, String group, String subject){
            this.date = date;
            this.lessonNumber = lessonNumber;
            this.group = group;
            this.subject = subject;
        }
    }
}