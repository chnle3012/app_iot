package com.example.btl_iot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.btl_iot.data.model.DeleteWarningResponse;
import com.example.btl_iot.data.model.WarningResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.data.repository.WarningRepository;

public class WarningViewModel extends AndroidViewModel {
    private WarningRepository warningRepository;
    private WarningResponse.Warning selectedWarning;

    public WarningViewModel(@NonNull Application application) {
        super(application);
        warningRepository = new WarningRepository(); // Khởi tạo WarningRepository
    }

    // 5 tham số: token, page, limit, start, end
    public LiveData<AuthRepository.Resource<WarningResponse>> getWarnings(
            String token, Integer page, Integer limit, String start, String end
    ) {
        return warningRepository.getWarning(token, page, limit, start, end);
    }

    public void setSelectedWarning(WarningResponse.Warning warning) {
        this.selectedWarning = warning;
    }

    public WarningResponse.Warning getSelectedWarning() {
        return selectedWarning;
    }

    public LiveData<AuthRepository.Resource<DeleteWarningResponse>> deleteWarning(String token, int warningId) {
        return warningRepository.deleteWarning(token, warningId);
    }
}
