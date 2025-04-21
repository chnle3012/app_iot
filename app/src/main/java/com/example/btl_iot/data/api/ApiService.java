package com.example.btl_iot.data.api;

import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.data.model.LoginRequest;
import com.example.btl_iot.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/history/{historyId}")
    Call<HistoryResponse> getHistory(
            @Path("historyId") long historyId,
            @Header("Authorization") String token
    );
}