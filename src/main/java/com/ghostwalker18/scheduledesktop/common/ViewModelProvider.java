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

/**
 * Этот класс используется для предоставления моделей представления активностям приложения.
 * Является упрощенным аналогом класса из androidx.lifecycle.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class ViewModelProvider {
    private final ViewModelStore store;

    public ViewModelProvider(ViewModelOwner owner){
        this.store = owner.store;
    }

    /**
     * Этот метод позволяет получить существующую или новую модель представления из хранилища
     * @param modelClass класс модели представления для получения
     * @return модель представления
     */
    public <T extends ViewModel> T get(Class<T> modelClass){
        return get(String.format("DEFAULT_KEY:%s", modelClass.getCanonicalName()), modelClass);
    }

    @SuppressWarnings("UNCHECKED_CAST")
    public <T extends ViewModel> T get(String key, Class<T> modelClass){
        ViewModel model = store.get(key);
        if(modelClass.isInstance(model) ){
            return (T) model;
        }
        try {
            T newModel = modelClass.getConstructor().newInstance();
            store.put(key, newModel);
            return newModel;
        } catch (Exception e){
            return null;
        }
    }
}