package com.example.btl_iot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.model.ModeType;
import com.example.btl_iot.data.model.PiModeResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.data.repository.PiRepository;

public class PiViewModel extends AndroidViewModel {
    private PiRepository piRepository;
    private MutableLiveData<ModeType> currentMode = new MutableLiveData<>();

    public PiViewModel(@NonNull Application application) {
        super(application);
        piRepository = new PiRepository();
        
        // Default to SECURE mode
        currentMode.setValue(ModeType.SECURE);
    }

    public LiveData<ModeType> getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(ModeType mode) {
        currentMode.setValue(mode);
    }

    public LiveData<AuthRepository.Resource<PiModeResponse>> updatePiMode(ModeType mode, String token) {
        int piId = 1;
        return piRepository.updatePiMode(piId, mode.name(), token);
    }

    public boolean isSecureMode() {
        return ModeType.SECURE.equals(currentMode.getValue());
    }
} 