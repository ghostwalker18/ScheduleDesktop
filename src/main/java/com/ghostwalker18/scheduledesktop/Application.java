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

import com.ghostwalker18.scheduledesktop.views.Form;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Этот класс является прототипом многоформенного GUI приложения Swing.
 *
 * @author Ипатов Никита
 */
public abstract class Application {
    protected static Application instance = null;
    protected final JFrame frame = new JFrame();
    private final Map<String, Bundle> formStates = new HashMap<>();
    protected Form currentForm;

    protected Application(){
        setupTheme();
        setupLanguage();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                currentForm.onDestroy(new Bundle());
                System.exit(0);
            }
        });
    }

    /**
     * Этот метод используется для отображения новой формы на экране
     */
    public final void startActivity(Class<? extends Form> formType, Bundle bundle){
        Form newForm = new Form.FormFactory().createForm(formType, formStates.get(formType.getCanonicalName()), bundle);
        if(newForm != null){
            SwingUtilities.invokeLater(()->{
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
                frame.revalidate();
                frame.repaint();
                currentForm = newForm;
            });
        }
    }

    /**
     * Этот метод используется для перезапуска приложения. Работает только для приложения,
     * упакованного в jar.
     */
    public static void restartApplication(){
        try{
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

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
     * Этот метод используется для настройки темы приложения. Должен вызываться
     * до создания каких-либо компонентов UI.
     */
    protected abstract void setupTheme();

    /**
     * Этот метод используется для задания языковых настроек приложения.
     */
    protected abstract void setupLanguage();
}