package com.example.btl_iot.data.api;

import com.example.btl_iot.data.model.AddPersonResponse;
import com.example.btl_iot.data.model.LoginRequest;
import com.example.btl_iot.data.model.LoginResponse;
import com.example.btl_iot.data.model.PeopleResponse;
import com.example.btl_iot.data.model.PersonDetailResponse;
import com.example.btl_iot.data.model.RegisterRequest;
import com.example.btl_iot.data.model.RegisterResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
    
    @GET("api/people")
    Call<PeopleResponse> getPeople();
    
    @GET("api/people/{peopleId}")
    Call<PersonDetailResponse> getPersonDetail(@Path("peopleId") int peopleId);
    
    @Multipart
    @POST("api/people")
    Call<AddPersonResponse> addPerson(
            @Part("name") RequestBody name,
            @Part("age") RequestBody age,
            @Part MultipartBody.Part file
    );
    
    @Multipart
    @PUT("api/people/{peopleId}")
    Call<AddPersonResponse> updatePerson(
            @Path("peopleId") int peopleId,
            @Part("name") RequestBody name,
            @Part("age") RequestBody age,
            @Part MultipartBody.Part file
    );
} 