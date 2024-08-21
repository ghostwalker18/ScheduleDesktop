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
    private static final ResourceBundle languages = ResourceBundle.getBundle("languages", new XMLBundleControl());
    private static final ResourceBundle themes = ResourceBundle.getBundle("themes", new XMLBundleControl());
    private static final ResourceBundle strings = ResourceBundle.getBundle("strings", new XMLBundleControl());
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
    private JComboBox languageComboBox;
    private JButton saveButton;
    private JLabel languageLabel;
    public JPanel mainPanel;
    private JComboBox themeComboBox;
    private JLabel themeLabel;
    private JCheckBox doNotUpdateTimesCB;
    private JLabel doNotUpdateTimesL;

    public SettingsForm() {
        $$$setupUI$$$();
        setupLanguage();

        languageComboBox.setModel(new DefaultComboBoxModel(new Vector(languagesCodes
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

        themeComboBox.setModel(new DefaultComboBoxModel(new Vector(themesCodes
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
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
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
        mainPanel.add(languageComboBox, gbc);
        saveButton = new JButton();
        saveButton.setBackground(new Color(-10051327));
        saveButton.setEnabled(true);
        saveButton.setForeground(new Color(-1));
        saveButton.setHideActionText(false);
        saveButton.setText("Сохранить");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        mainPanel.add(saveButton, gbc);
        themeLabel = new JLabel();
        themeLabel.setHorizontalAlignment(10);
        themeLabel.setText("Тема");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        mainPanel.add(themeLabel, gbc);
        themeComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainPanel.add(themeComboBox, gbc);
        languageLabel = new JLabel();
        languageLabel.setText("Язык");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        mainPanel.add(languageLabel, gbc);
        doNotUpdateTimesL = new JLabel();
        doNotUpdateTimesL.setText("Не обновлять звонки");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(doNotUpdateTimesL, gbc);
        doNotUpdateTimesCB = new JCheckBox();
        doNotUpdateTimesCB.setForeground(new Color(-16249741));
        doNotUpdateTimesCB.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        mainPanel.add(doNotUpdateTimesCB, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}