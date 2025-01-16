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

import com.ghostwalker18.scheduledesktop.converters.DateConverters;
import com.ghostwalker18.scheduledesktop.models.Note;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Этот класс служит для отображения элемента списка.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class NoteViewHolder
        extends JPanel
        implements ListCellRenderer<Note> {
    private JLabel date;
    private  JLabel theme;
    private JTextArea text;
    private JLabel checked;

    public NoteViewHolder(){
        setupUI();
    }
    @Override
    public Component getListCellRendererComponent(JList<? extends Note> list, Note note, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        date.setText(new DateConverters().convertToDatabaseColumn(note.date));
        theme.setText(note.theme);
        text.setText(note.text);
        checked.setVisible(isSelected);
        return this;
    }

    private void setupUI(){
        setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel note = new JPanel();
        note.setLayout(new GridLayoutManager(3, 1, new Insets(5, 5, 5, 5), -1, -1));
        add(note, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        note.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(null), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        date = new JLabel();
        note.add(date, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        note.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(null), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        theme = new JLabel();
        panel1.add(theme, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        text = new JTextArea();
        text.setEditable(false);
        note.add(text, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        checked = new JLabel();
        checked.setEnabled(true);
        checked.setIcon(new ImageIcon(getClass().getResource("/images/note_checked.png")));
        add(checked, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }
}