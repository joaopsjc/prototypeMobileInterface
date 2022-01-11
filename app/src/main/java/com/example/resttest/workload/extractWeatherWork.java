package com.example.resttest.workload;

import android.content.Context;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class extractWeatherWork extends Worker {

    public extractWeatherWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if (true) {
            return Result.success();
        } else {
            return Result.failure();
        }
    }

}