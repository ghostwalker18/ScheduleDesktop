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
import com.ghostwalker18.scheduledesktop.notifications.AppUpdateNotificationTask;
import com.ghostwalker18.scheduledesktop.notifications.NotificationManagerWrapper;
import com.ghostwalker18.scheduledesktop.notifications.ScheduleUpdateNotificationTask;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import com.ghostwalker18.scheduledesktop.themes.ScheduleDesktopDarkTheme;
import com.ghostwalker18.scheduledesktop.themes.ScheduleDesktopLightTheme;
import com.ghostwalker18.scheduledesktop.views.MainForm;
import com.ghostwalker18.scheduledesktop.common.Application;
import com.sun.istack.NotNull;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
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
        extends Application
        implements PreferenceChangeListener {
    public static final String DEVELOPER_EMAIL = "ghostwalker18@mail.ru";
    private static final Preferences preferences = Preferences.userNodeForPackage(ScheduleRepository.class);
    private static final  ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private static final  ResourceBundle nonPublicStrings = ResourceBundle.getBundle("non_public_strings",
            new XMLBundleControl());
    private final AppDatabase db;
    private final NetworkService service;
    private final ScheduleRepository scheduleRepository;
    private final NotesRepository notesRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> scheduleUpdateFuture;
    private ScheduledFuture<?> appUpdateFuture;

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

    /**
     * Этот метод используется для получения БД приложения.
     * @return синглтон БД
     */
    public AppDatabase getDatabase(){
        return db;
    }

    private ScheduleApp() {
        super();
        instance = this;
        db = AppDatabase.getInstance(AppDatabaseHibernateImpl.class);
        preferences.addPreferenceChangeListener(this);
        service = new NetworkService(ScheduleRepository.BASE_URI);
        scheduleRepository = new ScheduleRepository(db, service);
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

    @Override
    public void preferenceChange(@NotNull PreferenceChangeEvent evt) {
        boolean enabled;
        switch (evt.getKey()){
            case "update_notifications":
                enabled = preferences.getBoolean("update_notifications", false);
                if(enabled){
                    NotificationManagerWrapper.getInstance().createNotificationChannel(
                            nonPublicStrings.getString("notifications_notification_app_update_channel_id"),
                            platformStrings.getString("notifications_notification_app_update_channel_name"),
                            platformStrings.getString("notifications_notification_app_update_channel_descr")
                    );
                    appUpdateFuture = scheduler.scheduleAtFixedRate(
                            new AppUpdateNotificationTask(service), 1, 1, TimeUnit.MINUTES);
                }
                else{
                    NotificationManagerWrapper.getInstance()
                            .deleteNotificationChannel(
                                    nonPublicStrings.getString("notifications_notification_app_update_channel_id"));
                    if(appUpdateFuture != null)
                        appUpdateFuture.cancel(false);
                }
                break;
            case "schedule_notifications":
                enabled = preferences.getBoolean("schedule_notifications", false);
                if(enabled){
                    NotificationManagerWrapper.getInstance().createNotificationChannel(
                            nonPublicStrings.getString("notifications_notification_schedule_update_channel_id"),
                            platformStrings.getString("notifications_notification_schedule_update_channel_name"),
                            platformStrings.getString("notifications_notification_schedule_update_channel_descr")
                    );
                    scheduleUpdateFuture = scheduler.scheduleAtFixedRate(
                            new ScheduleUpdateNotificationTask(), 1, 1, TimeUnit.MINUTES);
                }
                else{
                    NotificationManagerWrapper.getInstance()
                            .deleteNotificationChannel(
                                    nonPublicStrings.getString("notifications_notification_schedule_update_channel_id"));
                    if(scheduleUpdateFuture != null)
                        scheduleUpdateFuture.cancel(false);
                }

        }
    }
}