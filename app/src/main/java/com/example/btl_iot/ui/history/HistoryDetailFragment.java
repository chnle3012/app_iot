package com.example.btl_iot.ui.history;

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
import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.util.Constants;
import com.example.btl_iot.viewmodel.HistoryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryDetailFragment extends Fragment {

    private static final String ARG_HISTORY_ID = "historyId";
    
    private HistoryViewModel viewModel;
    private int historyId;
    private HistoryResponse.History historyDetail;
    
    // UI components
    private ProgressBar progressBar;
    private ImageView personPhotoImageView;
    private TextView personNameTextView;
    private TextView personAgeTextView;
    private TextView personIdTextView;
    private TextView historyIdTextView;
    private TextView timestampTextView;
    private TextView modeTextView;
    private ImageView eventPhotoImageView;
    private Button deleteButton;
    private Toolbar toolbar;

    public static HistoryDetailFragment newInstance(int historyId) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HISTORY_ID, historyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        
        if (getArguments() != null) {
            historyId = getArguments().getInt(ARG_HISTORY_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_detail, container, false);
        
        // Initialize UI components
        progressBar = view.findViewById(R.id.progress_bar_detail);
        personPhotoImageView = view.findViewById(R.id.img_person_photo);
        personNameTextView = view.findViewById(R.id.txt_person_name);
        personAgeTextView = view.findViewById(R.id.txt_person_age);
        personIdTextView = view.findViewById(R.id.txt_person_id);
        historyIdTextView = view.findViewById(R.id.txt_history_id);
        timestampTextView = view.findViewById(R.id.txt_timestamp);
        modeTextView = view.findViewById(R.id.txt_mode);
        eventPhotoImageView = view.findViewById(R.id.img_event_photo);
        deleteButton = view.findViewById(R.id.btn_delete_history);
        toolbar = view.findViewById(R.id.toolbar_history_detail);
        
        setupToolbar();
        setupDeleteButton();
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (historyId > 0) {
            loadHistoryDetail(historyId);
        } else {
            showError("Invalid history ID");
            navigateBack();
        }
    }
    
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> navigateBack());
    }
    
    private void setupDeleteButton() {
        deleteButton.setOnClickListener(v -> confirmDelete());
    }
    
    private void loadHistoryDetail(int historyId) {
        progressBar.setVisibility(View.VISIBLE);
        
        // Here we would normally call the API to get history detail
        // For now, we'll use the selected history from ViewModel
        historyDetail = viewModel.getSelectedHistory().getValue();
        
        if (historyDetail != null) {
            displayHistoryDetail(historyDetail);
            progressBar.setVisibility(View.GONE);
        } else {
            // If we don't have the history detail in ViewModel, 
            // we would need to fetch it from the API
            showError("History detail not found");
            progressBar.setVisibility(View.GONE);
            navigateBack();
        }
    }
    
    private void displayHistoryDetail(HistoryResponse.History history) {
        // Display person information
        if (history.getPeople() != null) {
            personNameTextView.setText(history.getPeople().getName());
            personAgeTextView.setText(String.valueOf(history.getPeople().getBirthday()));
            personIdTextView.setText(String.valueOf(history.getPeople().getPeopleId()));
            
            // Load person image if available
            if (history.getPeople().getFaceImagePath() != null && !history.getPeople().getFaceImagePath().isEmpty()) {
                Glide.with(requireContext())
                        .load(history.getPeople().getFaceImagePath())
                        .placeholder(R.drawable.ic_history)
                        .error(android.R.drawable.ic_menu_camera)
                        .centerCrop()
                        .into(personPhotoImageView);
            }
        } else {
            personNameTextView.setText("Unknown");
            personAgeTextView.setText("N/A");
            personIdTextView.setText("N/A");
        }
        
        // Display event information
        historyIdTextView.setText(String.valueOf(history.getHistoryId()));
        
        // Format timestamp correctly
        timestampTextView.setText(formatTimestamp(history.getTimestamp()));
        
        modeTextView.setText(history.getMode());
        
        // Load event image if available
        if (history.getImagePath() != null && !history.getImagePath().isEmpty()) {
            Glide.with(requireContext())
                    .load(history.getImagePath())
                    .placeholder(R.drawable.ic_history)
                    .error(android.R.drawable.ic_menu_camera)
                    .centerInside()
                    .into(eventPhotoImageView);
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
            android.util.Log.e("HistoryDetailFragment", "Error parsing timestamp: " + e.getMessage());
            // If parsing fails, return the original timestamp
        }
        
        return timestamp;
    }
    
    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa lịch sử")
                .setMessage("Bạn có chắc chắn muốn xóa bản ghi lịch sử này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteHistory())
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteHistory() {
        if (historyDetail == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        
        // Lấy token xác thực
        String token = getAuthToken();
        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Không tìm thấy token. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        viewModel.deleteHistory(token, historyDetail.getHistoryId()).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null && resource.getData().isSuccess()) {
                    // Xóa thành công
                    Toast.makeText(requireContext(), "Xóa lịch sử thành công", Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    // Phản hồi từ server không thành công
                    String errorMessage = (resource.getData() != null) ? resource.getData().getMessage() : "Không xác định";
                    showError("Xóa lịch sử thất bại: " + errorMessage);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                // Xảy ra lỗi khi gọi API
                showError("Xóa lịch sử thất bại: " + resource.getMessage());
            }
            // Không xử lý trạng thái LOADING
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