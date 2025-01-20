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

package com.ghostwalker18.scheduledesktop.database;

/**
 * Этот интерфейс задает операцию конверсии для использования в методе equals QueryCache.QueryArgs.
 *
 * @see QueryCache
 * @author Ипатов Никита.
 * @since 3.0
 */
public interface QueryArgConverter {

    /**
     * Этот метод преобразует поле класса QueryCache.QueryArgs к другому типу,
     * если по каким то причинам реализация метода equals для этого типа поля
     * не подходит в данном случае для корректного сравнения,
     * а использование кастомных типов не представляется возможным.
     * @param o значение поля класса
     * @return преобразованное значение
     */
    Object convertToQueryArg(Object o);
}