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
import com.ghostwalker18.scheduledesktop.viewmodels.ScheduleState;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Этот класс представляет собой основной экран приложения.
 *
 * @author Ипатов Никита
 */
public class MainForm
        implements WindowListener {
    private final ScheduleState state = new ScheduleState(new Date());
    private final ScheduleRepository repository = ScheduleApp.getInstance().getScheduleRepository();
    private final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private JPanel mainPanel;
    private JComboBox<String> groupComboBox;
    private JButton clearGroupButton;
    private JComboBox<String> teacherComboBox;
    private JButton clearTeacherButton;
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
    private JButton downloadScheduleButton;
    private ImageView mondayTimes;
    private ImageView otherTimes;
    private JProgressBar updateProgress;
    private JPanel statusPanel;
    private JLabel updateStatus;
    private JButton refreshButton;

    /**
     * Этот метод используется для создания кастомных UI компоненетов.
     */
    private void createUIComponents() {
        mondayButton = new WeekdayButton(state.getYear(), state.getWeek(), strings.getString("monday"));
        state.addObserver(mondayButton);
        tuesdayButton = new WeekdayButton(state.getYear(), state.getWeek(), strings.getString("tuesday"));
        state.addObserver(tuesdayButton);
        wednesdayButton = new WeekdayButton(state.getYear(), state.getWeek(), strings.getString("wednesday"));
        state.addObserver(wednesdayButton);
        thursdayButton = new WeekdayButton(state.getYear(), state.getWeek(), strings.getString("thursday"));
        state.addObserver(thursdayButton);
        fridayButton = new WeekdayButton(state.getYear(), state.getWeek(), strings.getString("friday"));
        state.addObserver(fridayButton);
        mondayTimes = new ImageView();
        otherTimes = new ImageView();
    }

    public MainForm() {
        createUIComponents();
        $$$setupUI$$$();
        setupLanguage();
        setupGroupSearch();
        setupTeacherSearch();

        scheduleScroll.getVerticalScrollBar().setUnitIncrement(6);

        clearGroupButton.addActionListener(e -> {
            groupComboBox.setSelectedIndex(0);
            state.setGroup(null);
        });

        clearTeacherButton.addActionListener(e -> {
            teacherComboBox.setSelectedIndex(0);
            state.setTeacher(null);
        });

        backwardButton.addActionListener(e ->
                state.goPreviousWeek());
        backwardButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    state.goPreviousWeek();
                }
            }
        });

        forwardButton.addActionListener(e ->
                state.goNextWeek());
        forwardButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    state.goNextWeek();
                }
            }
        });

        shareButton.addActionListener(e -> {
            switch (tabs.getSelectedIndex()){
                case 0:
                    shareSchedule();
                    break;
                case 1:
                    shareTimes();
                    break;
            }
        });
        shareButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    switch (tabs.getSelectedIndex()){
                        case 0:
                            shareSchedule();
                            break;
                        case 1:
                            shareTimes();
                            break;
                    }
                }
            }
        });

        settingsButton.addActionListener(e -> {
            JFrame frame = new JFrame(strings.getString("settings"));
            frame.setIconImage(Toolkit.getDefaultToolkit()
                    .createImage(ScheduleApp.class.getResource("/images/baseline_settings_black_36dp.png")));
            frame.setPreferredSize(new Dimension(500, 400));
            frame.setContentPane(new SettingsForm().getMainPanel());
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

        downloadScheduleButton.addActionListener(e -> downloadSchedule());

        refreshButton.addActionListener( e -> repository.update());

        repository.getMondayTimes().subscribe(image -> mondayTimes.setImage(image));

        repository.getOtherTimes().subscribe(image -> otherTimes.setImage(image));

        repository.getStatus().subscribe(status -> {
            updateStatus.setText(status.text);
            updateProgress.setValue(status.progress);
        });

        groupComboBox.requestFocusInWindow();
    }

    /**
     * Этот метод используется для настройки всех надписей на экране, используя строковые ресурсы.
     */
    private void setupLanguage() {
        tabs.setTitleAt(0, strings.getString("days_tab"));
        tabs.setTitleAt(1, strings.getString("times_tab"));

        shareButton.setText(strings.getString("share"));
        shareButton.setToolTipText(platformStrings.getString("share_tooltip"));

        settingsButton.setText(strings.getString("settings"));
        settingsButton.setToolTipText(platformStrings.getString("settings_tooltip"));

        downloadScheduleButton.setText(strings.getString("download_schedule"));
        downloadScheduleButton.setToolTipText(platformStrings.getString("download_tooltip"));

        backwardButton.setText(strings.getString("back"));
        backwardButton.setToolTipText(platformStrings.getString("backward_tooltip"));

        forwardButton.setText(strings.getString("forward"));
        forwardButton.setToolTipText(platformStrings.getString("forward_tooltip"));

        chooseGroupLabel.setText(strings.getString("group_choice_text"));

        groupComboBox.setToolTipText(platformStrings.getString("groups_tooltip"));

        clearGroupButton.setText(platformStrings.getString("clear"));

        chooseTeacherLabel.setText(strings.getString("teacher_choice_text"));

        teacherComboBox.setToolTipText(platformStrings.getString("teachers_tooltip"));

        clearTeacherButton.setText(platformStrings.getString("clear"));

        updateStatus.setText(platformStrings.getString("downloading"));

        refreshButton.setToolTipText(platformStrings.getString("update_tooltip"));
    }

    /**
     * Этот метод используется для настройки поля выбора группы.
     */
    private void setupGroupSearch() {
        repository.getGroups().subscribe(groups -> {
            if (groups != null) {
                groupComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(groups)));
            }
            groupComboBox.insertItemAt(platformStrings.getString("combox_placeholder"), 0);
            groupComboBox.setSelectedIndex(0);
            String savedGroup = repository.getSavedGroup();
            for (int i = 0; i < groupComboBox.getItemCount(); i++) {
                if (groupComboBox.getItemAt(i).equals(savedGroup)) {
                    groupComboBox.setSelectedIndex(i);
                    state.setGroup(savedGroup);
                    break;
                }
            }
        });
        groupComboBox.addActionListener(e -> {
            if (groupComboBox.getSelectedIndex() != 0) {
                state.setGroup(groupComboBox.getSelectedItem().toString());
            } else {
                state.setGroup(null);
            }
        });
    }

    /**
     * Этот метод используется для настройки поля выбора преподавателя.
     */
    private void setupTeacherSearch() {
        repository.getTeachers().subscribe(teachers -> {
            if (teachers != null) {
                teacherComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(teachers)));
            }
            teacherComboBox.insertItemAt(platformStrings.getString("combox_placeholder"), 0);
            teacherComboBox.setSelectedIndex(0);
        });
        teacherComboBox.addActionListener(e -> {
            if (teacherComboBox.getSelectedIndex() != 0) {
                state.setTeacher(teacherComboBox.getSelectedItem().toString());
            } else {
                state.setTeacher(null);
            }
        });
    }

    /**
     * Этот метод используется для добавления форматированной строки расписания в системный
     * буфер обмена и уведомления об этом.
     */
    private void shareSchedule(){
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(getSchedule()), null);

        Toast message = new Toast(shareButton, platformStrings.getString("share_completed"));
        message.display();
    }

    /**
     * Этот метод используется для добавления файлов звонков в системный буфер обмена и
     * уведомления об этом.
     */
    private void shareTimes(){
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new FileTransferable()
                        .add(new File(ScheduleRepository.MONDAY_TIMES_PATH))
                                .add(new File(ScheduleRepository.OTHER_TIMES_PATH))
                        , null);

        Toast message = new Toast(shareButton, platformStrings.getString("share_times_completed"));
        message.display();
    }

    /**
     * Этот метод используется для получения расписания для всех открытых дней.
     * @return расписание в виде строки
     */
    private String getSchedule() {
        String scheduleText = "";
        if (mondayButton.isOpened())
            scheduleText += mondayButton.getSchedule();
        if (tuesdayButton.isOpened())
            scheduleText += tuesdayButton.getSchedule();
        if (wednesdayButton.isOpened())
            scheduleText += wednesdayButton.getSchedule();
        if (thursdayButton.isOpened())
            scheduleText += thursdayButton.getSchedule();
        if (fridayButton.isOpened())
            scheduleText += fridayButton.getSchedule();
        return scheduleText;
    }

    private void downloadSchedule(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(platformStrings.getString("download_file_dialog"));
        int result = fileChooser.showDialog(this.getMainPanel(), platformStrings.getString("saveButtonText"));
        if(result == JFileChooser.APPROVE_OPTION){
            new Thread(() -> {
                //Chosen directory is also a file, heh
                String directory = fileChooser.getSelectedFile().getAbsolutePath();
                for(Pair<String, File> fileRaw : repository.getScheduleFiles()){
                    File outputFile = new File(directory + File.separator + fileRaw.getValue0());
                    try{
                        Files.copy(fileRaw.getValue1().toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch (IOException e){
                        System.err.println(e.getMessage());
                    }
                }
                Toast message = new Toast(shareButton,
                        platformStrings.getString("download_completed") + directory);
                message.display();
            }).start();
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        setMainPanel(new JPanel());
        getMainPanel().setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs = new JTabbedPane();
        tabs.setTabPlacement(1);
        getMainPanel().add(tabs, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        schedule = new JPanel();
        schedule.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        schedule.setMaximumSize(new Dimension(-1, -1));
        schedule.setMinimumSize(new Dimension(-1, -1));
        tabs.addTab("Расписание", schedule);
        headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayoutManager(1, 6, new Insets(0, 10, 0, 10), -1, -1));
        schedule.add(headerPanel, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(634, 55), null, 0, false));
        chooseGroupLabel = new JLabel();
        headerPanel.add(chooseGroupLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        groupComboBox = new JComboBox<String>();
        groupComboBox.setEditable(false);
        groupComboBox.setPopupVisible(false);
        groupComboBox.setToolTipText("");
        headerPanel.add(groupComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearGroupButton = new JButton();
        clearGroupButton.setEnabled(true);
        headerPanel.add(clearGroupButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseTeacherLabel = new JLabel();
        headerPanel.add(chooseTeacherLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        teacherComboBox = new JComboBox<String>();
        teacherComboBox.setEditable(false);
        headerPanel.add(teacherComboBox, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearTeacherButton = new JButton();
        headerPanel.add(clearTeacherButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        schedulePanel = new JPanel();
        schedulePanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 0, 10), -1, -1));
        schedule.add(schedulePanel, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(24, 105), null, 0, false));
        backwardButton = new JButton();
        schedulePanel.add(backwardButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forwardButton = new JButton();
        schedulePanel.add(forwardButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scheduleScroll = new JScrollPane();
        schedulePanel.add(scheduleScroll, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(34, 244), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, 10));
        scheduleScroll.setViewportView(panel1);
        panel1.add(mondayButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(tuesdayButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(wednesdayButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(thursdayButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(fridayButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        schedule.add(statusPanel, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(-1, 50), new Dimension(-1, 80), null, 0, false));
        final Spacer spacer1 = new Spacer();
        statusPanel.add(spacer1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        statusPanel.add(spacer2, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        updateStatus = new JLabel();
        statusPanel.add(updateStatus, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(-1, 30), null, 0, false));
        updateProgress = new JProgressBar();
        statusPanel.add(updateProgress, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        refreshButton = new JButton();
        refreshButton.setOpaque(false);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setIcon(new ImageIcon(getClass()
                .getResource("/images/baseline_refresh_black_24dp.png")));
        statusPanel.add(refreshButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        times = new JPanel();
        times.setLayout(new GridLayoutManager(1, 2, new Insets(10, 20, 20, 20), -1, -1));
        times.setMaximumSize(new Dimension(-1, -1));
        times.setMinimumSize(new Dimension(-1, -1));
        tabs.addTab("Звонки", times);
        mondayTimes.setAlignmentX(0.0f);
        mondayTimes.setAlignmentY(0.0f);
        times.add(mondayTimes, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        otherTimes.setAlignmentX(0.0f);
        otherTimes.setAlignmentY(0.0f);
        times.add(otherTimes, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JToolBar toolBar1 = new JToolBar();
        getMainPanel().add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        final Spacer spacer3 = new Spacer();
        toolBar1.add(spacer3);
        downloadScheduleButton = new JButton();
        downloadScheduleButton.setIcon(new ImageIcon(getClass()
                .getResource("/images/baseline_file_download_black_36dp.png")));
        toolBar1.add(downloadScheduleButton);
        shareButton = new JButton();
        shareButton.setIcon(new ImageIcon(getClass()
                .getResource("/images/baseline_share_black_36dp.png")));
        toolBar1.add(shareButton);
        settingsButton = new JButton();
        settingsButton.setIcon(new ImageIcon(getClass()
                .getResource("/images/baseline_settings_black_36dp.png")));
        toolBar1.add(settingsButton);
    }

    @Override
    public void windowOpened(WindowEvent e) {/*Not required*/}

    /**
     * Этот метод используется для реакции на событие закрытия окна. Сохранаяет текущею выбранную группу.
     * @param e the event to be processed
     */
    @Override
    public void windowClosing(WindowEvent e) {
        try {
            String savedGroup = state.getGroup();
            repository.saveGroup(savedGroup);
        } catch (Exception ignored) {/*Not required*/}
    }

    @Override
    public void windowClosed(WindowEvent e) {/*Not required*/}

    @Override
    public void windowIconified(WindowEvent e) {/*Not required*/}

    @Override
    public void windowDeiconified(WindowEvent e) {/*Not required*/}

    @Override
    public void windowActivated(WindowEvent e) {/*Not required*/}

    @Override
    public void windowDeactivated(WindowEvent e) {/*Not required*/}

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }
}