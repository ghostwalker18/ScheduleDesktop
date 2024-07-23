package com.ghostwalker18.scheduledesktop;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ScheduleNetworkAPI {
    @GET
    Call<ResponseBody> getScheduleFile(@Url String url);
}
