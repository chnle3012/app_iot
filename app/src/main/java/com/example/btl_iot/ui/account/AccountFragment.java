package com.example.btl_iot.ui.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.ModeType;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.ui.auth.LoginActivity;
import com.example.btl_iot.util.Constants;
import com.example.btl_iot.viewmodel.AuthViewModel;
import com.example.btl_iot.viewmodel.PiViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AccountFragment extends Fragment {

    private AuthViewModel authViewModel;
    private PiViewModel piViewModel;
    private Button btnLogout;
    private Button btnUpdateMode;
    private SwitchMaterial switchPiMode;
    private TextView tvCurrentMode;
    private ProgressBar progressBarUpdateMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        piViewModel = new ViewModelProvider(requireActivity()).get(PiViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize UI components
        btnLogout = view.findViewById(R.id.btn_logout);
        btnUpdateMode = view.findViewById(R.id.btn_update_mode);
        switchPiMode = view.findViewById(R.id.switch_pi_mode);
        tvCurrentMode = view.findViewById(R.id.tv_current_mode);
        progressBarUpdateMode = view.findViewById(R.id.progress_bar_update_mode);
        
        // Setup click listeners
        btnLogout.setOnClickListener(v -> logout());
        btnUpdateMode.setOnClickListener(v -> updatePiMode());
        
        // Setup switch listener
        switchPiMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update UI immediately for better UX
            updateModeUI(isChecked ? ModeType.SECURE : ModeType.FREE);
        });
        
        // Observe current mode
        piViewModel.getCurrentMode().observe(getViewLifecycleOwner(), mode -> {
            updateModeUI(mode);
        });
    }
    
    private void updateModeUI(ModeType mode) {
        tvCurrentMode.setText(mode.name());
        boolean isSecure = ModeType.SECURE.equals(mode);
        switchPiMode.setChecked(isSecure);
        
        // Update text color based on mode
        int colorRes = isSecure ? android.R.color.holo_green_dark : android.R.color.holo_orange_dark;
        tvCurrentMode.setTextColor(requireContext().getColor(colorRes));
    }
    
    private void updatePiMode() {
        ModeType newMode = switchPiMode.isChecked() ? ModeType.SECURE : ModeType.FREE;
        String token = getAuthToken();
        
        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy token. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show progress
        progressBarUpdateMode.setVisibility(View.VISIBLE);
        btnUpdateMode.setEnabled(false);
        
        // Call API to update mode
        piViewModel.updatePiMode(newMode, token).observe(getViewLifecycleOwner(), resource -> {
            // Hide progress
            progressBarUpdateMode.setVisibility(View.GONE);
            btnUpdateMode.setEnabled(true);
            
            if (resource == null) {
                Toast.makeText(requireContext(), "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                return;
            }
            
            switch (resource.getStatus()) {
                case SUCCESS:
                    if (resource.getData() != null && resource.getData().isSuccess()) {
                        // Update current mode in ViewModel
                        piViewModel.setCurrentMode(newMode);
                        
                        // Show success message
                        Toast.makeText(requireContext(), "Cập nhật chế độ thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = resource.getData() != null ? resource.getData().getMessage() : "Cập nhật thất bại";
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                    
                case ERROR:
                    Toast.makeText(requireContext(), "Lỗi: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
                    
                case LOADING:
                    // Already handled by showing progress bar
                    break;
            }
        });
    }
    
    private String getAuthToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String rawToken = prefs.getString(Constants.KEY_AUTH_TOKEN, null);
        if (rawToken != null) {
            return "Bearer " + rawToken;
        } else {
            return null;
        }
    }
    
    private void logout() {
        // Add logout functionality to AuthViewModel
        authViewModel.logout();
        
        // Show success message
        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        
        // Navigate back to login screen
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
} 