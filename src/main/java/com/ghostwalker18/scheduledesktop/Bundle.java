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

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Этот класс представляет собой аналог класса Bundle для Android.
 *
 * @author Ипатов Никита
 */
public final class Bundle {
    private Map<String, Object> map = new HashMap<>();

    /**
     * Этот метод позволяет сохранить значение типа String.
     * @param key ключ для сохранения
     * @param string строка для сохранения
     */
    public void putString(@NotNull String key, @Nullable String string){
        map.put(key, string);
    }

    /**
     * Этот метод позволяет сохранить значение типа Integer.
     * @param key ключ для сохранения
     * @param integer число для сохранения
     */
    public void putInt(@NotNull String key, @Nullable Integer integer){
        map.put(key, integer);
    }

    /**
     * Этот метод позволяет сохранить значение типа Boolean.
     * @param key ключ для сохранения
     * @param bool число для сохранения
     */
    public void putBoolean(@NotNull String key, @Nullable Boolean bool){
        map.put(key, bool);
    }

    /**
     * Этот метод позволяет получить значение типа String.
     * @param key ключ для сохранения
     * @return сохраненая строка
     */
    @Nullable
    public String getString(@NotNull String key){
        Object o = map.get(key);
        if(o instanceof String)
            return (String)o;
        else
            return null;
    }

    /**
     * Этот метод позволяет получить значение типа Integer.
     * @param key ключ для сохранения
     * @return сохраненное число
     */
    @Nullable
    public Integer getInt(@NotNull String key){
        Object o = map.get(key);
        if(o instanceof Integer)
            return (Integer) o;
        else
            return null;
    }

    /**
     * Этот метод позволяет получить значение типа Boolean.
     * @param key ключ для сохранения
     * @return сохраненное логическое значение
     */
    @Nullable
    public Boolean getBoolean(@NotNull String key){
        Object o = map.get(key);
        if(o instanceof Boolean)
            return (Boolean) o;
        else
            return null;
    }
}