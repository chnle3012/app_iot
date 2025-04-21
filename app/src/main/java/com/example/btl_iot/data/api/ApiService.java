package com.example.btl_iot.data.api;

import com.example.btl_iot.data.model.LoginRequest;
import com.example.btl_iot.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
} 