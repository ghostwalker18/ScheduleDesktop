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

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

/**
 * Этот класс используется для отслеживания изменения состояния расписания.
 *
 * @author  Ипатов Никита
 */
public class ScheduleState extends Observable {
    private String group;
    private String teacher;
    private int year;
    private int week;
    private Calendar calendar;

    /**
     * Конструктор состояния на основе текущей даты.
     * @param currentDate текущая дата
     */
    public ScheduleState(Date currentDate){
        calendar = new Calendar.Builder().setInstant(currentDate).build();
        year = calendar.get(Calendar.YEAR);
        week = calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Этот метод позволяет передвинуть состояние расписания на следующую неделю.
     */
    public void goNextWeek(){
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        year = calendar.get(Calendar.YEAR);
        week = calendar.get(Calendar.WEEK_OF_YEAR);
        setChanged();
        notifyObservers();
    }

    /**
     * Этот метод позволяет передвинуть состояние расписания на предыдущую неделю.
     */
    public void goPreviousWeek(){
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        year = calendar.get(Calendar.YEAR);
        week = calendar.get(Calendar.WEEK_OF_YEAR);
        setChanged();
        notifyObservers();
    }

    public int getYear(){
        return year;
    }

    public int getWeek(){
        return week;
    }

    public void setGroup(String group){
        this.group = group;
        setChanged();
        notifyObservers();
    };

    public String getGroup(){
        return group;
    }

    public void setTeacher(String teacher){
        this.teacher = teacher;
        setChanged();
        notifyObservers();
    }

    public String getTeacher(){
        return teacher;
    }
}