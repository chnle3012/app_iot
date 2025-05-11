package com.example.btl_iot.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.btl_iot.R;
import com.example.btl_iot.ui.history.HistoryFragment;
import com.example.btl_iot.ui.warnings.WarningsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainDashboardFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_dashboard, container, false);

        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_history) {
                loadFragment(new HistoryFragment());
                return true;
            } else if (itemId == R.id.navigation_warnings) {
                loadFragment(new WarningsFragment());
                return true;
            }
            return false;
        });

        // Load default fragment (HistoryFragment)
        loadFragment(new HistoryFragment());

        return view;
    }

    private void loadFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)  // Sửa ở đây
                .commit();
    }
}
