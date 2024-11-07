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

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * Этот класс представляет собой экран настроек приложения.
 *
 * @author Ипатов Никита
 */
public class SettingsForm {
    private static final ResourceBundle languages = ResourceBundle.getBundle("languages",
            new XMLBundleControl());
    private static final ResourceBundle themes = ResourceBundle.getBundle("themes",
            new XMLBundleControl());
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings",
            new XMLBundleControl());
    private static final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    public static final HashMap<String, String> languagesCodes = new HashMap<>();
    public static final HashMap<String, String> themesCodes = new HashMap<>();

    static {
        languagesCodes.put(languages.getString("ru"), "ru");
        languagesCodes.put(languages.getString("en"), "en");
        languagesCodes.put(languages.getString("be"), "be");
        languagesCodes.put(languages.getString("uk"), "uk");
        languagesCodes.put(languages.getString("kk"), "kk");
    }

    static {
        themesCodes.put(themes.getString("dark"), "dark");
        themesCodes.put(themes.getString("light"), "light");
    }

    private final Preferences preferences = Application.getPreferences();
    private JComboBox<String> languageComboBox;
    private JButton saveButton;
    private JLabel languageLabel;
    private JPanel mainPanel;
    private JComboBox<String> themeComboBox;
    private JLabel themeLabel;
    private JCheckBox doNotUpdateTimesCB;
    private JLabel doNotUpdateTimesL;

    public SettingsForm() {
        $$$setupUI$$$();
        setupLanguage();

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

        saveButton.addActionListener(e -> {
            save();
            Application.restartApplication();
            SwingUtilities.getWindowAncestor(getMainPanel()).dispose();
        });
    }

    /**
     * Этот метод используется для сохранения выбранных настроек
     */
    private void save() {
        String selectedLanguage = languageComboBox.getSelectedItem().toString();
        preferences.put("language", languagesCodes.get(selectedLanguage));

        String selectedTheme = themeComboBox.getSelectedItem().toString();
        preferences.put("theme", themesCodes.get(selectedTheme));

        boolean doNotUpdateTimes = doNotUpdateTimesCB.isSelected();
        preferences.putBoolean("doNotUpdateTimes", doNotUpdateTimes);
    }

    /**
     * Этот метод используется для настройки всех надписей на экране, используя строковые ресурсы.
     */
    private void setupLanguage() {
        themeLabel.setText(strings.getString("option_theme"));
        languageLabel.setText(strings.getString("option_language"));
        doNotUpdateTimesL.setText(strings.getString("option_do_not_update_times"));
        saveButton.setText(platformStrings.getString("saveButtonText"));
        saveButton.setToolTipText(platformStrings.getString("save_button_tooltip"));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        setMainPanel(new JPanel());
        getMainPanel().setLayout(new GridBagLayout());
        languageComboBox = new JComboBox();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(languageComboBox, gbc);
        saveButton = new JButton();
        saveButton.setEnabled(true);
        saveButton.setHideActionText(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        getMainPanel().add(saveButton, gbc);
        themeLabel = new JLabel();
        themeLabel.setHorizontalAlignment(10);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        getMainPanel().add(themeLabel, gbc);
        themeComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        getMainPanel().add(themeComboBox, gbc);
        languageLabel = new JLabel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        getMainPanel().add(languageLabel, gbc);
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
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }
}