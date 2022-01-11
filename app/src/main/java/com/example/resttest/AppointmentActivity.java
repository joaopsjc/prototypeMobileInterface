package com.example.resttest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import com.example.resttest.DAO.ContractDAO;
import com.example.resttest.Retrofit.ApiClient;
import com.example.resttest.Retrofit.ApiInterface;
import com.example.resttest.Retrofit.Exemplo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity {
    private CalendarView appointmentCalendar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        appointmentCalendar = findViewById(R.id.appointCalendar); // get the reference of the calendar appointView

        appointmentCalendar.setMinDate(Calendar.getInstance().getTimeInMillis());

        appointmentCalendar.setOnDateChangeListener((calendarView, i, i1, i2) -> onDateClick());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onDateClick(){
        Log.d("DATA","working");

        AlertDialog.Builder alert = new AlertDialog.Builder(AppointmentActivity.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes", (dialog, which) -> {MarkAppointment();dialog.dismiss();})
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void MarkAppointment()
    {
        List<WeatherData> weatherDataList = getAllWeatherData();

        JSONObject weatherData = new JSONObject();
            try {
                for (WeatherData item:weatherDataList) {
                    weatherData.accumulate("Temperature",item.getTemperature());
                    weatherData.accumulate("Humidity",item.getHumidity());
                    weatherData.accumulate("TimeStamp",item.getTimestamp());
                }
                sendData(weatherData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void sendData(JSONObject weatherData) {


        Call<Exemplo> call = null;
        /*
        call.enqueue(new Callback<#formato#>() {
            @Override
            public void onResponse(Call<#formato#> call, Response<#formato#> response) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Exemplo> call, Throwable t) {

            }
        });*/
    }

    public List<WeatherData> getAllWeatherData()
    {
        ContractDAO.FeedReaderDbHelper dbHelper =
                new ContractDAO.FeedReaderDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                ContractDAO.ContractModel.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        List<WeatherData> weatherData = new ArrayList<>();
        while(cursor.moveToNext()) {
            String newTemperature = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContractDAO.
                            ContractModel.COLUMN_NAME_TEMPERATURE));
            String newHumidity = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContractDAO.
                            ContractModel.COLUMN_NAME_HUMIDITY));
            String newTimestamp = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContractDAO.
                            ContractModel.COLUMN_NAME_TIMESTAMP));
            Log.d("DATA",  newTemperature+"//"+newHumidity+"-"+newTimestamp);
            weatherData.add(new WeatherData(newTemperature, newHumidity,newTimestamp));
        }
        cursor.close();
        return weatherData;
    }
}