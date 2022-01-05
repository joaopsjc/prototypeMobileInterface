package com.example.resttest.Retrofit;

import com.google.gson.annotations.SerializedName;

public class Principal {

    @SerializedName("temp")
    private String temperature;
    @SerializedName("humidity")
    private String humidity;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
