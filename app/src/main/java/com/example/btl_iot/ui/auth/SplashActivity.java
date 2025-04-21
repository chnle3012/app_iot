package com.example.btl_iot.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_iot.MainActivity;
import com.example.btl_iot.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Delay for a few seconds and then move to the next screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start MainActivity or LoginActivity depending on your app flow
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close this activity to prevent going back to splash
        }, SPLASH_DELAY);
    }
} 