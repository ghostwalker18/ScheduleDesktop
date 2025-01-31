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

import com.sun.istack.NotNull;
import org.apache.commons.lang3.SystemUtils;


/**
 * Этот класс представляет собой надстройку над системными менеджерами уведомлений для удобства использования.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public abstract class NotificationManagerWrapper {
    public interface NotificationManager{

        /**
         * Этот метод позволяет удалить канал для получения push-уведомлений
         * @param channelId ID канала
         */
        void deleteNotificationChannel(String channelId);

        /**
         * Этот метод позволяет создать канал для получения push-уведомлений.
         * @param channelId ID канала
         * @param channelName имя канала
         */
        void createNotificationChannel(String channelId, String channelName, String channelDescription);

        /**
         * Этот метод позволяет показать push-уведомление.
         * @param data сообщение
         */
        void showNotification(@NotNull AppNotification data);
    }

    /**
     * Этот метод позволяет получить экзампляр менеджера push-уведомлений.
     * @return менеджер уведомлений
     */
    public static NotificationManager getInstance() {
        if (instance == null) {
            if(SystemUtils.IS_OS_LINUX)
                instance = new NotificationManagerLinux();
            if(SystemUtils.IS_OS_WINDOWS)
                instance = new NotificationManagerWindows();
        }
        return instance;
    }

    private static NotificationManager instance;
}