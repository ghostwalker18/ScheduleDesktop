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

import com.ghostwalker18.scheduledesktop.common.Bundle;
import com.ghostwalker18.scheduledesktop.converters.DateConverters;
import com.ghostwalker18.scheduledesktop.ScheduleApp;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.viewmodels.EditNoteModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.ghostwalker18.scheduledesktop.common.Form;
import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Этот класс представляет собой экран редактирования или добавления новой заметки
 *
 * @author Ипатов Никита
 */
public class EditNoteForm
        extends Form {
    private final EditNoteModel model = new EditNoteModel();
    private final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private JButton saveButton;
    private JButton discardButton;
    private JComboBox<String> groupBox;
    private JComboBox<String> themeBox;
    private JTextField textField;
    private JButton groupClear;
    private JButton themeClear;
    private JButton textClear;
    private JButton chooseDate;
    private JLabel dateField;
    private JLabel dateLabel;
    private JButton backButton;

    @Override
    public void onCreate(Bundle savedState, Bundle bundle) {
        if(bundle != null){
            if(bundle.getInt("noteID") != null){
                model.setNoteID(bundle.getInt("noteID"));
                setTitle(strings.getString("edit_note"));
            }
            else {
                setTitle(strings.getString("add_note"));
            }
            if(bundle.getString("group") != null)
                model.setGroup(bundle.getString("group"));
            if(bundle.getString("date") != null)
                model.setDate(new DateConverters().convertToEntityAttribute(bundle.getString("date")));
        }
    }

    @Override
    public void onCreatedUI() {
        model.getDate().subscribe(
                date -> dateField.setText(new DateConverters().convertToDatabaseColumn(date))
        );
        model.getGroups().subscribe(groups -> {
            if (groups != null) {
                groupBox.setModel(new DefaultComboBoxModel<>(new Vector<>(groups)));
            }
        });
        model.getGroup().subscribe(group -> {
            groupBox.setSelectedItem(group);
        });
        model.getThemes().subscribe(themes ->{
            if (themes != null) {
                themeBox.setModel(new DefaultComboBoxModel<>(new Vector<>(themes)));
            }
        });
        model.getTheme().subscribe(theme -> {
            themeBox.setSelectedItem(theme);
        });
        model.getText().subscribe(text -> textField.setText(text));
        groupBox.addActionListener(e -> model.setGroup(groupBox.getSelectedItem().toString()));
        groupClear.addActionListener(e -> model.setGroup(""));
        themeClear.addActionListener(e -> model.setTheme(""));
        textClear.addActionListener(e -> model.setText(""));
        saveButton.addActionListener(e -> saveNote());
        discardButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(NotesForm.class, null));
        backButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(NotesForm.class, null));
    }

    @Override
    public void onSetupLanguage() {
        dateLabel.setText(strings.getString("date"));
        chooseDate.setText(platformStrings.getString("date_choice"));
        groupClear.setText(platformStrings.getString("clear"));
        themeClear.setText(platformStrings.getString("clear"));
        textClear.setText(platformStrings.getString("clear"));
        saveButton.setText(platformStrings.getString("saveButtonText"));
        discardButton.setText(platformStrings.getString("cancelButtonText"));
    }

    @Override
    public void onCreateUI() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 10, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 10, 0, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dateLabel = new JLabel();
        dateLabel.setText("Дата");
        panel2.add(dateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dateField = new JLabel();
        dateField.setText("Label");
        panel2.add(dateField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseDate = new JButton();
        chooseDate.setText("Выбрать дату");
        panel2.add(chooseDate, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        groupClear = new JButton();
        groupClear.setBorderPainted(true);
        groupClear.setContentAreaFilled(true);
        groupClear.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        groupClear.setText("Очистить");
        panel3.add(groupClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        groupBox = new JComboBox();
        groupBox.setEditable(true);
        panel3.add(groupBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        themeClear = new JButton();
        themeClear.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        themeClear.setText("Очистить");
        panel4.add(themeClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        themeBox = new JComboBox();
        themeBox.setEditable(true);
        panel4.add(themeBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textField = new JTextField();
        panel5.add(textField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textClear = new JButton();
        textClear.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        textClear.setText("Очистить");
        panel5.add(textClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 10, 0, 10), -1, -1));
        mainPanel.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Сохранить");
        panel6.add(saveButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discardButton = new JButton();
        discardButton.setText("Отмена");
        panel6.add(discardButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setBorderPainted(false);
        backButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_arrow_back_36.png")));
        backButton.setText("Назад");
        toolBar1.add(backButton);
        setMainPanel(mainPanel);
    }

    /**
     * Этот метод сохраняет заметку в репозитории и закрывает активность.
     */
    private void saveNote(){
        model.setTheme(themeBox.getSelectedItem().toString());
        model.setText(textField.getText().toString());
        model.saveNote();
        ScheduleApp.getInstance().startActivity(NotesForm.class, null);
    }

    /**
     * Этот метод открывает окно для выбора и установки даты.
     */
    private void showDateDialog(){

    }
}