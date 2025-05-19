package com.example.btl_iot.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.data.api.ApiService;
import com.example.btl_iot.data.model.DeleteWarningResponse;
import com.example.btl_iot.data.model.WarningResponse;
import com.example.btl_iot.data.repository.AuthRepository.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

public class WarningRepository {
    private final ApiService apiService;

    public WarningRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<WarningResponse>> getWarning(
            String token, Integer page, Integer limit, String start, String end
    ) {
        MutableLiveData<Resource<WarningResponse>> warningResult = new MutableLiveData<>();
        warningResult.setValue(Resource.loading(null));

        apiService.getWarning(token, page, limit, start, end).enqueue(new Callback<WarningResponse>() {
            @Override
            public void onResponse(Call<WarningResponse> call, Response<WarningResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    warningResult.setValue(Resource.success(response.body()));
                    System.out.println("Số lượng warning nhận được: " + response.body().getData().getTotalElements() + " trong tổng số " + response.body().getData().getTotalPages() + " trang");

                } else {
                    String errorMessage = "Failed to fetch warnings. Error code: " + response.code();
                    warningResult.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<WarningResponse> call, Throwable t) {
                warningResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return warningResult;
    }

    public LiveData<Resource<DeleteWarningResponse>> deleteWarning(String token, int warningId) {
        MutableLiveData<Resource<DeleteWarningResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        
        apiService.deleteWarning(token, warningId).enqueue(new Callback<DeleteWarningResponse>() {
            @Override
            public void onResponse(Call<DeleteWarningResponse> call, Response<DeleteWarningResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    try {
                        Gson gson = new Gson();
                        DeleteWarningResponse errorResponse = gson.fromJson(response.errorBody().string(), DeleteWarningResponse.class);
                        result.setValue(Resource.error(errorResponse.getMessage(), errorResponse));
                    } catch (Exception e) {
                        result.setValue(Resource.error("Lỗi xử lý phản hồi: " + e.getMessage(), null));
                    }
                }
            }
            
            @Override
            public void onFailure(Call<DeleteWarningResponse> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        
        return result;
    }
}