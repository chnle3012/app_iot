package com.example.btl_iot.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.btl_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainDashboardActivity";
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
        
        // Xử lý intent từ thông báo
        handleNotificationIntent(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Xử lý intent khi ứng dụng đã mở
        handleNotificationIntent(intent);
    }
    
    private void handleNotificationIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        
        Log.d(TAG, "Handling notification intent: " + intent);
        
        // Kiểm tra xem intent có phải từ thông báo không
        if (intent.hasExtra("OPEN_WARNINGS")) {
            Log.d(TAG, "Navigating to warnings screen from notification");
            
            // Mở tab Warnings
            if (navController != null) {
                navController.navigate(R.id.navigation_warnings);
                bottomNavigationView.setSelectedItemId(R.id.navigation_warnings);
                
                // Lấy warningId từ intent
                String warningIdString = intent.getStringExtra("warningId");
                int warningIdValue = -1;
                
                if (warningIdString != null && !warningIdString.isEmpty()) {
                    try {
                        warningIdValue = Integer.parseInt(warningIdString);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing warningId: " + warningIdString, e);
                    }
                }
                
                // Nếu có ID hợp lệ thì mở chi tiết cảnh báo
                final int finalWarningId = warningIdValue;
                if (finalWarningId > 0) {
                    // Delay nhỏ để đảm bảo fragment đã sẵn sàng
                    bottomNavigationView.postDelayed(() -> {
                        try {
                            // Tạo bundle với ID cảnh báo
                            Bundle args = new Bundle();
                            args.putInt("warningId", finalWarningId);
                            
                            // Mở chi tiết cảnh báo
                            navController.navigate(R.id.action_navigation_warnings_to_warningDetail, args);
                        } catch (Exception e) {
                            Log.e(TAG, "Error navigating to warning detail", e);
                        }
                    }, 300);
                }
            }
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