package com.ghostwalker18.scheduledesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.prefs.Preferences;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
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