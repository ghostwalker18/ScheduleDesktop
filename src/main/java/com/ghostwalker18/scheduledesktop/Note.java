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

/**
 * Этот класс используется для описания единичной сущности заметок.
 * Используется в ORM.
 * Содержит поля для даты, группы, темы, текста, идентификатора фото.
 *
 * @author  Ипатов Никита
 */
package com.ghostwalker18.scheduledesktop;

import com.sun.jndi.toolkit.url.Uri;
import io.reactivex.rxjava3.annotations.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Calendar;

@Entity
@Table(name="tblNote")
public class Note {
    @NonNull
    public Integer id;
    @Column(name="noteDate")
    @NonNull
    public Calendar date;
    @NonNull
    @Column(name="noteGroup")
    public String group;
    @Column(name="noteTheme")
    public String theme;
    @Column(name="noteText")
    @NonNull
    public String text;
    @Column(name="notePhotoIDs")
    public ArrayList<Uri> photoIDs;
}