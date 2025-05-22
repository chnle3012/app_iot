package com.example.btl_iot.data.api;

import com.example.btl_iot.data.model.AddPersonResponse;
import com.example.btl_iot.data.model.DeleteHistoryResponse;
import com.example.btl_iot.data.model.DeletePersonResponse;
import com.example.btl_iot.data.model.DeleteWarningResponse;
import com.example.btl_iot.data.model.FCMTokenRequest;
import com.example.btl_iot.data.model.FCMTokenResponse;
import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.data.model.LoginRequest;
import com.example.btl_iot.data.model.LoginResponse;
import com.example.btl_iot.data.model.PeopleResponse;
import com.example.btl_iot.data.model.PersonDetailResponse;
import com.example.btl_iot.data.model.PiModeResponse;
import com.example.btl_iot.data.model.RegisterRequest;
import com.example.btl_iot.data.model.RegisterResponse;
import com.example.btl_iot.data.model.WarningResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
            @Part("identificationId") RequestBody identificationId,
            @Part("gender") RequestBody gender,
            @Part("birthday") RequestBody birthday,
            @Part MultipartBody.Part file
    );

    @Multipart
    @PUT("api/people/{peopleId}")
    Call<AddPersonResponse> updatePerson(
            @Path("peopleId") int peopleId,
            @Part("name") RequestBody name,
            @Part("gender") RequestBody gender,
            @Part("birthday") RequestBody birthday,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @PUT("api/people/{peopleId}")
    Call<AddPersonResponse> updatePersonWithoutImage(
            @Path("peopleId") int peopleId,
            @Field("name") String name,
            @Field("gender") String gender,
            @Field("birthday") String birthday
    );

    @DELETE("api/people/{peopleId}")
    Call<DeletePersonResponse> deletePerson(@Path("peopleId") int peopleId);

    @GET("api/history")
    Call<HistoryResponse> getHistory(
            @Header("Authorization") String token,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("start") String start,
            @Query("end") String end
    );
    
    @DELETE("api/history/{historyId}")
    Call<DeleteHistoryResponse> deleteHistory(
            @Path("historyId") int historyId,
            @Header("Authorization") String token
    );

    @GET("api/warning")
    Call<WarningResponse> getWarning(
            @Header("Authorization") String token,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("start") String start,
            @Query("end") String end
    );
    
    @PUT("api/pi/{piId}/mode")
    Call<PiModeResponse> updatePiMode(
            @Path("piId") int piId,
            @Query("mode") String mode,
            @Header("Authorization") String token
    );

    @DELETE("api/warning/{warningId}")
    Call<DeleteWarningResponse> deleteWarning(
        @Header("Authorization") String token, 
        @Path("warningId") int warningId);
        
    @PUT("api/auth/me/token")
    Call<FCMTokenResponse> updateFCMToken(
        @Header("Authorization") String token,
        @Body FCMTokenRequest fcmTokenRequest);
}
