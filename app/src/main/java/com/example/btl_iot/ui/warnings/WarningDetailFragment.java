package com.example.btl_iot.ui.warnings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.WarningResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.util.Constants;
import com.example.btl_iot.viewmodel.WarningViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WarningDetailFragment extends Fragment {

    private static final String ARG_WARNING_ID = "warningId";
    
    private WarningViewModel viewModel;
    private int warningId;
    private WarningResponse.Warning warningDetail;
    
    // UI components
    private ProgressBar progressBar;
    private ImageView warningIconImageView;
    private TextView warningIdTextView;
    private TextView warningTimestampTextView;
    private TextView warningMessageTextView;
    private TextView warningDetailsTextView;
    private ImageView warningPhotoImageView;
    private Button deleteButton;
    private Toolbar toolbar;

    public static WarningDetailFragment newInstance(int warningId) {
        WarningDetailFragment fragment = new WarningDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WARNING_ID, warningId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WarningViewModel.class);
        
        if (getArguments() != null) {
            warningId = getArguments().getInt(ARG_WARNING_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning_detail, container, false);
        
        // Initialize UI components
        progressBar = view.findViewById(R.id.progress_bar_detail);
        warningIconImageView = view.findViewById(R.id.img_warning_icon);
        warningIdTextView = view.findViewById(R.id.txt_warning_id);
        warningTimestampTextView = view.findViewById(R.id.txt_warning_timestamp);
        warningMessageTextView = view.findViewById(R.id.txt_warning_message);
        warningDetailsTextView = view.findViewById(R.id.txt_warning_details);
        warningPhotoImageView = view.findViewById(R.id.img_warning_photo);
        deleteButton = view.findViewById(R.id.btn_delete_warning);
        toolbar = view.findViewById(R.id.toolbar_warning_detail);
        
        setupToolbar();
        setupDeleteButton();
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (warningId > 0) {
            loadWarningDetail(warningId);
        } else {
            showError("ID cảnh báo không hợp lệ");
            navigateBack();
        }
    }
    
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> navigateBack());
    }
    
    private void setupDeleteButton() {
        deleteButton.setOnClickListener(v -> confirmDelete());
    }
    
    private void loadWarningDetail(int warningId) {
        progressBar.setVisibility(View.VISIBLE);
        
        // Lấy thông tin cảnh báo từ ViewModel
        warningDetail = viewModel.getSelectedWarning();
        
        if (warningDetail != null) {
            displayWarningDetail(warningDetail);
            progressBar.setVisibility(View.GONE);
        } else {
            // Nếu không có thông tin trong ViewModel, cần phải fetch từ API
            showError("Không tìm thấy thông tin cảnh báo");
            progressBar.setVisibility(View.GONE);
            navigateBack();
        }
    }
    
    private void displayWarningDetail(WarningResponse.Warning warning) {
        // Hiển thị ID cảnh báo
        warningIdTextView.setText(String.valueOf(warning.getWarningId()));
        
        // Hiển thị thời gian cảnh báo với định dạng đúng
        warningTimestampTextView.setText(formatTimestamp(warning.getTimestamp()));
        
        // Hiển thị thông tin cảnh báo
        warningMessageTextView.setText(warning.getInfo());
        
        // Hiển thị chi tiết cảnh báo (nếu có)
        if (warning.getInfo() != null && !warning.getInfo().isEmpty()) {
            warningDetailsTextView.setText(warning.getInfo());
        } else {
            warningDetailsTextView.setText("Không có thông tin chi tiết");
        }
        
        // Hiển thị ảnh cảnh báo (nếu có)
        if (warning.getImagePath() != null && !warning.getImagePath().isEmpty()) {
            Glide.with(requireContext())
                    .load(warning.getImagePath())
                    .placeholder(R.drawable.ic_warning)
                    .error(android.R.drawable.ic_menu_camera)
                    .centerInside()
                    .into(warningPhotoImageView);
        }
    }
    
    private String formatTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "Unknown time";
        }
        
        try {
            // Parse the original timestamp format (assuming it's in yyyy-MM-dd HH-mm-ss format)
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
            // Create the correct output format (HH:mm:ss)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            
            Date date = originalFormat.parse(timestamp);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            android.util.Log.e("WarningDetailFragment", "Error parsing timestamp: " + e.getMessage());
            // If parsing fails, return the original timestamp
        }
        
        return timestamp;
    }
    
    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa cảnh báo")
                .setMessage("Bạn có chắc chắn muốn xóa cảnh báo này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteWarning())
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteWarning() {
        if (warningDetail == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        
        // Lấy token xác thực
        String token = getAuthToken();
        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Không tìm thấy token. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        viewModel.deleteWarning(token, warningDetail.getWarningId()).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null && resource.getData().isSuccess()) {
                    // Xóa thành công
                    Toast.makeText(requireContext(), "Xóa cảnh báo thành công", Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    // Phản hồi từ server không thành công
                    String errorMessage = (resource.getData() != null) ? resource.getData().getMessage() : "Không xác định";
                    showError("Xóa cảnh báo thất bại: " + errorMessage);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                // Xảy ra lỗi khi gọi API
                showError("Xóa cảnh báo thất bại: " + resource.getMessage());
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
    
    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    private void navigateBack() {
        Navigation.findNavController(requireView()).navigateUp();
    }
} 