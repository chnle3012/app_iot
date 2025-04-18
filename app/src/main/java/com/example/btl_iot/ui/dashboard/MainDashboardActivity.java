package com.example.btl_iot.ui.dashboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_iot.R;

public class MainDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_dashboard_container, new MainDashboardFragment())
                    .commit();
        }
    }
}