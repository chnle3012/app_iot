package com.example.btl_iot.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.data.api.ApiService;
import com.example.btl_iot.data.model.PeopleResponse;
import com.example.btl_iot.data.model.Person;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleRepository {
    private static final String TAG = "PeopleRepository";
    
    private final ApiService apiService;
    private final MutableLiveData<Resource<List<Person>>> peopleList = new MutableLiveData<>();

    public PeopleRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<Person>>> getPeopleList() {
        loadPeopleFromApi();
        return peopleList;
    }

    private void loadPeopleFromApi() {
        // Show loading state
        peopleList.setValue(Resource.loading(null));
        
        // Make API call
        apiService.getPeople().enqueue(new Callback<PeopleResponse>() {
            @Override
            public void onResponse(Call<PeopleResponse> call, Response<PeopleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PeopleResponse peopleResponse = response.body();
                    if (peopleResponse.isSuccess() && peopleResponse.getData() != null) {
                        List<Person> people = peopleResponse.getData().getContent();
                        peopleList.setValue(Resource.success(people));
                    } else {
                        String errorMsg = peopleResponse.getMessage() != null ? 
                                peopleResponse.getMessage() : "Error fetching people";
                        peopleList.setValue(Resource.error(errorMsg, null));
                    }
                } else {
                    peopleList.setValue(Resource.error("Error: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<PeopleResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                peopleList.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
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