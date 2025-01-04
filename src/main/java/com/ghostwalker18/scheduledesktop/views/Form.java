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

package com.ghostwalker18.scheduledesktop.views;

import com.ghostwalker18.scheduledesktop.Bundle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Этот класс является базовым для всех экранных форм
 *
 * @author  Ипатов Никита
 */
public abstract class Form
        implements WindowListener {
    protected Bundle bundle;
    protected Dimension preferredSize = new Dimension(800, 500);
    private JPanel mainPanel = new JPanel();
    private String title = "Form";

    /**
     * Этот метод используется для получения основной (корневой) панели формы,
     * которая может быть использована для дальнейшего задания контента JFrame.
     * @return основная панель формы
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Этот метод используется для задания основной (корневой) панели формы.
     */
    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    /**
     * Этот метод возвращает заголовок формы.
     * @return заголовок формы
     */
    public String getTitle(){
        return title;
    }

    /**
     * Этот метод устанавливает заголовок формы
     * @param title заголовок формы
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * Этот метод возвращает предпочитаемый размер экранного окна для данной формы.
     * @return предпочитаемый размер формы
     */
    public Dimension getPreferredSize(){
        return preferredSize;
    }

    protected Form(Bundle bundle){
        this.bundle = bundle;
        onCreate(bundle);
        onCreateUIComponents();
        onCreateUI();
        onSetupLanguage();
        onCreatedUI();
    }

    /**
     * Этот метод используется для начальной инициализации формы.
     */
    protected void onCreate(Bundle bundle){/*To be overridden*/}

    /**
     * Этот метод используется для создания кастомных UI компоненетов формы.
     */
    protected void onCreateUIComponents(){/*To be overridden*/}

    /**
     * Этот метод используется для создания UI интерфейса формы.
     */
    abstract protected void onCreateUI();

    /**
     * Этот метод используется для настройки всех надписей UI интерфейса
     * с использованием строковых ресурсов.
     */
    protected void onSetupLanguage(){/*To be overridden*/}

    /**
     * Этот метод используется для настройки поведения формы после создания UI.
     */
    protected void onCreatedUI(){/*To be overridden*/}
    @Override
    public void windowOpened(WindowEvent e) {/*To be overridden*/}

    @Override
    public void windowClosing(WindowEvent e) {/*To be overridden*/}

    @Override
    public void windowClosed(WindowEvent e) {/*To be overridden*/}

    @Override
    public void windowIconified(WindowEvent e) {/*To be overridden*/}

    @Override
    public void windowDeiconified(WindowEvent e) {/*To be overridden*/}

    @Override
    public void windowActivated(WindowEvent e) {/*To be overridden*/}

    @Override
    public void windowDeactivated(WindowEvent e) {/*To be overridden*/}
}