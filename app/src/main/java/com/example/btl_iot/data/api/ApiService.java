package com.example.btl_iot.data.api;

import com.example.btl_iot.data.model.LoginRequest;
import com.example.btl_iot.data.model.LoginResponse;
import com.example.btl_iot.data.model.PeopleResponse;
import com.example.btl_iot.data.model.RegisterRequest;
import com.example.btl_iot.data.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
    
    @GET("api/people")
    Call<PeopleResponse> getPeople();
} 