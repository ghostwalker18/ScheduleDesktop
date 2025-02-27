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
import com.ghostwalker18.scheduledesktop.system.Toast;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.ghostwalker18.scheduledesktop.common.Form;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ResourceBundle;

/**
 * Этот класс представляет собой экран, где пользователь может поделиться ссылкой на приложение.
 *
 * @author Ипатов Никита
 */
public class ShareAppForm
        extends Form {
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private JLabel QRLabel;
    private JLabel OrLabel;
    private JButton shareButton;
    private JButton backButton;

    @Override
    public void onCreateUI() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_arrow_back_36.png")));
        toolBar1.add(backButton);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        QRLabel = new JLabel();
        panel1.add(QRLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel QR = new JLabel();
        QR.setIcon(new ImageIcon(getClass().getResource("/images/github_qr.png")));
        panel1.add(QR, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OrLabel = new JLabel();
        panel1.add(OrLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shareButton = new JButton();
        panel1.add(shareButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setMainPanel(mainPanel);
    }

    @Override
    public void onSetupLanguage() {
        setTitle(platformStrings.getString("share_app"));
        QRLabel.setText(platformStrings.getString("scan_qr_code"));
        OrLabel.setText(platformStrings.getString("or"));
        shareButton.setText(platformStrings.getString("share_link"));
        backButton.setText(platformStrings.getString("back_button_text"));
    }

    @Override
    public void onCreatedUI() {
        shareButton.addActionListener(e -> shareLink());
        backButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(SettingsForm.class, null));
    }

    /**
     * Этот метод используется для добавления ссылки на приложение в системный буфер обмена и
     * уведомления об этом.
     */
    private void shareLink(){
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(platformStrings.getString("github_link")), null);

        Toast message = new Toast(shareButton, platformStrings.getString("share_link_completed"));
        message.display();
    }
}