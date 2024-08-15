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
public class WeekdayButton extends JPanel implements Observer {
    private static final HashMap<String, Integer> weekdaysNumbers = new HashMap<>();
    private static final String toolTip = "Показать расписание на этот день";
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

    private Theme theme = new DefaulTheme();
    private boolean isOpened = false;
    private final JPanel tablePanel = new JPanel();
    private  final JButton button = new JButton();
    private final ScheduleRepository repository = ScheduleRepository.getRepository();

    private final String[] tableColumnNames = new String[]{
            strings.getString("number"), strings.getString("times"), strings.getString("subject"),
            strings.getString("teacher"), strings.getString("room")
    };
    private final JTable table = new JTable();
    private final String dayOfWeek;
    private String teacher = null;
    private String group = null;
    private Calendar date;
    private List<Lesson> lessons;

    public WeekdayButton(int year, int week, String dayOfWeek) {
        super();
        this.dayOfWeek = dayOfWeek;
        date = new Calendar.Builder().setWeekDate(year, week, weekdaysNumbers.get(dayOfWeek)).build();
        if(isDateToday(date)){
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
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table.setDefaultRenderer(Object.class, centerRenderer);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(theme.getSecondaryColor());
        tableHeader.setForeground(theme.getTextColor());
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(tableHeader);
        tablePanel.add(table);
        tablePanel.setVisible(isOpened);
        add(tablePanel);

        button.setBackground(theme.getPrimaryColor());
        button.setForeground(theme.getTextColor());

        button.setIcon(new ImageIcon(getClass().getResource("/images/chevron-down.gif")));

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
            table.setModel(makeDataModel(lessonsList));
        });
    }

    private void setTableVisible(){
        isOpened = !isOpened;
        tablePanel.setVisible(isOpened);
        if(isOpened){
            button.setIcon(new ImageIcon(getClass().getResource("/images/chevron-up.gif")));
        }
        else{
            button.setIcon(new ImageIcon(getClass().getResource("/images/chevron-down.gif")));
        }
    }

    /**
     * Этот метод генерирует заголовок для этого элемента
     * @param date дата расписания
     * @param dayOfWeek день недели
     * @return заголовок
     */
    private String generateTitle(Calendar date,  String dayOfWeek){
        //Month is a number in 0 - 11
        int month = date.get(Calendar.MONTH) + 1;
        //Formatting month number with leading zero
        String monthString = String.valueOf(month);
        if(month < 10){
            monthString = "0" + monthString;
        }
        int day = date.get(Calendar.DAY_OF_MONTH);
        String dayString = String.valueOf(day);
        //Formatting day number with leading zero
        if(day < 10){
            dayString = "0" + dayString;
        }
        String label = dayOfWeek + " (" + dayString  + "/" + monthString + ")";
        if(isDateToday(date)){
            label = label + " - " + strings.getString("today");
        }
        return label;
    }

    private boolean isDateToday(Calendar date){
        Calendar rightNow = Calendar.getInstance();
        return rightNow.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                && rightNow.get(Calendar.MONTH) == date.get(Calendar.MONTH)
                && rightNow.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH);
    }

    private DefaultTableModel makeDataModel(List<Lesson> lessons){
        DefaultTableModel tableModel = new DefaultTableModel(tableColumnNames, 0);
        if(lessons != null){
            for(Lesson lesson : lessons){
                tableModel.addRow(new Object[]{
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

        String schedule = strings.getString("date") + ": ";
        schedule = schedule + converter.convertToDatabaseColumn(date) + "\n";
        schedule += "\n";

        for(Lesson lesson : lessons){
            schedule = schedule + strings.getString("number") + ": ";
            schedule = schedule + lesson.getLessonNumber() + "\n";

            schedule = schedule + strings.getString("subject") + ": ";
            schedule = schedule +lesson.getSubject() + "\n";

            if(!lesson.getTeacher().equals("")){
                schedule = schedule + strings.getString("teacher") + ": ";
                schedule = schedule + lesson.getTeacher() + "\n";
            }

            if(!lesson.getRoomNumber().equals("")){
                schedule = schedule + strings.getString("room") + ": ";
                schedule = schedule + lesson.getRoomNumber() + "\n";
            }

            schedule += "\n";
        }
        schedule += "\n";

        return schedule;
    }

    /**
     * Этот метод позволяет узнать, открыто ли расписание для промотра.
     * @return
     */
    public boolean isOpened(){
        return isOpened;
    }

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
            table.setModel(makeDataModel(lessonsList));
        });
    }
}