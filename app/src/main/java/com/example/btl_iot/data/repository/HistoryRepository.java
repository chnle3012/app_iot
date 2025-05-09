package com.example.btl_iot.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.data.api.ApiService;
import com.example.btl_iot.data.model.HistoryResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository {
    private ApiService apiService;

    public HistoryRepository() {
        this.apiService = ApiClient.getApiService();
    }

    // 5 tham số: token, page, limit, start, end
    public LiveData<AuthRepository.Resource<HistoryResponse>> getHistory(
            String token, Integer page, Integer limit, String start, String end
    ) {
        MutableLiveData<AuthRepository.Resource<HistoryResponse>> historyResult = new MutableLiveData<>();
        historyResult.setValue(AuthRepository.Resource.loading(null));

        apiService.getHistory(token, page, limit, start, end).enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    historyResult.setValue(AuthRepository.Resource.success(response.body()));
                } else {
                    String errorMessage = "Failed to fetch history. Error code: " + response.code();
                    historyResult.setValue(AuthRepository.Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                historyResult.setValue(AuthRepository.Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return historyResult;
    }
}
