package com.example.resttest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.resttest.Retrofit.ApiClient;
import com.example.resttest.Retrofit.ApiInterface;
import com.example.resttest.Retrofit.Exemplo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };
    private static final int INITIAL_REQUEST=1337;

    protected LocationManager locationManager;
    Button search_btn;

    String cityName = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search_btn = findViewById(R.id.searchBtn);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS,INITIAL_REQUEST);
            }
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        search_btn.setOnClickListener(view -> getWeatherData());
    }

    private void getWeatherData(){
        ApiInterface apiInterface =
                ApiClient.getClient().create(ApiInterface.class);
        Log.d("DATA",cityName);
        Call<Exemplo> call = apiInterface.getWeatherData(cityName);

        call.enqueue(new Callback<Exemplo>() {
            @Override
            public void onResponse(Call<Exemplo> call, Response<Exemplo> response) {
                try {
                    String temperature = response.body().getMain().getTemperature();
                    String humidity = response.body().getMain().getHumidity();
                    Log.d("DATA", temperature + "//"+humidity);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("DATA",response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Exemplo> call, Throwable t) {

            }
        });
    }

    private void setCityName(double latitude, double longitude){
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getSubAdminArea().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        setCityName(latitude, longitude);
    }

    @Override
    public void onLocationChanged(@NonNull List<android.location.Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
