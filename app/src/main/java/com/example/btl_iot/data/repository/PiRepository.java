package com.example.btl_iot.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.data.api.ApiService;
import com.example.btl_iot.data.model.PiModeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PiRepository {
    private ApiService apiService;

    public PiRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<AuthRepository.Resource<PiModeResponse>> updatePiMode(int piId, String mode, String token) {
        MutableLiveData<AuthRepository.Resource<PiModeResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading(null));

        apiService.updatePiMode(piId, mode, token).enqueue(new Callback<PiModeResponse>() {
            @Override
            public void onResponse(Call<PiModeResponse> call, Response<PiModeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(AuthRepository.Resource.success(response.body()));
                } else {
                    String errorMessage = "Failed to update Pi mode. Error code: " + response.code();
                    result.setValue(AuthRepository.Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<PiModeResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
} 