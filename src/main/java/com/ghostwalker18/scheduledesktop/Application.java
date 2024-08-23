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

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * <h1>Schedule Desktop</h1>
 * <p>
 *      Программа представляет собой десктопную реализацию приложения расписания ПАСТ.
 * </p>
 *
 * @author  Ипатов Никита
 * @version  1.0
 */
public class Application{
    private static Application instance = null;
    private final ScheduleRepository repository;
    private ResourceBundle strings;
    private ResourceBundle platformStrings;
    private final static Preferences preferences = Preferences.userNodeForPackage(ScheduleRepository.class);
    private final JFrame frame;

    /**
     * Этот метод используется для создания экземпляра приложения
     * @return синглтон приложения
     */
    public static Application getInstance(){
        if(instance == null)
            instance = new Application();
        return instance;
    }

    private Application(){
        setupTheme();
        setupLanguage();
        frame = new JFrame(strings.getString("app_name"));
        frame.setPreferredSize(new Dimension(
                preferences.getInt("main_form_width", 800),
                preferences.getInt("main_form_height", 500)));
        frame.setIconImage(Toolkit.getDefaultToolkit()
                .createImage(Application.class.getResource("/images/favicon.gif")));
        MainForm mainForm = new MainForm();
        frame.setContentPane(mainForm.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        //This shit is important: listeners are called in order they are added!!!
        frame.addWindowListener(mainForm);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                preferences.putInt("main_form_width", frame.getWidth());
                preferences.putInt("main_form_height", frame.getHeight());
                frame.dispose();
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
        repository = ScheduleRepository.getRepository();
        repository.update();
    }

    /**
     * Этот метод используется для настройки темы приложения. Должен вызываться
     * до создания каких-либо компонентов UI.
     */
    private void setupTheme(){
        FlatLaf.registerCustomDefaultsSource("themes");
        switch(preferences.get("theme", "light")){
            case "light":
                ScheduleDesktopLightTheme.setup();
                break;
            case "dark":
                ScheduleDesktopDarkTheme.setup();
                break;
        }
    }

    /**
     * Этот метод используется для задания языковых настроек приложения.
     */
    private void setupLanguage(){
        Locale locale = new Locale(preferences.get("language", "ru"));
        Locale.setDefault(locale);
        strings = ResourceBundle.getBundle("strings",
                new XMLBundleControl());
        platformStrings = ResourceBundle.getBundle("platform_strings",
                new XMLBundleControl());
        //localization of file chooser
        UIManager.put("FileChooser.lookInLabelText",
                        platformStrings.getString("lookInLabelText"));
        UIManager.put("FileChooser.filesOfTypeLabelText",
                        platformStrings.getString("filesOfTypeLabelText"));
        UIManager.put("FileChooser.folderNameLabelText",
                platformStrings.getString("folderNameLabelText"));
        UIManager.put("FileChooser.upFolderToolTipText",
                        platformStrings.getString("upFolderToolTipText"));
        UIManager.put("FileChooser.homeFolderToolTipText",
                platformStrings.getString("homeFolderToolTipText"));
        UIManager.put("FileChooser.newFolderToolTipText",
                platformStrings.getString("newFolderToolTipText"));
        UIManager.put("FileChooser.listViewButtonToolTipText",
                platformStrings.getString("listViewButtonToolTipText"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText",
                platformStrings.getString("detailsViewButtonToolTipText"));
        UIManager.put("FileChooser.saveButtonText",
                platformStrings.getString("saveButtonText"));
        UIManager.put("FileChooser.cancelButtonText",
                platformStrings.getString("cancelButtonText"));
    }

    /**
     * Этот метод используется для перезапуска приложения. Работает только для приложения,
     * упакованного в jar.
     */
    public static void restartApplication(){
        try{
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            /* is it a jar file? */
            if(!currentJar.getName().endsWith(".jar"))
                return;

            /* Build command: java -jar application.jar */
            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            System.exit(0);
        }
        catch (URISyntaxException | IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Этот метод используется для получения настроек приложения.
     * @return настройки приложения
     */
    public static Preferences getPreferences(){
        return preferences;
    }

    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args){
        Application app = Application.getInstance();
    }
}