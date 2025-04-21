package com.example.btl_iot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.btl_iot.data.model.LoginResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.util.SharedPrefsUtils;

public class AuthViewModel extends AndroidViewModel {
    
    private AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }
    
    public LiveData<AuthRepository.Resource<LoginResponse>> login(String username, String password) {
        return authRepository.login(username, password);
    }
    
    public void saveAuthToken(String token, long expiration) {
        SharedPrefsUtils.saveAuthToken(getApplication(), token, expiration);
    }
    
    public boolean isLoggedIn() {
        return SharedPrefsUtils.isLoggedIn(getApplication());
    }
} 