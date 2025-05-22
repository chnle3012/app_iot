package com.example.btl_iot.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.LoginResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.service.MyFirebaseMessagingService;
import com.example.btl_iot.ui.dashboard.MainDashboardActivity;
import com.example.btl_iot.util.SharedPrefsUtils;
import com.example.btl_iot.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin, btnGoToRegister;
    private ProgressBar progressBar;
    
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();
        
        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Check if already logged in
        if (authViewModel.isLoggedIn()) {
            navigateToMainDashboard();
            return;
        }
        
        // Set up button click listeners
        setupListeners();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoToRegister = findViewById(R.id.btn_go_to_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        btnGoToRegister.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        // Clear previous errors
        tilUsername.setError(null);
        tilPassword.setError(null);

        // Get input values
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        boolean isValid = validateInput(username, password);

        if (isValid) {
            // Show progress
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            
            // Call login API
            authViewModel.login(username, password).observe(this, result -> {
                // Hide progress
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                
                if (result.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                    // Login successful
                    LoginResponse response = result.getData();
                    if (response != null && response.getData() != null) {
                        // Save token to SharedPreferences
                        String token = response.getData().getFormattedToken();
                        long expiration = response.getData().getExpiration();
                        authViewModel.saveAuthToken(token, expiration);
                        
                        // Send FCM token to server now that we have a JWT token
                        sendFCMTokenToServer();
                        
                        // Show success message
                        Toast.makeText(LoginActivity.this, 
                                "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to main screen
                        navigateToMainDashboard();
                    }
                } else if (result.getStatus() == AuthRepository.Resource.Status.ERROR) {
                    // Show error message
                    Toast.makeText(LoginActivity.this, 
                            result.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    
    private void sendFCMTokenToServer() {
        Log.d(TAG, "Attempting to send FCM token to server after login");
        
        // First check if we have a stored token
        String fcmToken = SharedPrefsUtils.getFCMToken(this);
        Log.d(TAG, "Current stored FCM token: " + fcmToken);
        
        if (fcmToken == null || fcmToken.isEmpty()) {
            // If we don't have a token, get one from Firebase
            Log.d(TAG, "No FCM token stored, requesting a new one");
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d(TAG, "New FCM token obtained: " + token);
                        
                        // Save token locally
                        SharedPrefsUtils.saveFCMToken(this, token);
                        
                        // Call the service to send the token to server
                        MyFirebaseMessagingService.sendStoredFCMTokenToServer(this);
                    });
        } else {
            // We have a token, send it to the server
            Log.d(TAG, "Using existing FCM token: " + fcmToken);
            MyFirebaseMessagingService.sendStoredFCMTokenToServer(this);
        }
    }
    
    private void navigateToMainDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validateInput(String username, String password) {
        boolean isValid = true;

        if (username.isEmpty()) {
            tilUsername.setError("Vui lòng nhập tên đăng nhập");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }

        return isValid;
    }
} 