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

package com.ghostwalker18.scheduledesktop.viewmodels;

import com.ghostwalker18.scheduledesktop.common.ViewModel;
import java.util.Calendar;
import java.util.Date;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.core.Observable;

/**
 * Этот класс используется для отслеживания изменения состояния расписания.
 *
 * @author  Ипатов Никита
 * @since 1.0
 * @version 2.0
 */
public class ScheduleModel
        extends ViewModel {
    private final BehaviorSubject<String> group = BehaviorSubject.create();
    private final BehaviorSubject<String> teacher = BehaviorSubject.create();
    private final BehaviorSubject<Calendar> calendar = BehaviorSubject.createDefault(
            new Calendar.Builder()
                .setInstant(new Date())
                .build());

    /**
     * Этот метод позволяет передвинуть состояние расписания на следующую неделю.
     */
    public void goNextWeek(){
        Calendar date = calendar.getValue();
        date.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.onNext(date);
    }

    /**
     * Этот метод позволяет передвинуть состояние расписания на предыдущую неделю.
     */
    public void goPreviousWeek(){
        Calendar date = calendar.getValue();
        date.add(Calendar.WEEK_OF_YEAR, -1);
        calendar.onNext(date);
    }

    /**
     * Этот метод позваляет передвинуть состояние расписания к выбранной дате.
     * @param date дата для отображения расписания
     */
    public void goToDate(Calendar date){
        calendar.onNext(date);
    }

    public Observable<Calendar> getCalendar(){
        return calendar;
    }

    public int getYear(){
        return calendar.getValue().get(Calendar.YEAR);
    }

    public int getWeek(){
        return calendar.getValue().get(Calendar.WEEK_OF_YEAR);
    }

    public void setGroup(String group){
        try{
            this.group.onNext(group);
        } catch (Exception ignored){/*Not required*/}
    }

    public Observable<String> getGroup(){
        return group;
    }

    public void setTeacher(String teacher){
        try{
            this.teacher.onNext(teacher);
        } catch (Exception ignored){/*Not required*/}
    }

    public Observable<String> getTeacher(){
        return teacher;
    }
}