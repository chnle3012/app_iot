package com.example.btl_iot.data.api;

import android.content.Context;

import com.example.btl_iot.util.Constants;
import com.example.btl_iot.MyApplication;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static ApiService apiService;
    private static Context context;

    public static void init(Context appContext) {
        context = appContext;
    }

    public static synchronized ApiService getApiService() {
        if (context == null) {
            // If not initialized with init(), try to get application context
            context = MyApplication.getAppContext();
        }
        
        if (apiService == null) {
            apiService = getRetrofitInstance().create(ApiService.class);
        }
        return apiService;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Add AuthInterceptor to add token to requests
            AuthInterceptor authInterceptor = new AuthInterceptor(context);
            
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
} 