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

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

/**
 * <h1>Schedule Desktop</h1>
 * <p>
 *      Программа представляет собой десктопную реализацию приложения расписания ПАСТ
 * </p>
 *
 * @автор Ипатов Никита
 * @версия 1.0
 */
public class Application {
    public static final String mondayTimesURL = "https://r1.nubex.ru/s1748-17b/47698615b7_fit-in~1280x800~filters:no_upscale()__f44488_08.jpg";
    public static final String otherTimesURL = "https://r1.nubex.ru/s1748-17b/320e9d2d69_fit-in~1280x800~filters:no_upscale()__f44489_bb.jpg";
    private static Application instance = null;
    private final ScheduleRepository repository = ScheduleRepository.getRepository();
    private final Preferences preferences = repository.getPreferences();
    private JFrame mainForm;

    public static Application getInstance() throws Exception{
        if(instance == null)
            instance = new Application();
        return instance;
    }

    private Application() throws Exception{
        repository.update();
        repository.getStatus().subscribe(System.out::println);
        FlatLightLaf.setup();
        AppDatabase database = AppDatabase.getInstance();
        database.getGroups().subscribe(System.out::print);
        mainForm = new JFrame("Расписание");
        mainForm.setPreferredSize(new Dimension(
                preferences.getInt("main_form_width", 800),
                preferences.getInt("main_form_height", 500)));
        mainForm.setIconImage(Toolkit.getDefaultToolkit()
                .createImage(Application.class.getResource("/images/favicon.gif")));
        mainForm.setContentPane(new MainForm().mainPanel);
        mainForm.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainForm.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                preferences.putInt("main_form_width", mainForm.getWidth());
                preferences.putInt("main_form_height", mainForm.getHeight());
                mainForm.dispose();
                System.exit(0);
            }
        });
        mainForm.pack();
        mainForm.setVisible(true);
    }

    public ScheduleRepository getRepository(){
        return repository;
    }

    public static void main(String[] args) throws Exception{
        Application app = Application.getInstance();
    }
}