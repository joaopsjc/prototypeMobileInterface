package com.example.resttest.workload;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.resttest.DAO.ContractDAO;
import com.example.resttest.Retrofit.ApiClient;
import com.example.resttest.Retrofit.ApiInterface;
import com.example.resttest.Retrofit.Exemplo;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExtractWeatherWork extends Worker implements LocationListener {
    String cityName, lastHumidity, lastTemperature;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };
    private static final int INITIAL_REQUEST = 1337;
    private LocationManager locationManager;
    private Context context;

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public ExtractWeatherWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this. context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, this);

        setLastWeatherData();
    }

    @NonNull
    @Override
    public Result doWork() {
        extractWeatherData();
        return Result.success();
    }

    public void insertData(String temperature, String humidity)
    {
        ContractDAO.FeedReaderDbHelper dbHelper =
                new ContractDAO.FeedReaderDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String timestamp = year + "/" + month + "/" + day + "-" +
                hour + ":00";
        values.put(ContractDAO.ContractModel.COLUMN_NAME_TIMESTAMP,timestamp);
        values.put(ContractDAO.ContractModel.COLUMN_NAME_TEMPERATURE, temperature);
        values.put(ContractDAO.ContractModel.COLUMN_NAME_HUMIDITY, humidity);

        // Insert the new row, returning the primary key value of the new row
        db.insert(ContractDAO.ContractModel.TABLE_NAME, null, values);
    }

    public boolean hasWeatherChanged(String temperature, String humidity)
    {
        return !temperature.equals(lastTemperature) || !humidity.equals(lastHumidity);
    }

    private void extractWeatherData(){
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
                    if(hasWeatherChanged(temperature,humidity))
                    {
                        lastHumidity = humidity;
                        lastTemperature = temperature;
                        insertData(temperature,humidity);
                    }
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

    public void setLastWeatherData()
    {
        ContractDAO.FeedReaderDbHelper dbHelper =
                new ContractDAO.FeedReaderDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ContractDAO.ContractModel.COLUMN_NAME_TIMESTAMP + " DESC";
        String limit = "1";

        Cursor cursor = db.query(
                ContractDAO.ContractModel.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder,               // The sort order
                limit                   // amount of rows
        );
        while(cursor.moveToNext()) {
            lastTemperature = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContractDAO.
                            ContractModel.COLUMN_NAME_TEMPERATURE));
            lastHumidity = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContractDAO.
                            ContractModel.COLUMN_NAME_HUMIDITY));
        }
        cursor.close();
    }

    private void setCityName(double latitude, double longitude){
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getSubAdminArea().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        setCityName(latitude, longitude);
    }
}