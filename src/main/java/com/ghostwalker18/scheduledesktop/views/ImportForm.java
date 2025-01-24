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
import com.ghostwalker18.scheduledesktop.common.Form;
import com.ghostwalker18.scheduledesktop.system.FileTransferable;
import com.ghostwalker18.scheduledesktop.system.Toast;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Vector;

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
    private static final  ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private static final  ResourceBundle operationTypes = ResourceBundle.getBundle("arrays/operation_types",
            new XMLBundleControl());
    private static final  ResourceBundle dataTypes = ResourceBundle.getBundle("arrays/data_types",
            new XMLBundleControl());
    private static final  ResourceBundle importModes = ResourceBundle.getBundle("arrays/import_modes",
            new XMLBundleControl());
    private JComboBox<String> operationTypeBox;
    private JComboBox<String> dataTypesBox;
    private JComboBox<String> importModeBox;
    private JButton doOperationButton;
    private JLabel operationTypeLabel;
    private JLabel dataTypesLabel;
    private JLabel importTypeLabel;
    private JButton backButton;

    @Override
    public void onCreatedUI(){
        backButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(SettingsForm.class, null));
        List<String> operationTypesValues = new LinkedList<>();
        for(String key : operationTypes.keySet()){
            operationTypesValues.add(operationTypes.getString(key));
        }
        operationTypeBox.setModel(new DefaultComboBoxModel<>(new Vector<>(operationTypesValues)));
        List<String> dataTypesValues = new LinkedList<>();
        for(String key : dataTypes.keySet()){
            dataTypesValues.add(dataTypes.getString(key));
        }
        operationTypeBox.setSelectedItem(operationTypes.getString("export"));
        doOperationButton.addActionListener(this::exportDB);
        dataTypesBox.setModel(new DefaultComboBoxModel<>(new Vector<>(dataTypesValues)));
        List<String> importModeValues = new LinkedList<>();
        for(String key : importModes.keySet()){
            importModeValues.add(importModes.getString(key));
        }
        importModeBox.setModel(new DefaultComboBoxModel<>(new Vector<>(importModeValues)));
        operationTypeBox.addActionListener(e -> {
            String operationType = "export";
            for(String key : operationTypes.keySet()){
                if(operationTypes.getString(key).equals(operationTypeBox.getSelectedItem().toString())){
                    operationType = key;
                    break;
                }
            }
            if(operationType.equals("import")){
                doOperationButton.setText(strings.getString("import_data"));
                doOperationButton.removeActionListener(this::exportDB);
                doOperationButton.addActionListener(this::importDB);
                importModeBox.setVisible(true);
                importTypeLabel.setVisible(true);
            } else {
                doOperationButton.setText(strings.getString("export_data"));
                doOperationButton.removeActionListener(this::importDB);
                doOperationButton.addActionListener(this::exportDB);
                importModeBox.setVisible(false);
                importTypeLabel.setVisible(false);
            }

        });
    }

    /**
     * Этот метод используется для экспорта БД приложения.
     */
    private void exportDB(ActionEvent event){
        String dataType = dataTypesBox.getSelectedItem().toString();
        try{
            File file = ScheduleApp.getInstance().getDatabase().exportDBFile(dataType);
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new FileTransferable().add(file), null);
            Toast toast = new Toast(this.getMainPanel(), platformStrings.getString("db_export_completed"));
            toast.setDuration(500);
            toast.display();
        } catch (Exception e){/*Not requiered*/}
    }

    /**
     * Этот метод используется для импорта БД приложения.
     */
    private void importDB(ActionEvent event){
        String importPolicy = importModeBox.getSelectedItem().toString();
        String dataType = dataTypesBox.getSelectedItem().toString();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle(strings.getString("import_file_dialog"));
        int result = fileChooser.showDialog(this.getMainPanel(), platformStrings.getString("saveButtonText"));
        if(result == JFileChooser.APPROVE_OPTION){
            new Thread(() -> {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try{
                    ScheduleApp.getInstance().getDatabase().importDBFile(file, dataType, importPolicy);
                } catch (Exception e){
                    Toast toast = new Toast(ImportForm.this.getMainPanel(), strings.getString("import_db_error"));
                    toast.setDuration(500);
                    toast.display();
                }
            }).start();
        }
    }

    @Override
    public void onSetupLanguage(){
        setTitle(strings.getString("import_activity"));
        operationTypeLabel.setText(strings.getString("operation_type"));
        dataTypesLabel.setText(strings.getString("data_types"));
        importTypeLabel.setText(strings.getString("import_policy_type"));
        doOperationButton.setText(strings.getString("export_data"));
        backButton.setText(platformStrings.getString("back_button_text"));
    }

    @Override
    public void onCreateUI(){
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 2, new Insets(0, 10, 0, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        operationTypeLabel = new JLabel();
        operationTypeLabel.setHorizontalAlignment(0);
        panel1.add(operationTypeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        operationTypeBox = new JComboBox<>();
        panel1.add(operationTypeBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataTypesLabel = new JLabel();
        panel1.add(dataTypesLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataTypesBox = new JComboBox<>();
        panel1.add(dataTypesBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importTypeLabel = new JLabel();
        importTypeLabel.setVisible(false);
        panel1.add(importTypeLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importModeBox = new JComboBox<>();
        importModeBox.setVisible(false);
        panel1.add(importModeBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doOperationButton = new JButton();
        panel1.add(doOperationButton, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_arrow_back_36.png")));
        toolBar1.add(backButton);
        setMainPanel(mainPanel);
    }
}