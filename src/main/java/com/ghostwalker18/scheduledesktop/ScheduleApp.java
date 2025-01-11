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
import com.ghostwalker18.scheduledesktop.database.AppDatabase;
import com.ghostwalker18.scheduledesktop.database.AppDatabaseHibernateImpl;
import com.ghostwalker18.scheduledesktop.models.NotesRepository;
import com.ghostwalker18.scheduledesktop.models.ScheduleRepository;
import com.ghostwalker18.scheduledesktop.network.NetworkService;
import com.ghostwalker18.scheduledesktop.views.Form;
import com.ghostwalker18.scheduledesktop.views.MainForm;
import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
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
public class ScheduleApp {
    private static final Preferences preferences = Preferences.userNodeForPackage(ScheduleRepository.class);
    private static ScheduleApp instance = null;
    private final ScheduleRepository scheduleRepository;
    private final NotesRepository notesRepository;
    private final JFrame frame;
    private final Map<String, Bundle> formStates = new HashMap<>();
    private Form currentForm;

    /**
     * Этот метод используется для создания экземпляра приложения
     * @return синглтон приложения
     */
    public static ScheduleApp getInstance(){
        if(instance == null)
            instance = new ScheduleApp();
        return instance;
    }

    /**
     * Этот метод используется для перезапуска приложения. Работает только для приложения,
     * упакованного в jar.
     */
    public static void restartApplication(){
        try{
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(ScheduleApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());

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
        catch (URISyntaxException | IOException ignored ){ /*ignored*/}
    }

    /**
     * Этот метод используется для получения настроек приложения.
     * @return синглтон настроек приложения
     */
    public static Preferences getPreferences(){
        return preferences;
    }

    /**
     * Этот метод используется для получения репозитория расписания приложения.
     * @return синглтон репозитория расписания
     */
    public ScheduleRepository getScheduleRepository(){
        return scheduleRepository;
    }

    /**
     * Этот метод используется для получения репозитория заметок приложения.
     * @return синглтон репозитория заметок
     */
    public NotesRepository getNotesRepository(){
        return notesRepository;
    }

    /**
     * Этот метод используется для отображения новой формы на экране
     */
    public void startActivity(Class<? extends Form> formType, Bundle bundle){
        Form newForm = new Form.FormFactory().createForm(formType, formStates.get(formType.getCanonicalName()), bundle);
        if(newForm != null){
            if(currentForm != null){
                frame.removeWindowListener(currentForm);
                Bundle outState = new Bundle();
                currentForm.onDestroy(outState);
                formStates.put(currentForm.getClass().getCanonicalName(), outState);
            }
            frame.setTitle(newForm.getTitle());
            frame.setPreferredSize(newForm.getPreferredSize());
            frame.addWindowListener(newForm);
            frame.setContentPane(newForm.getMainPanel());
            currentForm = newForm;
        }
    }

    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args){
        System.setProperty("sun.java2d.uiScale", "1");
        ScheduleApp app = ScheduleApp.getInstance();
    }

    private ScheduleApp() {
        instance = this;
        setupTheme();
        setupLanguage();
        AppDatabase db = AppDatabase.getInstance(AppDatabaseHibernateImpl.class);
        scheduleRepository = new ScheduleRepository(db,
                new NetworkService(ScheduleRepository.BASE_URI));
        notesRepository = new NotesRepository(db);
        scheduleRepository.update();
        frame = new JFrame();
        frame.setIconImage(Toolkit.getDefaultToolkit()
                .createImage(ScheduleApp.class.getResource("/images/favicon.png")));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        startActivity(MainForm.class, null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                currentForm.onDestroy(new Bundle());
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
        RxJavaPlugins.setErrorHandler(e -> {
                System.err.println(e.getMessage());
        });
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
        ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
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
}