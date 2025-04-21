package com.example.btl_iot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.data.repository.HistoryRepository;

public class HistoryViewModel extends AndroidViewModel {
    private HistoryRepository historyRepository;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepository = new HistoryRepository();
    }

    public LiveData<AuthRepository.Resource<HistoryResponse>> getHistory(long historyId, String token) {
        return historyRepository.getHistory(historyId, token);
    }
}