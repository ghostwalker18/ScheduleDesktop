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

package com.ghostwalker18.scheduledesktop.views;

import com.ghostwalker18.scheduledesktop.*;
import com.ghostwalker18.scheduledesktop.common.Form;
import com.ghostwalker18.scheduledesktop.common.Fragment;
import com.ghostwalker18.scheduledesktop.common.ViewModelProvider;
import com.ghostwalker18.scheduledesktop.converters.DateConverters;
import com.ghostwalker18.scheduledesktop.models.Lesson;
import com.ghostwalker18.scheduledesktop.system.MultilineTableCellRenderer;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.utils.Utils;
import com.ghostwalker18.scheduledesktop.viewmodels.DayModel;
import com.ghostwalker18.scheduledesktop.viewmodels.ScheduleModel;
import com.ghostwalker18.scheduledesktop.common.Bundle;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * Этот класс предсавляет собой кастомный элемент GUI,
 * используемый для отображения расписания на день.
 *
 * @author Ипатов Никита
 */
public class ScheduleItemFragment
        extends Fragment {

    private static final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private static final HashMap<String, Integer> weekdaysNumbers = new HashMap<>();
    static {
        weekdaysNumbers.put(strings.getString("monday"), Calendar.MONDAY);
        weekdaysNumbers.put(strings.getString("tuesday"), Calendar.TUESDAY);
        weekdaysNumbers.put(strings.getString("wednesday"), Calendar.WEDNESDAY);
        weekdaysNumbers.put(strings.getString("thursday"), Calendar.THURSDAY);
        weekdaysNumbers.put(strings.getString("friday"), Calendar.FRIDAY);
    }

    private boolean isOpened = false;
    private JPanel tablePanel;
    private JTable table;
    private JButton scheduleButton;
    private JButton notesButton;
    private final String[] tableColumnNames = new String[]{
            platformStrings.getString("availability_column"),
            strings.getString("number"), strings.getString("times"), strings.getString("subject"),
            strings.getString("teacher"), strings.getString("room")
    };
    private final MultilineTableCellRenderer renderer = new MultilineTableCellRenderer();
    private String dayOfWeek;
    private String group = null;
    private Calendar date;
    private transient List<Lesson> lessons;
    private ScheduleModel scheduleModel;
    private DayModel dayModel;

    public ScheduleItemFragment(Form form) {
        super(form);
    }

    /**
     * Этот метод используется для отображения и скрытия таблицы расписания.
     */
    private void setTableVisible(){
        isOpened = !isOpened;
        tablePanel.setVisible(isOpened);
        if(isOpened){
            scheduleButton.setIcon(new ImageIcon(getClass()
                    .getResource("/images/baseline_arrow_drop_up_black_36dp.png")));
        }
        else{
            scheduleButton.setIcon(new ImageIcon(getClass()
                    .getResource("/images/baseline_arrow_drop_down_black_36dp.png")));
        }
    }

    /**
     * Этот метод генерирует заголовок для этого элемента
     * @param date дата расписания
     * @param dayOfWeek день недели
     * @return заголовок
     */
    private String generateTitle(Calendar date,   String dayOfWeek){
        String label = dayOfWeek + " (" + Utils.generateDateForTitle(date) + ")";
        if(Utils.isDateToday(date)){
            label = label + " - " + strings.getString("today");
        }
        return label;
    }

    /**
     * Этот метод использутся для обновления GUI таблицы расписания на UI-потоке.
     * @param lessons занятия для заполнения таблицы
     */
    private void updateTableGUI(List<Lesson> lessons){
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateTableGUI(lessons));
            return;
        }
        try{
            table.setModel(makeDataModel(lessons));
            table.getColumnModel().getColumn(3).setCellRenderer(renderer);
            table.getColumn(0).setMaxWidth(100);
        } catch (Exception ignored){/*Not required*/}
    }

    /**
     * Этот метод преобразует занятия в таблицу.
     * @param lessons занятия
     * @return модель таблицы
     */
    private DefaultTableModel makeDataModel(List<Lesson> lessons){
        DefaultTableModel tableModel = new DefaultTableModel(tableColumnNames, 0);
        if(lessons != null){
            for(Lesson lesson : lessons){
                ImageIcon availabilityIcon = null;
                if(Utils.isLessonAvailable(lesson.getDate(), lesson.getTimes()) != null)
                    switch (Utils.isLessonAvailable(lesson.getDate(), lesson.getTimes())){
                        case ENDED:
                            availabilityIcon = new ImageIcon(getClass()
                                    .getResource("/images/event_busy_24dp.png"));
                            break;
                        case STARTED:
                            availabilityIcon = new ImageIcon(getClass()
                                    .getResource("/images/schedule_24dp.png"));
                            break;
                        case NOT_STARTED:
                            availabilityIcon = new ImageIcon(getClass()
                                    .getResource("/images/event_available_24dp.png"));
                            break;
                    }
                tableModel.addRow(new Object[]{
                        availabilityIcon,
                        lesson.getLessonNumber(),
                        lesson.getTimes(),
                        lesson.getSubject(),
                        lesson.getTeacher(),
                        lesson.getRoomNumber()});
            }
        }
        return tableModel;
    }

    /**
     * Этот метод позвоволяет получить расписание для этого элемента в виде
     * форматированной строки.
     *
     * @return расписание на этот день
     */
    public String getSchedule(){
        DateConverters converter = new DateConverters();

        StringBuilder schedule = new StringBuilder(strings.getString("date") + ": ");
        schedule.append(converter.convertToDatabaseColumn(date)).append("\n");
        schedule.append("\n");

        for(Lesson lesson : lessons){
            schedule.append(strings.getString("number")).append(": ");
            schedule.append(lesson.getLessonNumber()).append("\n");

            schedule.append(strings.getString("subject")).append(": ");
            schedule.append(lesson.getSubject()).append("\n");

            if(!lesson.getTeacher().equals("")){
                schedule.append(strings.getString("teacher")).append(": ");
                schedule.append(lesson.getTeacher()).append("\n");
            }

            if(!lesson.getRoomNumber().equals("")){
                schedule.append(strings.getString("room")).append(": ");
                schedule.append(lesson.getRoomNumber()).append("\n");
            }

            schedule.append("\n");
        }
        schedule.append("\n");

        return schedule.toString();
    }

    /**
     * Этот метод окрывает экран с заметками для этого дня.
     */
    private void openNotesActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("group", group);
        bundle.putString("date", new DateConverters().convertToDatabaseColumn(date));
        ScheduleApp.getInstance().startActivity(NotesForm.class, bundle);
    }

    /**
     * Этот метод позволяет узнать, открыто ли расписание для промотра.
     * @return открыто ли расписание
     */
    public boolean isOpened(){
        return isOpened;
    }

    @Override
    public void onCreate(Bundle bundle) {
        dayOfWeek = bundle.getString("dayOfWeek");
    }

    @Override
    public void onCreatedUI() {
        scheduleButton.addActionListener(e -> setTableVisible());
        scheduleButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    setTableVisible();
                }
            }
        });
        notesButton.addActionListener(e -> openNotesActivity());
        notesButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    openNotesActivity();
                }
            }
        });

        scheduleModel = new ViewModelProvider(getParentForm()).get(ScheduleModel.class);
        dayModel = new ViewModelProvider(this).get(DayModel.class);

        scheduleModel.getCalendar().subscribe(calendar -> {
            dayModel.setDate(new Calendar.Builder()
                    .setWeekDate(scheduleModel.getYear(),
                            scheduleModel.getWeek(),
                            weekdaysNumbers.get(dayOfWeek))
                    .build());
        });
        scheduleModel.getGroup().subscribe(group-> {
            this.group = group;
            dayModel.setGroup(group);
        });
        scheduleModel.getTeacher().subscribe(teacher -> {
            dayModel.setTeacher(teacher);
        });

        dayModel.getLessons().subscribe(lessons -> {
            this.lessons = lessons;
            this.updateTableGUI(lessons);
        });
        dayModel.getDate().subscribe(date -> {
            this.date = date;
            scheduleButton.setText(generateTitle(date, this.dayOfWeek));
            if(Utils.isDateToday(date)){
                setTableVisible();
            }
        });
    }

    @Override
    public void onSetupLanguage(){
        scheduleButton.setToolTipText(platformStrings.getString("weekday_tooltip"));
        notesButton.setToolTipText(platformStrings.getString("show_notes_tooltip"));
    }

    @Override
    public void onCreateUI() {
        scheduleButton = new JButton();
        scheduleButton.setIcon(new ImageIcon(getClass()
                .getResource("/images/baseline_arrow_drop_down_black_36dp.png")));

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridLayout(1,3));
        buttonContainer.add(new JPanel());
        buttonContainer.add(scheduleButton);
        buttonContainer.add(new JPanel());
        add(buttonContainer);
        table = new JTable(0,5){
            @Override
            public Class<?> getColumnClass(int column) {
                if(column == 0)
                    return ImageIcon.class;
                return super.getColumnClass(column);
            }
        };

        table.setFocusable(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JTableHeader tableHeader = table.getTableHeader();
        tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(tableHeader);
        tablePanel.add(table);

        notesButton = new JButton();
        notesButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_notes_36.png")));
        notesButton.setBackground(null);
        notesButton.setBorder(null);
        tablePanel.add(notesButton);

        tablePanel.setVisible(isOpened);
        add(tablePanel);
    }
}