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

import java.util.HashMap;
import java.util.Map;

/**
 * Этот класс используется для хранения моделей представления.
 * Является аналогом класса из androidx.lifecycle.
 *
 * @author Ипатов Никита
 * @since 3.0
 */
public class ViewModelStore {
    private Map<String, ViewModel> map = new HashMap<>();

    /**
     * Этот метод помещает модель представления в хранилище.
     * @param key ключ для помещения
     * @param model модель
     */
    public void put(String key, ViewModel model){
        ViewModel oldModel = map.put(key,model);
        if(oldModel != null){
            oldModel.onCleared();
        }
    }

    /**
     * Этот метод позволяет получить модель представления из хранилища.
     * @param key ключ для извлечения
     * @return модель
     */
    public ViewModel get(String key){
        return map.get(key);
    }
}