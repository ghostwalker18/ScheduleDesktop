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

import com.ghostwalker18.scheduledesktop.ScheduleApp;
import com.ghostwalker18.scheduledesktop.common.*;
import com.ghostwalker18.scheduledesktop.converters.DateConverters;
import com.ghostwalker18.scheduledesktop.models.ScheduleRepository;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.viewmodels.NotesModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Этот класс служит для отображения панели фильтров заметок.
 *
 * @author Ипатов Никита
 * @since 3.0
 * @see DatePicker
 */
public class NotesFilterFragment
        extends Fragment {
    public interface VisibilityListener {
        void onFragmentShow();
        void onFragmentHide();
    }
    private final ScheduleRepository repository = ScheduleApp.getInstance().getScheduleRepository();
    private final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private NotesModel model;
    private VisibilityListener listener;
    private JButton closeButton;
    private JComboBox<String> groupBox;
    private JButton groupClear;
    private JButton startDateSet;
    private JButton endDateSet;
    private JLabel endDateText;
    private JLabel startDateText;
    private JLabel forGroupLabel;
    private JLabel startDateLabel;
    private JLabel endDateLabel;
    private JLabel filterLabel;

    public NotesFilterFragment(Form form) {
        super(form);
    }

    /**
     * Этот метод задает слушателя события сокрытия фрагмента с экрана.
     */
    public void setListener(VisibilityListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle bundle) {
        model = new ViewModelProvider(getParentForm()).get(NotesModel.class);
    }

    @Override
    public void onCreatedUI() {
        model.getStartDate().subscribe(date -> startDateText.setText(new DateConverters().convertToDatabaseColumn(date)));
        model.getEndDate().subscribe(date -> endDateText.setText(new DateConverters().convertToDatabaseColumn(date)));
        repository.getGroups().subscribe(groups -> {
            groupBox.setModel(new DefaultComboBoxModel<>(new Vector<>(groups)));
            groupBox.setSelectedItem(model.getGroup());
        });
        startDateSet.addActionListener(e -> setStartDate());
        endDateSet.addActionListener(e -> setEndDate());
        groupBox.addActionListener(e -> model.setGroup(groupBox.getSelectedItem().toString()));
        groupClear.addActionListener(e -> {
            groupBox.setSelectedItem("");
            model.setGroup(null);
        });
        closeButton.addActionListener(e -> listener.onFragmentHide());
    }

    /**
     * Этот метод открывает ввод для задания начальной даты выдачи заметок.
     */
    private void setStartDate(){
        JDialog datePicker = new DatePicker(this, "start");
        datePicker.pack();
        datePicker.setVisible(true);
    }

    /**
     * Этот метод открывает ввод для задания конечной даты вывода заметок.
     */
    private void setEndDate(){
        JDialog datePicker = new DatePicker(this, "end");
        datePicker.pack();
        datePicker.setVisible(true);
    }

    @Override
    public void onSetupLanguage() {
        filterLabel.setText(strings.getString("filters"));
        forGroupLabel.setText(strings.getString("for_group"));
        startDateLabel.setText(strings.getString("start_date"));
        endDateLabel.setText(strings.getString("end_date"));
    }

    @Override
    public void onCreateUI() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        closeButton = new JButton();
        closeButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        closeButton.setText("");
        panel2.add(closeButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filterLabel = new JLabel();
        panel2.add(filterLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        groupBox = new JComboBox<>();
        panel3.add(groupBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        groupClear = new JButton();
        groupClear.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        panel3.add(groupClear, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forGroupLabel = new JLabel();
        panel3.add(forGroupLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel3.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        startDateLabel = new JLabel();
        panel4.add(startDateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startDateText = new JLabel();
        panel4.add(startDateText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startDateSet = new JButton();
        startDateSet.setIcon(new ImageIcon(getClass().getResource("/images/baseline_arrow_drop_down_black_36dp.png")));
        panel4.add(startDateSet, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        endDateLabel = new JLabel();
        panel5.add(endDateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        endDateText = new JLabel();
        panel5.add(endDateText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        endDateSet = new JButton();
        endDateSet.setIcon(new ImageIcon(getClass().getResource("/images/baseline_arrow_drop_down_black_36dp.png")));
        panel5.add(endDateSet, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        add(mainPanel);
    }

    /**
     * Этот класс служит для задания начальной/конечной даты выдачи заметок.
     *
     * @author Ипатов Никита
     * @since 3.0
     */
    public static class DatePicker
            extends DatePickerDialog {
        private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
                new XMLBundleControl());;
        private final NotesModel model;
        private final String dateType;

        DatePicker(Fragment owner, String dateType){
            model = new ViewModelProvider(owner.getParentForm()).get(NotesModel.class);
            this.dateType = dateType;
            setupLanguage();
        }

        @Override
        public void setupLanguage() {
            setOKText(platformStrings.getString("saveButtonText"));
            setCancelText(platformStrings.getString("cancelButtonText"));
        }

        @Override
        public void onDateSet(Calendar date) {
            switch(dateType){
                case "start":
                    model.setStartDate(date);
                    break;
                case "end":
                    model.setEndDate(date);
                    break;
            }
        }
    }
}