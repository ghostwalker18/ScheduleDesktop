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

package com.ghostwalker18.scheduledesktop.notifications;

import com.ghostwalker18.scheduledesktop.ScheduleApp;
import com.ghostwalker18.scheduledesktop.network.AppUpdateNetworkAPI;
import com.ghostwalker18.scheduledesktop.network.NetworkService;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import java.util.ResourceBundle;

/**
 * Этот класс представляет собой работу
 * по уведомлению пользователя о наличии новой версии мобильного и десктопного приложения.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class AppUpdateNotificationTask
        implements Runnable {
    private final AppUpdateNetworkAPI api;
    private static final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private static final ResourceBundle nonPublicStrings = ResourceBundle.getBundle("non_public_strings",
            new XMLBundleControl());

    public AppUpdateNotificationTask(NetworkService service){
        api = service.getUpdateAPI();
    }
    private final String latestDesktopUpdateVersion = ScheduleApp.getPreferences().get("latest_desktop_update", "2.3");
    private final String latestMobileUpdateVersion = ScheduleApp.getPreferences().get("latest_mobile_update", "4.0");
    @Override
    public void run() {
        try{
            String latestDesktopUpdateAvailable = api.getLatestDesktopReleaseInfo().execute().body().getTagName();
            if(latestDesktopUpdateAvailable.compareTo(latestDesktopUpdateVersion) > 0){
                NotificationManagerWrapper.getInstance().showNotification(new AppNotification(
                        0,
                        platformStrings.getString("notifications_notification_app_update_channel_name"),
                        platformStrings.getString("notifications_new_desktop_update"),
                        nonPublicStrings.getString("notifications_notification_app_update_channel_id"),
                        platformStrings.getString("notifications_notification_app_update_channel_name")
                ));
                ScheduleApp.getPreferences().put("latest_desktop_update", latestDesktopUpdateAvailable);
            }
            String latestMobileUpdateAvailable = api.getLatestMobileReleaseInfo().execute().body().getTagName();
            if(latestMobileUpdateAvailable.compareTo(latestMobileUpdateVersion) > 0){
                NotificationManagerWrapper.getInstance().showNotification(new AppNotification(
                        0,
                        platformStrings.getString("notifications_notification_app_update_channel_name"),
                        platformStrings.getString("notifications_new_mobile_update"),
                        nonPublicStrings.getString("notifications_notification_app_update_channel_id"),
                        platformStrings.getString("notifications_notification_app_update_channel_name")
                ));
                ScheduleApp.getPreferences().put("latest_mobile_update", latestMobileUpdateAvailable);
            }
        } catch (Exception ignored){/*Not required*/}
    }
}