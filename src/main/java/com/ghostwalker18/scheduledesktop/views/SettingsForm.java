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
import com.ghostwalker18.scheduledesktop.notifications.AppNotification;
import com.ghostwalker18.scheduledesktop.notifications.NotificationManagerWrapper;
import com.ghostwalker18.scheduledesktop.system.MouseClickAdapter;
import com.ghostwalker18.scheduledesktop.system.Toast;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.common.Form;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import static java.awt.Desktop.getDesktop;

/**
 * Этот класс представляет собой экран настроек приложения.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
public class SettingsForm
        extends Form {
    private static final ResourceBundle languages = ResourceBundle.getBundle("arrays/languages",
            new XMLBundleControl());
    private static final ResourceBundle themes = ResourceBundle.getBundle("arrays/themes",
            new XMLBundleControl());
    private static final ResourceBundle corpuses = ResourceBundle.getBundle("arrays/corpuses",
            new XMLBundleControl());
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private static final  ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private final ResourceBundle nonPublicStrings = ResourceBundle.getBundle("non_public_strings",
            new XMLBundleControl());
    private static final Map<String, String> languagesCodes = new HashMap<>();
    private static final Map<String, String> themesCodes = new HashMap<>();
    private static final Map<String, String> corpusesCodes = new HashMap<>();

    static {
        languagesCodes.put(languages.getString("ru"), "ru");
        languagesCodes.put(languages.getString("en"), "en");
        languagesCodes.put(languages.getString("be"), "be");
        languagesCodes.put(languages.getString("uk"), "uk");
        languagesCodes.put(languages.getString("kk"), "kk");

        themesCodes.put(themes.getString("dark"), "dark");
        themesCodes.put(themes.getString("light"), "light");

        corpusesCodes.put(corpuses.getString("all"), "all");
        corpusesCodes.put(corpuses.getString("first"), "first");
        corpusesCodes.put(corpuses.getString("second"), "second");
    }
    private final Preferences preferences = ScheduleApp.getPreferences();
    private JLabel scheduleSettingsL;
    private JLabel doNotUpdateTimesL;
    private JCheckBox doNotUpdateTimesCB;
    private JLabel networkSettingsL;
    private JLabel downloadForL;
    private JComboBox<String> downloadForComboBox;
    private JLabel enableCachingL;
    private JCheckBox enableCachingCB;
    private JLabel appSettingsL;
    private JLabel languageLabel;
    private JComboBox<String> languageComboBox;
    private JLabel themeLabel;
    private JComboBox<String> themeComboBox;
    private JLabel notificationSettings;
    private JLabel appUpdateNotificationL;
    private JCheckBox appUpdateCB;
    private JLabel scheduleUpdateNotificationL;
    private JCheckBox scheduleUpdateCB;
    private JButton saveButton;
    private JButton dataTransfer;
    private JButton shareButton;
    private JLabel copyright;
    private JButton backButton;

    /**
     * Этот метод используется для сохранения выбранных настроек
     */
    private void save() {
        boolean doNotUpdateTimes = doNotUpdateTimesCB.isSelected();
        preferences.putBoolean("doNotUpdateTimes", doNotUpdateTimes);

        String selectedLanguage = languageComboBox.getSelectedItem().toString();
        preferences.put("language", languagesCodes.get(selectedLanguage));

        String selectedTheme = themeComboBox.getSelectedItem().toString();
        preferences.put("theme", themesCodes.get(selectedTheme));

        String selectedDownloadFor = downloadForComboBox.getSelectedItem().toString();
        preferences.put("downloadFor", corpusesCodes.get(selectedDownloadFor));

        boolean enableCaching = enableCachingCB.isSelected();
        preferences.putBoolean("enableCaching", enableCaching);

        boolean enableUpdateNotifications = appUpdateCB.isSelected();
        preferences.putBoolean("update_notifications", enableUpdateNotifications);

        boolean enableScheduleNotifications = scheduleUpdateCB.isSelected();
        preferences.putBoolean("schedule_notifications", enableScheduleNotifications);
    }

    @Override
    public void onCreatedUI() {
        shareButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(ShareAppForm.class, null));

        languageComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(languagesCodes
                .keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList()))));
        String currentLanguage = languages.getString(preferences.get("language", "ru"));
        for (int i = 0; i < languageComboBox.getItemCount(); i++) {
            if (languageComboBox.getItemAt(i).equals(currentLanguage)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
        }

        themeComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(themesCodes
                .keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList()))));
        String currentTheme = themes.getString(preferences.get("theme", "light"));
        for (int i = 0; i < themeComboBox.getItemCount(); i++) {
            if (themeComboBox.getItemAt(i).equals(currentTheme)) {
                themeComboBox.setSelectedIndex(i);
                break;
            }
        }

        doNotUpdateTimesCB.setSelected(preferences.getBoolean("doNotUpdateTimes", true));

        downloadForComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(corpusesCodes
                .keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList()))));
        String currentDownloadMode = corpuses.getString(preferences.get("downloadFor", "all"));
        for (int i = 0; i < downloadForComboBox.getItemCount(); i++) {
            if (downloadForComboBox.getItemAt(i).equals(currentDownloadMode)) {
                downloadForComboBox.setSelectedIndex(i);
                break;
            }
        }

        enableCachingCB.setSelected(preferences.getBoolean("enableCaching", true));

        appUpdateCB.setSelected(preferences.getBoolean("update_notifications", false));

        scheduleUpdateCB.setSelected(preferences.getBoolean("schedule_notifications", false));

        //dataTransfer.addActionListener(e -> ScheduleApp.getInstance().startActivity(ImportForm.class, null));
        dataTransfer.addActionListener(e -> NotificationManagerWrapper.getInstance()
                .showNotification(new AppNotification(
                                0,
                                platformStrings.getString(
                                        "notifications_notification_schedule_update_channel_name"),
                                platformStrings.getString(
                                        "notifications_new_schedule_available"),
                                nonPublicStrings.getString(
                                        "notifications_notification_schedule_update_channel_id"),
                                platformStrings.getString(
                                        "notifications_notification_schedule_update_channel_name")
                        )
                ));

        saveButton.addActionListener(e -> {
            save();
            ScheduleApp.restartApplication();
            SwingUtilities.getWindowAncestor(getMainPanel()).dispose();
        });

        copyright.addMouseListener(new MouseClickAdapter() {
            @Override
            public void onClick() {
                try{
                    getDesktop().mail(new URI("mailto:"+ ScheduleApp.DEVELOPER_EMAIL
                            + "?subject=" + platformStrings.getString("email_subject")));
                } catch (Exception e) {
                    Toast toast = new Toast(SettingsForm.this.getMainPanel(),
                            platformStrings.getString("no_email_client_found"));
                    toast.display();
                    Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(new StringSelection(ScheduleApp.DEVELOPER_EMAIL), null);
                    toast = new Toast(SettingsForm.this.getMainPanel(),
                            platformStrings.getString("share_email_completed"));
                    toast.display();
                }
            }

            @Override
            public void onLongClick() {
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new StringSelection(ScheduleApp.DEVELOPER_EMAIL), null);
                Toast toast = new Toast(SettingsForm.this.getMainPanel(),
                        platformStrings.getString("share_email_completed"));
                toast.display();
            }
        });
        backButton.addActionListener(e -> ScheduleApp.getInstance().startActivity(MainForm.class, null));
    }

    @Override
    public void onSetupLanguage() {
        setTitle(strings.getString("settings"));
        scheduleSettingsL.setText(strings.getString("schedule_settings"));
        doNotUpdateTimesL.setText(strings.getString("option_do_not_update_times"));
        networkSettingsL.setText(strings.getString("network_settings"));
        downloadForL.setText(strings.getString("option_download_for"));
        enableCachingL.setText(strings.getString("option_enable_caching"));
        appSettingsL.setText(strings.getString("app_settings"));
        themeLabel.setText(strings.getString("option_theme"));
        languageLabel.setText(strings.getString("option_language"));
        notificationSettings.setText(platformStrings.getString("notifications"));
        appUpdateNotificationL.setText(
                platformStrings.getString("notifications_notification_app_update_channel_name"));
        scheduleUpdateNotificationL.setText(
                platformStrings.getString("notifications_notification_schedule_update_channel_name"));
        dataTransfer.setText(strings.getString("data_transfer"));
        saveButton.setText(platformStrings.getString("saveButtonText"));
        saveButton.setToolTipText(platformStrings.getString("save_button_tooltip"));
        shareButton.setText(platformStrings.getString("share_app"));
        backButton.setText(platformStrings.getString("back_button_text"));
        copyright.setToolTipText(platformStrings.getString("connect_to_developer_tooltip"));
    }

    @Override
    public void onCreateUI() {
        setMainPanel(new JPanel());
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc;

        final JToolBar toolBar1 = new JToolBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getMainPanel().add(toolBar1, gbc);
        backButton = new JButton();
        backButton.setIcon(new ImageIcon(getClass().getResource("/images/baseline_arrow_back_36.png")));
        toolBar1.add(backButton);

        scheduleSettingsL = new JLabel();
        scheduleSettingsL.setHorizontalAlignment(SwingConstants.CENTER);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        getMainPanel().add(scheduleSettingsL, gbc);

        doNotUpdateTimesL = new JLabel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        getMainPanel().add(doNotUpdateTimesL, gbc);

        doNotUpdateTimesCB = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(doNotUpdateTimesCB, gbc);

        networkSettingsL = new JLabel();
        networkSettingsL.setHorizontalAlignment(SwingConstants.CENTER);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        getMainPanel().add(networkSettingsL, gbc);

        downloadForL = new JLabel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        getMainPanel().add(downloadForL, gbc);

        downloadForComboBox = new JComboBox<>();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(downloadForComboBox, gbc);

        enableCachingL = new JLabel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        getMainPanel().add(enableCachingL, gbc);

        enableCachingCB = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(enableCachingCB, gbc);

        appSettingsL = new JLabel();
        appSettingsL.setHorizontalAlignment(SwingConstants.CENTER);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        getMainPanel().add(appSettingsL, gbc);

        languageLabel = new JLabel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        getMainPanel().add(languageLabel, gbc);
        
        languageComboBox = new JComboBox<>();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(languageComboBox, gbc);
        
        themeLabel = new JLabel();
        themeLabel.setHorizontalAlignment(10);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        getMainPanel().add(themeLabel, gbc);
        
        themeComboBox = new JComboBox<>();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(themeComboBox, gbc);

        notificationSettings = new JLabel();
        notificationSettings.setHorizontalAlignment(SwingConstants.CENTER);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        getMainPanel().add(notificationSettings, gbc);

        appUpdateNotificationL = new JLabel();
        appUpdateNotificationL.setHorizontalAlignment(10);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        getMainPanel().add(appUpdateNotificationL, gbc);

        appUpdateCB = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(appUpdateCB, gbc);

        scheduleUpdateNotificationL = new JLabel();
        scheduleUpdateNotificationL.setHorizontalAlignment(10);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        getMainPanel().add(scheduleUpdateNotificationL, gbc);

        scheduleUpdateCB = new JCheckBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(scheduleUpdateCB, gbc);

        dataTransfer = new JButton();
        dataTransfer.setEnabled(true);
        dataTransfer.setHideActionText(false);
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        getMainPanel().add(dataTransfer, gbc);

        saveButton = new JButton();
        saveButton.setEnabled(true);
        saveButton.setHideActionText(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        getMainPanel().add(saveButton, gbc);

        shareButton = new JButton();
        shareButton.setEnabled(true);
        saveButton.setHideActionText(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        getMainPanel().add(shareButton, gbc);

        copyright = new JLabel();
        copyright.setHorizontalAlignment(10);
        copyright.setText("2024 © Ипатов Никита");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        getMainPanel().add(copyright, gbc);
    }
}