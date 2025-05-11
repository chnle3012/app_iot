package com.example.btl_iot.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.btl_iot.R;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword, tilConfirmPassword;
    private TextInputEditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister, btnBackToLogin;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Initialize views
        initViews();
        
        // Set up button click listeners
        setupListeners();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        
        btnBackToLogin.setOnClickListener(v -> {
            // Navigate back to LoginActivity
            finish();
        });
    }

    private void attemptRegister() {
        // Clear previous errors
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Get input values
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        boolean isValid = validateInput(username, password, confirmPassword);

        if (isValid) {
            // Show progress
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
            
            // Call register API
            authViewModel.register(username, password).observe(this, result -> {
                // Hide progress
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                
                if (result.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                    // Registration successful
                    String successMessage = result.getData() != null && result.getData().getMessage() != null 
                            ? result.getData().getMessage() 
                            : "Đăng ký thành công!";
                    
                    // Show success message
                    Toast.makeText(RegisterActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                    
                    // Navigate to login screen
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (result.getStatus() == AuthRepository.Resource.Status.ERROR) {
                    // Show error message
                    Toast.makeText(RegisterActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean validateInput(String username, String password, String confirmPassword) {
        boolean isValid = true;

        if (username.isEmpty()) {
            tilUsername.setError("Vui lòng nhập tên đăng nhập");
            isValid = false;
        } else if (username.length() < 4) {
            tilUsername.setError("Tên đăng nhập phải có ít nhất 4 ký tự");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError("Mật khẩu xác nhận không trùng khớp");
            isValid = false;
        }

        return isValid;
    }
} 