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

import com.ghostwalker18.scheduledesktop.common.Form;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Этот класс используется для отображенияя экрана импорта и экспорта БД приложения.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class ImportForm
        extends Form {
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private JComboBox<String> operationTypeBox;
    private JComboBox<String> dataTypesBox;
    private JComboBox<String> importModeBox;
    private JButton doOperationButton;
    private JLabel operationTypeLabel;
    private JLabel dataTypesLabel;
    private JLabel importTypeLabel;

    @Override
    public void onCreatedUI() {
    }

    @Override
    public void onSetupLanguage() {
        operationTypeLabel.setText(strings.getString("operation_type"));
        dataTypesLabel.setText(strings.getString("data_types"));
        importTypeLabel.setText(strings.getString("import_policy_type"));
        doOperationButton.setText(strings.getString("export_data"));
    }

    @Override
    public void onCreateUI() {
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 2, new Insets(0, 10, 0, 10), -1, -1));
        operationTypeLabel = new JLabel();
        operationTypeLabel.setHorizontalAlignment(0);
        panel1.add(operationTypeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataTypesLabel = new JLabel();
        panel1.add(dataTypesLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        operationTypeBox = new JComboBox<>();
        panel1.add(operationTypeBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataTypesBox = new JComboBox<>();
        panel1.add(dataTypesBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importTypeLabel = new JLabel();
        panel1.add(importTypeLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importModeBox = new JComboBox<>();
        panel1.add(importModeBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doOperationButton = new JButton();
        panel1.add(doOperationButton, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }
}