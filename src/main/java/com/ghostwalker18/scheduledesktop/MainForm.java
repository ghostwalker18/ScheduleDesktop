package com.ghostwalker18.scheduledesktop;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class MainForm {

    private ScheduleState state;
    private Theme theme = new DefaulTheme();
    private DatabaseWorker databaseWorker = DatabaseWorker.getInstance();
    private JComboBox groupComboBox;
    private JButton clearButton;
    private JComboBox teacherComboBox;
    private JButton clearButton1;
    public JPanel mainPanel;
    private JPanel headerPanel;
    private JLabel chooseGroupLabel;
    private JLabel chooseTeacherLabel;
    private JButton backwardButton;
    private JButton forwardButton;
    private WeekdayButton mondayButton;
    private WeekdayButton tuesdayButton;
    private WeekdayButton wednesdayButton;
    private WeekdayButton thursdayButton;
    private WeekdayButton fridayButton;
    private JPanel schedulePanel;
    private JScrollPane scheduleScroll;
    private JTabbedPane tabs;
    private JPanel schedule;
    private JPanel times;
    private JButton shareButton;
    private JButton settingsButton;

    private void createUIComponents() {
        state = new ScheduleState(new Date(2023 - 1900, Calendar.JUNE, 8));
        mondayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Понедельник");
        state.addObserver(mondayButton);
        tuesdayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Вторник");
        state.addObserver(tuesdayButton);
        wednesdayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Среда");
        state.addObserver(wednesdayButton);
        thursdayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Четверг");
        state.addObserver(thursdayButton);
        fridayButton = new WeekdayButton(state.getYear(), state.getWeek(), "Пятница");
        state.addObserver(fridayButton);
    }

    public MainForm() throws SQLException {
        createUIComponents();
        $$$setupUI$$$();
        UIManager.put("ToolTip.background", theme.getBackgroundColor());
        UIManager.put("ToolTip.foreground", theme.getAccentColor());
        schedulePanel.setBackground(theme.getBackgroundColor());
        scheduleScroll.getVerticalScrollBar().setUnitIncrement(6);
        scheduleScroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = theme.getSecondaryColor();
                this.trackColor = theme.getBackgroundColor();
            }
        });
        scheduleScroll.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = theme.getSecondaryColor();
                this.trackColor = theme.getBackgroundColor();
            }
        });

        clearButton.addActionListener(e -> {
            groupComboBox.setSelectedIndex(0);
            state.setGroup(null);
        });

        clearButton1.addActionListener(e -> {
            teacherComboBox.setSelectedIndex(0);
            state.setTeacher(null);
        });

        groupComboBox.addActionListener(e -> {
            if (groupComboBox.getSelectedIndex() != 0) {
                state.setGroup(groupComboBox.getSelectedItem().toString());
            } else {
                state.setGroup(null);
            }

        });
        Vector<String> groupNames = databaseWorker.getGroupNames();
        if (groupNames != null) {
            groupComboBox.setModel(new DefaultComboBoxModel(databaseWorker.getGroupNames()));
        }
        ;
        groupComboBox.insertItemAt("Не выбрано", 0);
        groupComboBox.setSelectedIndex(0);
        groupComboBox.setToolTipText("Например: \"A-11\"");

        teacherComboBox.addActionListener(e -> {
            if (teacherComboBox.getSelectedIndex() != 0) {
                state.setTeacher(teacherComboBox.getSelectedItem().toString());
            } else {
                state.setTeacher(null);
            }
        });
        Vector<String> teacherNames = databaseWorker.getTeacherNames();
        if (teacherNames != null) {
            teacherComboBox.setModel(new DefaultComboBoxModel(databaseWorker.getTeacherNames()));
        }
        ;
        teacherComboBox.insertItemAt("Не выбрано", 0);
        teacherComboBox.setSelectedIndex(0);
        teacherComboBox.setToolTipText("Например: \"Иванов И.И\"");

        backwardButton.addActionListener(e -> {
            state.goPreviousWeek();
        });
        backwardButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    state.goPreviousWeek();
            }
        });
        backwardButton.setToolTipText("Предыдущая неделя");

        forwardButton.addActionListener(e -> {
            state.goNextWeek();
        });
        forwardButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    state.goNextWeek();
            }
        });
        forwardButton.setToolTipText("Следующая неделя");
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs = new JTabbedPane();
        tabs.setTabPlacement(1);
        mainPanel.add(tabs, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        schedule = new JPanel();
        schedule.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs.addTab("Расписание", schedule);
        headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayoutManager(1, 6, new Insets(0, 10, 0, 10), -1, -1));
        headerPanel.setBackground(new Color(-10051327));
        schedule.add(headerPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(634, 55), null, 0, false));
        chooseGroupLabel = new JLabel();
        chooseGroupLabel.setBackground(new Color(-1));
        chooseGroupLabel.setForeground(new Color(-1));
        chooseGroupLabel.setText("Выберите группу");
        headerPanel.add(chooseGroupLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        groupComboBox = new JComboBox();
        groupComboBox.setEditable(true);
        groupComboBox.setPopupVisible(false);
        groupComboBox.setToolTipText("");
        headerPanel.add(groupComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearButton = new JButton();
        clearButton.setBackground(new Color(-1));
        clearButton.setForeground(new Color(-16249741));
        clearButton.setText("Очистить");
        headerPanel.add(clearButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseTeacherLabel = new JLabel();
        chooseTeacherLabel.setForeground(new Color(-1));
        chooseTeacherLabel.setText("Выберите преподавателя:");
        headerPanel.add(chooseTeacherLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        teacherComboBox = new JComboBox();
        teacherComboBox.setEditable(true);
        teacherComboBox.setToolTipText("");
        headerPanel.add(teacherComboBox, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearButton1 = new JButton();
        clearButton1.setBackground(new Color(-1));
        clearButton1.setForeground(new Color(-16249741));
        clearButton1.setText("Очистить");
        headerPanel.add(clearButton1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        schedulePanel = new JPanel();
        schedulePanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 0, 10), -1, -1));
        schedulePanel.setBackground(new Color(-1));
        schedulePanel.setForeground(new Color(-1));
        schedule.add(schedulePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(24, 105), null, 0, false));
        backwardButton = new JButton();
        backwardButton.setBackground(new Color(-14115282));
        backwardButton.setForeground(new Color(-1));
        backwardButton.setText("Назад");
        schedulePanel.add(backwardButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forwardButton = new JButton();
        forwardButton.setBackground(new Color(-14115282));
        forwardButton.setForeground(new Color(-1));
        forwardButton.setText("Вперед");
        schedulePanel.add(forwardButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scheduleScroll = new JScrollPane();
        schedulePanel.add(scheduleScroll, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scheduleScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16249741)), "Расписание на неделю", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null, new Color(-16249741)));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, 10));
        scheduleScroll.setViewportView(panel1);
        panel1.add(mondayButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(tuesdayButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(wednesdayButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(thursdayButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(fridayButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        times = new JPanel();
        times.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs.addTab("Звонки", times);
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        shareButton = new JButton();
        shareButton.setText("Скопировать");
        toolBar1.add(shareButton);
        settingsButton = new JButton();
        settingsButton.setText("Настройки");
        toolBar1.add(settingsButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}