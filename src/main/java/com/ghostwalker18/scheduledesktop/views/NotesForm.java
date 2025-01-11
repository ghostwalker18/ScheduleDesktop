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
import com.ghostwalker18.scheduledesktop.viewmodels.NotesModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Этот класс представляет собой экран приложения, на котором отображаются заметки к занятиям.
 *
 * @author Ипатов Никита
 */
public class NotesForm
        extends Form {
    private final NotesModel model = new NotesModel();
    private final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private JButton addNoteButton;
    private JList notesList;
    private JTextField searchField;
    private JButton filterButton;
    private JButton backButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton shareButton;
    private String group;
    private Calendar startDate;
    private Calendar endDate;

    @Override
    public void onCreate(Bundle bundle) {
        group = bundle.getString("group");
        startDate = new DateConverters().convertToEntityAttribute(bundle.getString("date"));
        endDate = startDate;
        model.setGroup(group);
        model.setStartDate(startDate);
        model.setEndDate(endDate);
    }

    @Override
    public void onCreatedUI() {
        addNoteButton.addActionListener(e -> {
            Bundle bundle = new Bundle();
            bundle.putString("group", group);
            if(startDate != null){
                bundle.putString("date", new DateConverters().convertToDatabaseColumn(startDate));
            }
            ScheduleApp.getInstance().startActivity(EditNoteForm.class, bundle);
        });
        backButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(MainForm.class, null));
        model.getNotes().subscribe(notes -> {
        });
    }

    @Override
    public void onSetupLanguage() {
        setTitle(strings.getString("notes_activity"));
        addNoteButton.setText(strings.getString("add_note"));
        editButton.setText(strings.getString("edit"));
        deleteButton.setText(strings.getString("delete"));
        shareButton.setText(strings.getString("share"));
        filterButton.setText(platformStrings.getString("notes_filter"));
        backButton.setText(platformStrings.getString("back_button_text"));
    }

    @Override
    public void onCreateUI() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 0, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        searchField = new JTextField();
        panel1.add(searchField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        filterButton = new JButton();
        filterButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_tune_36.png")));
        panel1.add(filterButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/images/baseline_search_36.png")));
        label1.setText("");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 10, 0, 10), -1, -1));
        mainPanel.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        notesList = new JList();
        scrollPane1.setViewportView(notesList);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.add(panel3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addNoteButton = new JButton();
        addNoteButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_note_add_36.png")));
        addNoteButton.setMargin(new Insets(0, 0, 0, 0));
        panel3.add(addNoteButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_arrow_back_36.png")));
        toolBar1.add(backButton);
        final Spacer spacer2 = new Spacer();
        toolBar1.add(spacer2);
        editButton = new JButton();
        editButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_edit_document_36.png")));
        toolBar1.add(editButton);
        deleteButton = new JButton();
        deleteButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_delete_36.png")));
        toolBar1.add(deleteButton);
        shareButton = new JButton();
        shareButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_share_black_36dp.png")));
        toolBar1.add(shareButton);
        setMainPanel(mainPanel);
    }
}