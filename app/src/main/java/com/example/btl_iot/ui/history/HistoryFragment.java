package com.example.btl_iot.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.viewmodel.HistoryViewModel;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private TextView textViewName, textViewAge, textViewMode, textViewTimestamp;
    private ImageView imageViewFace, imageViewHistory;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize views
        textViewName = view.findViewById(R.id.text_view_name);
        textViewAge = view.findViewById(R.id.text_view_age);
        textViewMode = view.findViewById(R.id.text_view_mode);
        textViewTimestamp = view.findViewById(R.id.text_view_timestamp);
        imageViewFace = view.findViewById(R.id.image_view_face);
        imageViewHistory = view.findViewById(R.id.image_view_history);
        progressBar = view.findViewById(R.id.progress_bar);

        // Initialize ViewModel
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // Call API and observe data
        long historyId = 1; // Replace with actual ID
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoaWV1dDMiLCJpYXQiOjE3NDUyMTIzNTQsImV4cCI6MTc0NTI5ODc1NH0.0m-1DCeSFxWHtRBgiKKNGCARQe5fy7nOiCi2ePkSqN0"; // Replace with actual token
        observeHistoryData(historyId, token);

        return view;
    }

    private void observeHistoryData(long historyId, String token) {
        progressBar.setVisibility(View.VISIBLE);

        historyViewModel.getHistory(historyId, token).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);

            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                HistoryResponse historyResponse = resource.getData();
                if (historyResponse != null && historyResponse.isSuccess() && historyResponse.getData() != null) {
                    // Update UI with data
                    textViewName.setText(historyResponse.getData().getPeople().getName());
                    textViewAge.setText(String.valueOf(historyResponse.getData().getPeople().getAge()));
                    textViewMode.setText(historyResponse.getData().getMode());
                    textViewTimestamp.setText(historyResponse.getData().getTimestamp());
                } else {
                    // Display "no history" message
                    textViewName.setText("chưa có lịch sử");
                    textViewAge.setText("");
                    textViewMode.setText("");
                    textViewTimestamp.setText("");
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                // Display error
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}