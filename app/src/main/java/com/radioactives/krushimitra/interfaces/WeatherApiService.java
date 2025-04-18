package com.radioactives.krushimitra.interfaces;

import com.radioactives.krushimitra.modal.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("data/2.5/forecast")
    Call<WeatherResponse> getForecast(
        @Query("q") String city,
        @Query("appid") String apiKey,
        @Query("units") String units  // metric = °C
    );
}
