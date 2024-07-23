package com.ghostwalker18.scheduledesktop;

import retrofit2.Retrofit;

import java.util.concurrent.Executors;

public class ScheduleRepository {
    private final ScheduleNetworkAPI api;
    private final String baseUri = "https://ptgh.onego.ru/9006/";

    public static class Status{
        public String text;
        public int progress;

        public Status(String text, int progress){
            this.text = text;
            this.progress = progress;
        }
    }

    public ScheduleRepository(){
        api = new Retrofit.Builder()
                .baseUrl(baseUri)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .build()
                .create(ScheduleNetworkAPI.class);
    }
}