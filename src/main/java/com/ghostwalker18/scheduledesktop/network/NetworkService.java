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

package com.ghostwalker18.scheduledesktop.network;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import com.ghostwalker18.scheduledesktop.ScheduleApp;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Этот класс используется для предоставления приложению услуг доступа к сети.
 *
 * @author Ipatov Nikita
 * @since 2.3
 */
public class NetworkService {
    private static final long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
    private final String baseUri;
    private final Preferences preferences = ScheduleApp.getPreferences();

    public NetworkService(String baseUri){
        this.baseUri = baseUri;
    }

    /**
     * Этот метод позволяет получить API сайта ПТГХ.
     * @return API сайта для доступа к скачиванию файлов расписания
     */
    public ScheduleNetworkAPI getScheduleAPI(){
        Retrofit.Builder apiBuilder = new Retrofit.Builder()
                .baseUrl(baseUri)
                .callbackExecutor(Executors.newFixedThreadPool(4))
                .addConverterFactory(new JsoupConverterFactory());

        boolean isCachingEnabled = preferences.getBoolean("isCachingEnabled", true);
        if(isCachingEnabled){
            try{
                Cache cache = new Cache(new File(getClass().getResource("/cache").getPath()), SIZE_OF_CACHE);
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .cache(cache)
                        .addInterceptor(new CacheInterceptor())
                        .build();
                apiBuilder.client(client);
            } catch (Exception e){
                System.err.println("Cannot enable caching: " + e.getMessage());
            }
        }

        return apiBuilder
                .build()
                .create(ScheduleNetworkAPI.class);
    }

    /**
     * Этот метод позволяет получить API GitHub репозитория.
     * @return API для доступа к проверке наличия обновлений.
     */
    public AppUpdateNetworkAPI getUpdateAPI(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit.Builder apiBuilder = new Retrofit.Builder()
                .baseUrl("https://api.github.com/repos/ghostwalker18")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(Executors.newFixedThreadPool(2));
        return apiBuilder
                .build()
                .create(AppUpdateNetworkAPI.class);
    }
}