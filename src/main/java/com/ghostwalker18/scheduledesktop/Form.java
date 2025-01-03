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

/**
 * Этот класс является базовым для всех экранных форм
 *
 * @author  Ипатов Никита
 */
public abstract class Form {
    private JPanel mainPanel;

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

    protected Form(){
        createUIComponents();
        setupUI();
        setupLanguage();
    }

    /**
     * Этот метод используется для создания кастомных UI компоненетов формы.
     */
    protected void createUIComponents(){/*To be overridden*/}

    /**
     * Этот метод используется для создания UI интерфейса формы.
     */
    abstract protected void setupUI();

    /**
     * Этот метод используется для настройки всех надписей UI интерфейса
     * с использованием строковых ресурсов.
     */
    protected void setupLanguage(){/*To be overridden*/};
}