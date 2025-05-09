package com.example.btl_iot.ui.dashboard;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
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

        // Khởi tạo NavController từ NavHostFragment (cách an toàn)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            // Kết nối bottom navigation với navController
            // Dùng sự kiện thay đổi điểm đến để cập nhật bottom nav
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                
                // Nếu là một trong các tab chính, cập nhật bottom nav tương ứng
                if (id == R.id.navigation_history || 
                    id == R.id.navigation_warnings ||
                    id == R.id.navigation_people ||
                    id == R.id.navigation_account) {
                    
                    // Cập nhật bottom nav nhưng không kích hoạt sự kiện onClick
                    bottomNavigationView.setOnNavigationItemSelectedListener(null);
                    
                    if (id == R.id.navigation_history) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_history);
                    } else if (id == R.id.navigation_warnings) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_warnings);
                    } else if (id == R.id.navigation_people) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_people);
                    } else if (id == R.id.navigation_account) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_account);
                    }
                    
                    // Gắn lại listener
                    bottomNavigationView.setOnNavigationItemSelectedListener(this);
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (navController == null) {
            return false;
        }
        
        if (itemId == R.id.navigation_history) {
            navController.navigate(R.id.navigation_history);
            return true;
        } else if (itemId == R.id.navigation_warnings) {
            navController.navigate(R.id.navigation_warnings);
            return true;
        } else if (itemId == R.id.navigation_people) {
            navController.navigate(R.id.navigation_people);
            return true;
        } else if (itemId == R.id.navigation_account) {
            navController.navigate(R.id.navigation_account);
            return true;
        }
        
        return false;
    }
}