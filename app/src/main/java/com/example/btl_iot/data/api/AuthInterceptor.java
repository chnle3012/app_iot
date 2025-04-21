package com.example.btl_iot.data.api;

import android.content.Context;

import com.example.btl_iot.util.SharedPrefsUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Get token from SharedPreferences
        String token = SharedPrefsUtils.getAuthToken(context);
        
        // If token is null or empty, proceed with the original request
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }
        
        // Add token to the header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", token)
                .build();
        
        return chain.proceed(newRequest);
    }
} 