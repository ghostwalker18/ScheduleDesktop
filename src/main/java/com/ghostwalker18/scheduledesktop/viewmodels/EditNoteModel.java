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
import com.ghostwalker18.scheduledesktop.models.Note;
import com.ghostwalker18.scheduledesktop.models.NotesRepository;
import com.ghostwalker18.scheduledesktop.models.ScheduleRepository;
import com.ghostwalker18.scheduledesktop.views.EditNoteForm;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import java.util.Calendar;
import java.util.List;

/**
 * Этот класс используется для отслеживания изменений состояния редактируемой заметки.
 *
 * @author Ипатов Никита
 * @see EditNoteForm
 * @see NotesRepository
 * @see ScheduleRepository
 */
public class EditNoteModel extends ViewModel {
    private final ScheduleRepository scheduleRepository = ScheduleApp.getInstance().getScheduleRepository();
    private final NotesRepository notesRepository = ScheduleApp.getInstance().getNotesRepository();
    private final BehaviorSubject<Note> note = BehaviorSubject.createDefault(new Note());
    private final BehaviorSubject<List<String>> noteThemesMediator = BehaviorSubject.create();
    private Observable<List<String>> themes;
    private Disposable themesWatch;
    private final BehaviorSubject<String> theme = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> text = BehaviorSubject.createDefault("");
    private final BehaviorSubject<Calendar> date = BehaviorSubject.createDefault(Calendar.getInstance());
    private final BehaviorSubject<String> group = BehaviorSubject.createDefault(scheduleRepository.getSavedGroup());
    private boolean isEdited = false;

    public EditNoteModel(){
        themes = scheduleRepository.getSubjects(scheduleRepository.getSavedGroup());
        themesWatch = themes.subscribe(noteThemesMediator::onNext);
    }

    /**
     * Этот метод позволяет задать id заметки для редактирования.
     * @param id идентификатор
     */
    public void setNoteID(Integer id){
        isEdited = true;
        note.subscribe((Observer<? super Note>) notesRepository.getNote(id));
        note.subscribe(note1 -> {
            if(note1 != null){
                group.onNext(note1.group);
                date.onNext(note1.date);
                text.onNext(note1.text);
                theme.onNext(note1.theme);
            }
        });
    }

    /**
     * Этот метод позволяет задать группу для заметки.
     * @param group группа
     */
    public void setGroup(String group){
        this.group.onNext(group);
        themesWatch.dispose();
        themes = scheduleRepository.getSubjects(group);
        themesWatch = themes.subscribe(noteThemesMediator::onNext);
    }

    /**
     * Этот метод позволяет получить группу заметки.
     * @return название группы
     */
    public Observable<String> getGroup(){
        return group;
    }

    /**
     * Этот метод позволяет получить возможные группы для заметки.
     * @return список допустимых групп
     */
    public Observable<List<String>> getGroups(){
        return scheduleRepository.getGroups();
    }

    /**
     * Этот метод позволяет задать текст заметки.
     *
     * @param text текст
     */
    public void setText(String text){
        this.text.onNext(text);
    }

    /**
     * Этот метод позволяет получить текст заметки.
     *
     * @return текст
     */
    public Observable<String> getText(){
        return text;
    }

    /**
     * Этот метод позволяет задать тему заметки.
     *
     * @param theme тема
     */
    public void setTheme(String theme){
        this.theme.onNext(theme);
    }

    /**
     * Этот метод позволяет получить тему заметки.
     * @return тема
     */
    public Observable<String> getTheme(){
        return theme;
    }

    /**
     * Этот метод позволяет получить список предметов у данной группы в качестве тем.
     * @return список предлаагаемых тем
     */
    public Observable<List<String>> getThemes(){
        return noteThemesMediator;
    }

    /**
     * Этот метод позволяет получить текущую дату редактируемой заметки.
     *
     * @return дата
     */
    public Observable<Calendar> getDate(){
        return date;
    }

    /**
     * Этот метод позволяет установить дату редактируемой заметки.
     *
     * @param date дата
     */
    public void setDate(Calendar date) {
        this.date.onNext(date);
    }

    /**
     * Этот метод позволяет сохранить заметку.
     */
    public void saveNote(){
        Note noteToSave = note.getValue();
        if(noteToSave != null){
            noteToSave.date = date.getValue();
            noteToSave.group = group.getValue();
            noteToSave.theme = theme.getValue();
            noteToSave.text = text.getValue();
            noteToSave.photoIDs = null;
            if(isEdited)
                notesRepository.updateNote(noteToSave);
            else
                notesRepository.saveNote(noteToSave);
        }
    }
}