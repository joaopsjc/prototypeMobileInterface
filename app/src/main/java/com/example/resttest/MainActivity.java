package com.example.resttest;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resttest.Retrofit.ApiClient;
import com.example.resttest.Retrofit.ApiInterface;
import com.example.resttest.Retrofit.Exemplo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected  void onCreate (Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_main);

    }
    private void getWeatherData(String cityName)
    {
        ApiInterface apiInterface =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Exemplo> call = apiInterface.getWeatherData(cityName);
        call.enqueue(new Callback<Exemplo>() {
            @Override
            public void onResponse(Call<Exemplo> call, Response<Exemplo> response) {
                Log.d("DATA", response.body().getMain().getTemperature());
            }

            @Override
            public void onFailure(Call<Exemplo> call, Throwable t) {

            }
        });
    }
}
