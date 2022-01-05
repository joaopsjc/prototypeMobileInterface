package com.example.resttest.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface
{
    @GET("weather?appid=95a420bb5d3a7e8c3e21c26f7b195452&units=metric")
    Call<Exemplo> getWeatherData(@Query("q") String name);
}
