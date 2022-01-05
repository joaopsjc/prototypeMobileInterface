package com.example.resttest.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerClient {
    private static Retrofit retrofit = null;
    public static Retrofit getClient()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl("/api-rest")
                    .addConverterFactory
                            (GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
