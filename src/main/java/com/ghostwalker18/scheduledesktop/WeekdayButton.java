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
public class WeekdayButton
        extends JPanel
        implements Observer {
    private static final HashMap<String, Integer> weekdaysNumbers = new HashMap<>();
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private static final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());

    static {
        weekdaysNumbers.put(strings.getString("monday"), Calendar.MONDAY);
        weekdaysNumbers.put(strings.getString("tuesday"), Calendar.TUESDAY);
        weekdaysNumbers.put(strings.getString("wednesday"), Calendar.WEDNESDAY);
        weekdaysNumbers.put(strings.getString("thursday"), Calendar.THURSDAY);
        weekdaysNumbers.put(strings.getString("friday"), Calendar.FRIDAY);
    }

    private boolean isOpened = false;
    private final JPanel tablePanel = new JPanel();
    private  final JButton button = new JButton();
    private final transient ScheduleRepository repository = ScheduleRepository.getRepository();

    private final String[] tableColumnNames = new String[]{
            platformStrings.getString("availability_column"),
            strings.getString("number"), strings.getString("times"), strings.getString("subject"),
            strings.getString("teacher"), strings.getString("room")
    };
    private final MultilineTableCellRenderer renderer = new MultilineTableCellRenderer();
    private final JTable table = new JTable(0,5){
        @Override
        public Class<?> getColumnClass(int column) {
            if(column == 0)
                return ImageIcon.class;
            return super.getColumnClass(column);
        }
    };
    private final String dayOfWeek;
    private String teacher = null;
    private String group = null;
    private Calendar date;
    private transient List<Lesson> lessons;

    public WeekdayButton(int year, int week, String dayOfWeek) {
        super();
        this.dayOfWeek = dayOfWeek;
        date = new Calendar.Builder().setWeekDate(year, week, weekdaysNumbers.get(dayOfWeek)).build();
        if(Utils.isDateToday(date)){
            isOpened = true;
        }
        button.setToolTipText(platformStrings.getString("weekday_tooltip"));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridLayout(1,3));
        buttonContainer.add(new JPanel());
        buttonContainer.add(button);
        buttonContainer.add(new JPanel());
        add(buttonContainer);

        table.setFocusable(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JTableHeader tableHeader = table.getTableHeader();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(tableHeader);
        tablePanel.add(table);
        tablePanel.setVisible(isOpened);
        add(tablePanel);

        button.setIcon(new ImageIcon(getClass()
                .getResource("/images/baseline_arrow_drop_down_black_36dp.png")));
        button.setText(generateTitle(date, this.dayOfWeek));
        button.addActionListener(e -> setTableVisible());

        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    setTableVisible();
                }
            }
        });

        repository.getSchedule(date, teacher, group).subscribe(lessonsList -> {
            lessons = lessonsList;
            updateTableGUI(lessons);
        });
    }

    /**
     * Этот метод используется для отображения и скрытия таблицы расписания.
     */
    private void setTableVisible(){
        isOpened = !isOpened;
        tablePanel.setVisible(isOpened);
        if(isOpened){
            button.setIcon(new ImageIcon(getClass()
                    .getResource("/images/baseline_arrow_drop_up_black_36dp.png")));
        }
        else{
            button.setIcon(new ImageIcon(getClass()
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
        table.setModel(makeDataModel(lessons));
        table.getColumnModel().getColumn(3).setCellRenderer(renderer);
        table.getColumn(0).setMaxWidth(100);
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
     * Этот метод позволяет узнать, открыто ли расписание для промотра.
     * @return открыто ли расписание
     */
    public boolean isOpened(){
        return isOpened;
    }

    /**
     * Этот метод позволяет реагировать на изменения состояния расписания.
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */
    @Override
    public void update(Observable o, Object arg) {
        ScheduleState state = (ScheduleState)o;
        date = new Calendar.Builder().setWeekDate(state.getYear(),
                state.getWeek(),
                weekdaysNumbers.get(dayOfWeek))
                .build();
        teacher = state.getTeacher();
        group = state.getGroup();
        button.setText(generateTitle(date, this.dayOfWeek));
        repository.getSchedule(date, teacher, group).subscribe(lessonsList -> {
            lessons = lessonsList;
            updateTableGUI(lessonsList);
        });
    }
}