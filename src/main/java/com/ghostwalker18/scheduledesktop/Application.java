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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

public class Application {
    private static Application application = null;
    private Preferences preferences = Preferences.userNodeForPackage(Application.class);
    private JFrame mainForm;

    public static Application getInstance() throws Exception{
        if(application == null)
            application = new Application();
        return application;
    }


    private Application() throws Exception{
        mainForm = new JFrame("Расписание");
        mainForm.setPreferredSize(new Dimension(
                preferences.getInt("main_form_width", 800),
                preferences.getInt("main_form_height", 500)));
        mainForm.setIconImage(Toolkit.getDefaultToolkit()
                .createImage(Application.class.getResource("/images/favicon.gif")));
        mainForm.setContentPane(new MainForm().mainPanel);
        mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainForm.addWindowStateListener(e -> {
            switch (e.getID()){
                case WindowEvent.WINDOW_CLOSING:
                    preferences.putInt("main_form_width", mainForm.getWidth());
                    preferences.putInt("main_form_height", mainForm.getHeight());
                    System.exit(0);
            }
        });
        mainForm.pack();
        mainForm.setVisible(true);
    }

    public Preferences getPreferences(){
        return preferences;
    }
    public static void main(String[] args) throws Exception{
        Application app = Application.getInstance();
    }
}