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

import java.io.File;
import java.util.concurrent.Executors;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class NetworkService {
    private static final long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
    private final String baseUri;

    public NetworkService(String baseUri){
        this.baseUri = baseUri;
    }

    public IScheduleNetworkAPI getScheduleAPI(){
        Cache cache = new Cache(new File(this.getClass().getResource("/cache/http").getPath()), SIZE_OF_CACHE);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .cache(cache)
                .addInterceptor(new CacheInterceptor())
                .build();
        IScheduleNetworkAPI api = new Retrofit.Builder()
                .baseUrl(baseUri)
                .callbackExecutor(Executors.newFixedThreadPool(4))
                .client(client)
                .addConverterFactory(new JsoupConverterFactory())
                .build()
                .create(IScheduleNetworkAPI.class);
        return api;
    }
}