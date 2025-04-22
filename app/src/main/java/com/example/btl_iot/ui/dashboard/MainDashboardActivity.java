package com.example.btl_iot.ui.dashboard;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.btl_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Wait for view to be laid out to get NavController
        bottomNavigationView.post(() -> {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            // Connect the bottom navigation with the nav controller
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.navigation_history) {
            navController.navigate(R.id.navigation_history);
            return true;
        } else if (itemId == R.id.navigation_warnings) {
            navController.navigate(R.id.navigation_warnings);
            return true;
        } else if (itemId == R.id.navigation_people) {
            navController.navigate(R.id.navigation_people);
            return true;
        }
        
        return false;
    }
}