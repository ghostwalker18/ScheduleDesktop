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
import com.ghostwalker18.scheduledesktop.common.*;
import com.ghostwalker18.scheduledesktop.models.ScheduleRepository;
import com.ghostwalker18.scheduledesktop.system.*;
import com.ghostwalker18.scheduledesktop.viewmodels.ScheduleModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Этот класс представляет собой основной экран приложения.
 *
 * @author Ипатов Никита
 */
public class MainForm
        extends Form {
    private final ScheduleRepository repository = ScheduleApp.getInstance().getScheduleRepository();
    private ScheduleModel state;
    private final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());

    private String savedGroup;
    private JComboBox<String> groupComboBox;
    private JButton clearGroupButton;
    private JComboBox<String> teacherComboBox;
    private JButton clearTeacherButton;
    private JLabel chooseGroupLabel;
    private JLabel chooseTeacherLabel;
    private JButton backwardButton;
    private JButton forwardButton;
    private ScheduleItemFragment mondayButton;
    private ScheduleItemFragment tuesdayButton;
    private ScheduleItemFragment wednesdayButton;
    private ScheduleItemFragment thursdayButton;
    private ScheduleItemFragment fridayButton;
    private JTabbedPane tabs;
    private JButton shareButton;
    private JButton settingsButton;
    private JButton downloadScheduleButton;
    private ImageView mondayTimes;
    private ImageView otherTimes;
    private JProgressBar updateProgress;
    private JLabel updateStatus;
    private JButton refreshButton;

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

    /**
     * Этот метод используется для скачивания расписания в выбранную пользователем директорию.
     */
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

    @Override
    public void onCreate(Bundle savedState, Bundle bundle) {
        super.onCreate(savedState, bundle);
        state = new ViewModelProvider(this).get(ScheduleModel.class);
        state.getGroup().subscribe(group -> savedGroup = group);
    }

    @Override
    public void onCreatedUI() {
        setupGroupSearch();
        setupTeacherSearch();

        clearGroupButton.addActionListener(e -> {
            groupComboBox.setSelectedIndex(0);
            state.setGroup(null);
        });

        clearTeacherButton.addActionListener(e -> {
            teacherComboBox.setSelectedIndex(0);
            state.setTeacher(null);
        });

        backwardButton.addMouseListener(new MouseClickAdapter(){
            @Override
            public void onClick() {
                state.goPreviousWeek();
            }

            @Override
            public void onLongClick() {
                JDialog datePicker = new DatePicker(MainForm.this);
                datePicker.pack();
                datePicker.setVisible(true);
            }
        });
        backwardButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    state.goPreviousWeek();
                }
            }
        });

        forwardButton.addMouseListener(new MouseClickAdapter(){
            @Override
            public void onClick() {
                state.goNextWeek();
            }

            @Override
            public void onLongClick() {
                JDialog datePicker = new DatePicker(MainForm.this);
                datePicker.pack();
                datePicker.setVisible(true);
            }
        });
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

        settingsButton.addActionListener(e -> ScheduleApp.getInstance()
                .startActivity(SettingsForm.class, null)
        );

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

    @Override
    public void onDestroy(Bundle outState) {
        repository.saveGroup(savedGroup);
        super.onDestroy(outState);
    }

    @Override
    public void onCreateUIComponents() {
        Bundle bundle = new Bundle();
        bundle.putString("dayOfWeek", strings.getString("monday"));
        mondayButton = new Fragment.FragmentFactory().create(this, ScheduleItemFragment.class, bundle);
        bundle = new Bundle();
        bundle.putString("dayOfWeek", strings.getString("tuesday"));
        tuesdayButton = new Fragment.FragmentFactory().create(this, ScheduleItemFragment.class, bundle);
        bundle = new Bundle();
        bundle.putString("dayOfWeek", strings.getString("wednesday"));
        wednesdayButton = new Fragment.FragmentFactory().create(this, ScheduleItemFragment.class, bundle);
        bundle = new Bundle();
        bundle.putString("dayOfWeek", strings.getString("thursday"));
        thursdayButton = new Fragment.FragmentFactory().create(this, ScheduleItemFragment.class, bundle);
        bundle = new Bundle();
        bundle.putString("dayOfWeek", strings.getString("friday"));
        fridayButton = new Fragment.FragmentFactory().create(this, ScheduleItemFragment.class, bundle);
        mondayTimes = new ImageView();
        otherTimes = new ImageView();
    }

    @Override
    public void onSetupLanguage() {
        setTitle(strings.getString("app_name"));

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

    @Override
    public void onCreateUI() {
        setMainPanel(new JPanel());
        getMainPanel().setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs = new JTabbedPane();
        tabs.setTabPlacement(1);
        getMainPanel().add(tabs, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        JPanel schedule = new JPanel();
        schedule.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        schedule.setMaximumSize(new Dimension(-1, -1));
        schedule.setMinimumSize(new Dimension(-1, -1));
        tabs.addTab("Расписание", schedule);
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayoutManager(1, 6, new Insets(0, 10, 0, 10), -1, -1));
        schedule.add(headerPanel, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(634, 55), null, 0, false));
        chooseGroupLabel = new JLabel();
        headerPanel.add(chooseGroupLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        groupComboBox = new JComboBox<>();
        groupComboBox.setEditable(false);
        groupComboBox.setPopupVisible(false);
        headerPanel.add(groupComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearGroupButton = new JButton();
        clearGroupButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        clearGroupButton.setEnabled(true);
        headerPanel.add(clearGroupButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseTeacherLabel = new JLabel();
        headerPanel.add(chooseTeacherLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        teacherComboBox = new JComboBox<>();
        teacherComboBox.setEditable(false);
        headerPanel.add(teacherComboBox, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearTeacherButton = new JButton();
        clearTeacherButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        headerPanel.add(clearTeacherButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JPanel schedulePanel = new JPanel();
        schedulePanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 0, 10), -1, -1));
        schedule.add(schedulePanel, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(24, 105), null, 0, false));
        backwardButton = new JButton();
        schedulePanel.add(backwardButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forwardButton = new JButton();
        schedulePanel.add(forwardButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JScrollPane scheduleScroll = new JScrollPane();
        scheduleScroll.getVerticalScrollBar().setUnitIncrement(6);
        schedulePanel.add(scheduleScroll, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(34, 244), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, 10));
        scheduleScroll.setViewportView(panel1);
        panel1.add(mondayButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(tuesdayButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(wednesdayButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(thursdayButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(fridayButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JPanel statusPanel = new JPanel();
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
        JPanel times = new JPanel();
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

    /**
     * Этот класс используется для выбора даты (недели) для отображения расписания.
     *
     * @author Ипатов Никита
     * @since 3.0
     */
    public static class DatePicker
            extends DatePickerDialog {
        private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
                new XMLBundleControl());
        private final ScheduleModel model;

        public DatePicker(Form owner) {
            model = new ViewModelProvider(owner).get(ScheduleModel.class);
            setupLanguage();
        }

        @Override
        public void setupLanguage() {
            setOKText(platformStrings.getString("go_to_date"));
            setCancelText(platformStrings.getString("cancelButtonText"));
        }

        @Override
        public void onDateSet(Calendar date) {
            model.goToDate(date);
        }
    }
}