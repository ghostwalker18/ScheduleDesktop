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
import io.reactivex.rxjava3.annotations.NonNull;
import javax.persistence.*;
import java.util.Calendar;

/**
 * Этот класс используется для описания единичной сущности заметок.
 * Используется в ORM.
 * Содержит поля для даты, группы, темы, текста, идентификатора фото.
 *
 * @author  Ипатов Никита
 */
@Entity
@Table(name="tblNote")
public class Note {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Integer id;
    @NonNull
    @Convert(converter = DateConverters.class)
    @Column(name="noteDate")
    public Calendar date;
    @NonNull
    @Column(name="noteGroup")
    public String group;
    @Column(name="noteTheme")
    public String theme;
    @NonNull
    @Column(name="noteText")
    public String text;
    @Column(name="notePhotoIDs")
    public String photoIDs;
}