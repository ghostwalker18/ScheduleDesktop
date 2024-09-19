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

import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Этот класс представляет собой адаптер ячейки таблицы для отображения многострочного текста.
 *
 * @author Ипатов Никита
 */
public class MultilineTableCellRenderer
        extends JTextArea
        implements TableCellRenderer {
    public MultilineTableCellRenderer(){
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        setSize(table.getColumnModel().getColumn(column).getWidth(), table.getRowHeight(row));
        int preferredHeight = getPreferredSize().height;
        if (table.getRowHeight(row) != preferredHeight){
            table.setRowHeight(row, preferredHeight);
        }
        if(isSelected){
            setBackground(UIManager.getColor("Table.selectionInactiveBackground"));
        }
        else{
            Color background = UIManager.getColor("Table.background");
            Color alternateColor = UIManager.getColor("Table.alternateRowColor");
            if (alternateColor != null && row % 2 != 0) {
                background = alternateColor;
            }
            setBackground(background);
        }
        return this;
    }
}