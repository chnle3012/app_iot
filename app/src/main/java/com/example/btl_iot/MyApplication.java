package com.example.btl_iot;

import android.app.Application;
import android.content.Context;

import com.example.btl_iot.data.api.ApiClient;

public class MyApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        
        // Initialize the ApiClient with the application context
        ApiClient.init(appContext);
    }

    public static Context getAppContext() {
        return appContext;
    }
} 