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

package com.ghostwalker18.scheduledesktop.common;

import com.sun.istack.Nullable;
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
        implements ViewModelOwner, WindowListener {
    protected Dimension preferredSize = new Dimension(800, 500);
    protected Bundle savedState;
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

    /**
     * Этот метод используется для начальной инициализации формы.
     */
    public void onCreate(@Nullable Bundle savedState, @Nullable Bundle bundle){
        this.savedState = savedState;
    }

    /**
     * Этот метод используется для создания кастомных UI компоненетов формы.
     */
    public void onCreateUIComponents(){/*To be overridden*/}

    /**
     * Этот метод используется для создания UI интерфейса формы.
     */
    public abstract void onCreateUI();

    /**
     * Этот метод используется для настройки всех надписей UI интерфейса
     * с использованием строковых ресурсов.
     */
    public void onSetupLanguage(){/*To be overridden*/}

    /**
     * Этот метод используется для настройки поведения формы после создания UI.
     */
    public void onCreatedUI(){/*To be overridden*/}

    /**
     * Этот метод используется для настройки поведения формы при ее уничтожении.
     */
    public void onDestroy(Bundle outState){}

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

    /**
     * Этот класс используется для создания форм заданного типа.
     *
     * @author Ипатов Никита
     */
    public static class FormFactory {

        /**
         * Этот метод возвращает сконфигурированную форму заданного типа.
         * @param formType тип формы
         * @param bundle передаваемые в форму данные
         * @return сконфигурированная форма
         */
        public Form createForm(Class<? extends Form> formType, Bundle savedState, Bundle bundle){
            try{
                Form newForm = formType.getConstructor().newInstance();
                newForm.onCreate(savedState, bundle);
                newForm.onCreateUIComponents();
                newForm.onCreateUI();
                newForm.onSetupLanguage();
                newForm.onCreatedUI();
                return newForm;
            } catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }
        }
    }
}