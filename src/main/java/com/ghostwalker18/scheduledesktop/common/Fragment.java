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

import javax.swing.*;

/**
 * Этот класс является прототипом для кастомных элементов GUI со сложной внутренней логикой.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public abstract class Fragment
        extends JPanel
        implements ViewModelOwner {
    private final Form parentForm;
    private final ViewModelStore store = new ViewModelStore();

    /**
     * Этот метод используется для создания UI интерфейса фрагмента.
     */
    public abstract void onCreateUI();

    protected Fragment(Form form){
        parentForm = form;
    }

    @Override
    public ViewModelStore getViewModelStore(){
        return store;
    }

    /**
     * Этот метод возвращает родительскую форму данного фрагмента.
     * @return родительская форма
     */
    public final Form getParentForm() {
        return parentForm;
    }

    /**
     * Этот метод используется для начальной инициализации фрагмента.
     * @param bundle передаваемые данные
     */
    public void onCreate(Bundle bundle){/*To be overridden*/}

    /**
     * Этот метод используется для настройки всех надписей UI интерфейса
     * с использованием строковых ресурсов.
     */
    public void onSetupLanguage(){/*To be overridden*/}

    /**
     * Этот метод используется для настройки поведения фрагмента после создания UI.
     */
    public void onCreatedUI(){/*To be overridden*/}

    /**
     * Этот класс используется для создания фрагментов заданного типа.
     *
     * @author Ипатов Никита
     */
    public static class FragmentFactory {
        public <T extends Fragment> T create(Form owner, Class<T> fragmentClass, Bundle bundle){
            try{
                T fragment = fragmentClass.getConstructor(Form.class).newInstance(owner);
                fragment.onCreate(bundle);
                fragment.onCreateUI();
                fragment.onSetupLanguage();
                fragment.onCreatedUI();
                return fragment;
            } catch (Exception e){
                return null;
            }
        }
    }
}