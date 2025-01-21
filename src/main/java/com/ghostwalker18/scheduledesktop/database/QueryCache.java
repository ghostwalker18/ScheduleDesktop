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

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Этот класс является прототипом для создания кэшей запросов к БД.
 * @param <T> тип класса аргументов запроса к БД. Должен наследовать к QueryArgs.
 * @param <R> тип возращаемого запросом значения
 * @see QueryArgs
 * @author Ипатов Никита
 */
public abstract class QueryCache<T extends QueryCache.QueryArgs, R> {
    private final Map<T, BehaviorSubject<R>> cachedResults = Collections.synchronizedMap(new HashMap<>());

    /**
     * Этот метод возвращает кэш запросов для данного типа запросов к БД, определяемых классом наследником.
     * @return кэщ запросов к БД
     */
    public final Map<T, BehaviorSubject<R>> getCache(){
        return cachedResults;
    }

    public final synchronized BehaviorSubject<R> cacheQuery(Class<T> clazz, Object... args){
        Class[] argsTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
        try{
            final T key = clazz.getConstructor(argsTypes).newInstance(args);
            if(!cachedResults.containsKey(key))
                cachedResults.put(key, BehaviorSubject.create());
            return cachedResults.get(key);
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * Этот класс является прототипом для класса аргументов запроса к БД.
     * При наследовании нужно определить конструктор класса и поля аргументов.
     */
    protected abstract static class QueryArgs {

        /**
         * Этот метод позволяет получить значения поля класа наследника.
         * Возможно задать операцию конверсии для приведения к нужному типу, отметив поле аннотацией Converter.
         * @param field поле класса
         * @return значение поля класса
         */
        private Object getFieldValue(Field field) {
            try {
                if(field.isAnnotationPresent(Converter.class)){
                    Converter converterAnnotation = field.getAnnotation(Converter.class);
                    QueryArgConverter converter = converterAnnotation.converter()
                            .getConstructor()
                            .newInstance();
                    return converter.convertToQueryArg(field.get(this));
                }
                return field.get(this);
            } catch (Exception e) {
                return null;
            }
        }

        /**
         * Этот метод представляет собой обобщенную версию метода equals.
         * @param o объект сравнения на равенство
         * @param <T> тип класса наследника
         * @return равны ли объекты
         */
        @SuppressWarnings("UNCHECKED_CAST")
        protected final synchronized <T extends QueryCache.QueryArgs> boolean t_equals(Object o){
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            T that = (T) o;
            boolean res = true;
            Field[] fieldsThat = that.getClass().getFields();
            Field[] fieldsThis = this.getClass().getFields();
            for(int i = 0; i < fieldsThis.length; i++){
                res &= getFieldValue(fieldsThis[i]).equals(getFieldValue(fieldsThat[i]));
            }
            return res;
        }

        @Override
        public final int hashCode(){
            synchronized (this){
                Object[] fieldValues = Arrays.stream(this.getClass().getFields()).map(this::getFieldValue).toArray();
                return Objects.hash(fieldValues);
            }
        }
    }
}