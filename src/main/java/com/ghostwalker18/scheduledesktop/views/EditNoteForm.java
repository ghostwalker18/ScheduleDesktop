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

import com.ghostwalker18.scheduledesktop.Bundle;
import com.ghostwalker18.scheduledesktop.DateConverters;
import com.ghostwalker18.scheduledesktop.ScheduleApp;
import com.ghostwalker18.scheduledesktop.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.viewmodels.EditNoteModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

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
    private JTextField groupField;
    private JTextField themeField;
    private JTextField textField;
    private JButton groupClear;
    private JButton themeClear;
    private JButton textClear;
    private JButton chooseDate;
    private JLabel dateField;
    private JLabel dateLabel;

    @Override
    public void onCreate(Bundle bundle) {
        if(bundle != null){
            if(bundle.getInt("noteID") != 0){
                model.setNoteID(bundle.getInt("noteID"));
                setTitle(platformStrings.getString("edit_note"));
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
        model.getGroup().subscribe(group -> groupField.setText(group));
        model.getTheme().subscribe(theme -> themeField.setText(theme));
        model.getText().subscribe(text -> textField.setText(text));
        groupClear.addActionListener(e -> model.setGroup(""));
        themeClear.addActionListener(e -> model.setTheme(""));
        textClear.addActionListener(e -> model.setText(""));
        saveButton.addActionListener(e -> saveNote());
        discardButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(NotesForm.class, null));
    }

    @Override
    public void onSetupLanguage() {
        setTitle(strings.getString("edit_note_activity"));
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
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dateLabel = new JLabel();
        dateLabel.setText("Дата");
        panel3.add(dateLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dateField = new JLabel();
        dateField.setText("Label");
        panel3.add(dateField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseDate = new JButton();
        chooseDate.setText("Выбрать");
        panel3.add(chooseDate, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        groupField = new JTextField();
        panel4.add(groupField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        groupClear = new JButton();
        groupClear.setText("Очистить");
        groupClear.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        panel4.add(groupClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        themeField = new JTextField();
        panel5.add(themeField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        themeClear = new JButton();
        themeClear.setText("Очистить");
        themeClear.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        panel5.add(themeClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textField = new JTextField();
        panel6.add(textField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textClear = new JButton();
        textClear.setText("Очистить");
        textClear.setIcon(new ImageIcon(getClass().getResource("/images/baseline_clear_24.png")));
        panel6.add(textClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Сохранить");
        panel7.add(saveButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discardButton = new JButton();
        discardButton.setText("Отмена");
        panel7.add(discardButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        setMainPanel(mainPanel);
    }

    /**
     * Этот метод сохраняет заметку в репозитории и закрывает активность.
     */
    private void saveNote(){
        model.setTheme(themeField.getText().toString());
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