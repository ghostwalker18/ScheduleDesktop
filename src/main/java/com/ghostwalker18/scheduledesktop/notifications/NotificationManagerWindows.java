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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Этот класс представляет собой реализацию менеджера уведомлений для Windows.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class NotificationManagerWindows
        implements NotificationManagerWrapper.NotificationManager{
    private final Map<String, TrayIcon> iconMap = new HashMap<>();

    @Override
    public void deleteNotificationChannel(String channelId) {
        SystemTray.getSystemTray().remove(iconMap.remove(channelId));
    }

    @Override
    public void createNotificationChannel(String channelId, String channelName, String channelDescription) {
        try{
            Image image = Toolkit.getDefaultToolkit().createImage(
                    getClass().getResource("/images/favicon.png"));
            TrayIcon trayIcon = new TrayIcon(image, channelDescription);
            iconMap.put(channelId, trayIcon);
            trayIcon.setImageAutoSize(true);
            SystemTray.getSystemTray().add(trayIcon);
        } catch (Exception ignored){/*Not required*/}
    }

    @Override
    public void showNotification(AppNotification data) {
        try{
            TrayIcon trayIcon = iconMap.get(data.getChannelId());
            trayIcon.displayMessage(data.getTitle(), data.getMessage(), TrayIcon.MessageType.INFO);
        } catch (Exception ignored){/*Not required*/}
    }
}