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
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.themes.ScheduleDesktopDarkTheme;
import com.ghostwalker18.scheduledesktop.themes.ScheduleDesktopLightTheme;
import com.ghostwalker18.scheduledesktop.views.MainForm;
import com.ghostwalker18.scheduledesktop.common.Application;
import com.sun.istack.NotNull;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * <h1>Schedule Desktop</h1>
 * <p>
 *      Программа представляет собой десктопную реализацию приложения расписания ПАСТ.
 * </p>
 *
 * @author  Ипатов Никита
 * @version  3.0
 */
public class ScheduleApp
        extends Application {
    public static final String DEVELOPER_EMAIL = "ghostwalker18@mail.ru";
    private static final Preferences preferences = Preferences.userNodeForPackage(ScheduleRepository.class);
    private final ScheduleRepository scheduleRepository;
    private final NotesRepository notesRepository;

    /**
     * Этот метод используется для создания экземпляра приложения
     * @return синглтон приложения
     */
    public static ScheduleApp getInstance(){
        try{
            if(instance == null)
                instance = new ScheduleApp();
            return (ScheduleApp) instance;
        } catch (Exception e){
            return null;
        }
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

    private ScheduleApp() {
        super();
        instance = this;
        AppDatabase db = AppDatabase.getInstance(AppDatabaseHibernateImpl.class);
        scheduleRepository = new ScheduleRepository(db,
                new NetworkService(ScheduleRepository.BASE_URI));
        notesRepository = new NotesRepository(db);
        scheduleRepository.update();
        frame.setIconImage(Toolkit.getDefaultToolkit()
                .createImage(ScheduleApp.class.getResource("/images/favicon.png")));
        startActivity(MainForm.class, null);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void setupTheme(){
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

    @Override
    protected void setupLanguage(){
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