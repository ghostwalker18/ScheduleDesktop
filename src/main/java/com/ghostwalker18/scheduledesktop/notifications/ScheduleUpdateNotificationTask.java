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
import com.ghostwalker18.scheduledesktop.models.ScheduleRepository;
import com.ghostwalker18.scheduledesktop.system.XMLBundleControl;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Этот класс представляет собой работу
 * по получению нового расписания в фоновом режиме
 * и уведомления пользователя о результате работы.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class ScheduleUpdateNotificationTask
        implements Runnable{
    private final ResourceBundle platformStrings = ResourceBundle.getBundle("platform_strings",
            new XMLBundleControl());
    private final ResourceBundle nonPublicStrings = ResourceBundle.getBundle("non_public_strings",
            new XMLBundleControl());

    @Override
    public void run() {
        final ScheduleRepository repository = ScheduleApp.getInstance().getScheduleRepository();
        ScheduleRepository.UpdateResult lastUpdateResult = repository.getUpdateResult();
        Calendar lastAvailableDate = repository.getLastKnownLessonDate(repository.getSavedGroup());
        repository.update();
        try{
            ScheduleRepository.UpdateResult updateResult = repository.onUpdateCompleted().get();
            if(lastUpdateResult != ScheduleRepository.UpdateResult.FAIL
                    && updateResult == ScheduleRepository.UpdateResult.FAIL){
                NotificationManagerWrapper.getInstance()
                        .showNotification(new AppNotification(
                                        0,
                                        platformStrings.getString(
                                                "notifications_notification_schedule_update_channel_name"),
                                        platformStrings.getString(
                                                "notifications_schedule_unavailable"),
                                        nonPublicStrings.getString(
                                                "notifications_notification_schedule_update_channel_id"),
                                        platformStrings.getString(
                                                "notifications_notification_schedule_update_channel_name")
                                )
                        );
            }
            Calendar currentAvailableDate = repository.getLastKnownLessonDate(repository.getSavedGroup());
            if(currentAvailableDate.after(lastAvailableDate)){
                NotificationManagerWrapper.getInstance()
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
                        );
            }
        }
        catch (InterruptedException | ThreadDeath e){
            Thread.currentThread().interrupt();
        }
        catch (Exception ignored){/*Not required*/}
    }
}