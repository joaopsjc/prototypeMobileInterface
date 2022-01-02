package com.example.resttest.Retrofit;

import com.google.gson.annotations.SerializedName;

public class Exemplo {

    @SerializedName("main")
    private Principal main;

    public Principal getMain() {
        return main;
    }

    public void setMain(Principal main) {
        this.main = main;
    }
}
