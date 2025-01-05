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
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
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
    private ResourceBundle platformStrings;
    private JLabel QRLabel;
    private JLabel OrLabel;
    private JButton shareButton;

    protected ShareAppForm(Bundle bundle) {
        super(bundle);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        platformStrings = ResourceBundle.getBundle("platform_strings",
                new XMLBundleControl());
    }

    @Override
    protected void onCreateUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        QRLabel = new JLabel();
        mainPanel.add(QRLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JLabel QR = new JLabel();
        QR.setIcon(new ImageIcon(getClass().getResource("/images/github_qr.png")));
        mainPanel.add(QR, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        OrLabel = new JLabel();
        mainPanel.add(OrLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shareButton = new JButton();
        mainPanel.add(shareButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setMainPanel(mainPanel);
    }

    @Override
    protected void onSetupLanguage() {
        QRLabel.setText(platformStrings.getString("scan_qr_code"));
        OrLabel.setText(platformStrings.getString("or"));
        shareButton.setText(platformStrings.getString("share_link"));
    }

    @Override
    protected void onCreatedUI() {
        shareButton.addActionListener(e -> shareLink());
    }

    /**
     * Этот метод используется для добавления ссылки на приложение в системный буфер обмена и
     * уведомления об этом.
     */
    private void shareLink(){
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(platformStrings.getString("github_link")), null);

        Toast message = new Toast(shareButton, platformStrings.getString("share_times_completed"));
        message.display();
    }
}