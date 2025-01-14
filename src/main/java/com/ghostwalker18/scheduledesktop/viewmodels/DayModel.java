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

import com.ghostwalker18.scheduledesktop.ScheduleApp;
import com.ghostwalker18.scheduledesktop.common.ViewModel;
import com.ghostwalker18.scheduledesktop.models.Lesson;
import com.ghostwalker18.scheduledesktop.models.ScheduleRepository;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import java.util.Calendar;
import java.util.List;

/**
 * Этот класс представляет собой модель представления фрагмента распиисания для определенного дня недели.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class DayModel
        extends ViewModel {
    private final ScheduleRepository repository = ScheduleApp.getInstance().getScheduleRepository();
    private final BehaviorSubject<Calendar> dateFlow = BehaviorSubject.create();
    private final BehaviorSubject<List<Lesson>> lessons = BehaviorSubject.create();
    private Observable<List<Lesson>> lessonsMediator;
    private Disposable lessonsWatchdog;
    private Calendar date;
    private String group;
    private String teacher;

    public Observable<Calendar> getDate(){
        return dateFlow;
    }

    public void setDate(Calendar date){
        dateFlow.onNext(date);
        this.date = date;
        if(lessonsWatchdog != null)
            lessonsWatchdog.dispose();
        lessonsMediator = repository.getSchedule(date, teacher, group);
        lessonsWatchdog = lessonsMediator.subscribe(lessons::onNext);
    }

    public void setGroup(String group){
        this.group = group;
        if(lessonsWatchdog != null)
            lessonsWatchdog.dispose();
        lessonsMediator = repository.getSchedule(date, teacher, group);
        lessonsWatchdog = lessonsMediator.subscribe(lessons::onNext);
    }

    public void setTeacher(String teacher){
        this.teacher = teacher;
        if(lessonsWatchdog != null)
            lessonsWatchdog.dispose();
        lessonsMediator = repository.getSchedule(date, teacher, group);
        lessonsWatchdog = lessonsMediator.subscribe(lessons::onNext);
    }

    public Observable<List<Lesson>> getLessons(){
        return lessons;
    }
}