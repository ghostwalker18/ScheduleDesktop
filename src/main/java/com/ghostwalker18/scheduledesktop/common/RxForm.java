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

import io.reactivex.rxjava3.disposables.Disposable;
import java.util.LinkedList;
import java.util.List;

/**
 * Этот класс является прототипом для форм, использующих RxJava.
 * Обеспечивает корректное освобождение ресурсов при уничтожении формы.
 *
 * @author Ипатов Никита
 */
public abstract class RxForm
        extends Form {
    private final List<Disposable> subscriptions = new LinkedList<>();

    /**
     * Этот метод должен использоваться при подписке на Observable для избежания последующей утечки памяти.
     * @param subscriptionResult результат подписки
     */
    protected void addSubscription(Disposable subscriptionResult){
        subscriptions.add(subscriptionResult);
    }

    @Override
    public void onDestroy(Bundle outState){
        for(Disposable subscription : subscriptions)
            if(subscription != null)
                subscription.dispose();
        super.onDestroy(outState);
    }
}