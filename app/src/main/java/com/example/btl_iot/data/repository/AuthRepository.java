package com.example.btl_iot.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.data.api.ApiService;
import com.example.btl_iot.data.model.LoginRequest;
import com.example.btl_iot.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private ApiService apiService;

    public AuthRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<LoginResponse>> login(String username, String password) {
        MutableLiveData<Resource<LoginResponse>> loginResult = new MutableLiveData<>();
        
        // Show loading state
        loginResult.setValue(Resource.loading(null));
        
        // Create login request
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        // Make API call
        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        // Success response
                        loginResult.setValue(Resource.success(loginResponse));
                    } else {
                        // API returned an error message
                        loginResult.setValue(Resource.error(loginResponse.getMessage(), null));
                    }
                } else {
                    // HTTP error
                    String errorMsg = "Login failed. Please try again.";
                    loginResult.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Network or other error
                String errorMsg = "Network error: " + t.getMessage();
                loginResult.setValue(Resource.error(errorMsg, null));
            }
        });
        
        return loginResult;
    }
    
    // Resource class for handling loading, success, and error states
    public static class Resource<T> {
        public enum Status { SUCCESS, ERROR, LOADING }
        
        private final Status status;
        private final T data;
        private final String message;
        
        private Resource(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }
        
        public static <T> Resource<T> success(T data) {
            return new Resource<>(Status.SUCCESS, data, null);
        }
        
        public static <T> Resource<T> error(String msg, T data) {
            return new Resource<>(Status.ERROR, data, msg);
        }
        
        public static <T> Resource<T> loading(T data) {
            return new Resource<>(Status.LOADING, data, null);
        }
        
        public Status getStatus() {
            return status;
        }
        
        public T getData() {
            return data;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 