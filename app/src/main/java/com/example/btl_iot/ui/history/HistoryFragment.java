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

import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private TextView textViewName, textViewHistoryId, textViewMode, textViewTimestamp;
    private ImageView imageViewFace, imageViewHistory;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize views
        textViewName = view.findViewById(R.id.text_view_name);
        textViewHistoryId = view.findViewById(R.id.text_view_history_id);
        textViewMode = view.findViewById(R.id.text_view_mode);
        textViewTimestamp = view.findViewById(R.id.text_view_timestamp);
        imageViewFace = view.findViewById(R.id.image_view_face);
        imageViewHistory = view.findViewById(R.id.image_view_history);
        progressBar = view.findViewById(R.id.progress_bar);

        // Initialize ViewModel
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // Call API and observe data
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoaWV1dDUiLCJpYXQiOjE3NDUzMTEzOTUsImV4cCI6MTc0NTM5Nzc5NX0.gQpEVKihKJjUY3BvltEcHzFiQvz2nhPa9F5PmqafqhY"; // Replace with actual token
        int page = 0;
        int limit = 50;
        String start = "2024-01-01"; // Replace with your actual start date
        String end = "2025-12-31";   // Replace with your actual end date

        observeHistoryData(token, page, limit, start, end);

        return view;
    }

    private void observeHistoryData(String token, int page, int limit, String start, String end) {
        progressBar.setVisibility(View.VISIBLE);

        historyViewModel.getHistory(token, page, limit, start, end).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);

            if (resource == null) {
                Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (resource.getStatus()) {
                case SUCCESS:
                    HistoryResponse historyResponse = resource.getData();
                    if (historyResponse != null && historyResponse.isSuccess() && historyResponse.getData() != null) {
                        List<HistoryResponse.History> histories = historyResponse.getData().getContent();
                        if (histories != null && !histories.isEmpty()) {
                            // Print the number of history objects
                            int historyCount = histories.size();
                            Toast.makeText(getContext(), "Number of histories: " + historyCount, Toast.LENGTH_SHORT).show();

                            for (HistoryResponse.History history : histories) {
                                String name = history.getPeople() != null ? history.getPeople().getName() : "Unknown";
                                String historyId = "History id: " + history.getHistoryId();
                                String mode = history.getMode();
                                String timestamp = history.getTimestamp();

                                // Example: Update UI or log the data
                                textViewName.setText(name);
                                textViewHistoryId.setText(historyId);
                                textViewMode.setText(mode);
                                textViewTimestamp.setText(timestamp);
                            }
                        } else {
                            showNoHistory();
                        }
                    } else {
                        showNoHistory();
                    }
                    break;

                case ERROR:
                    Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    showNoHistory();
                    break;

                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void showNoHistory() {
        textViewName.setText("Chưa có lịch sử");
        textViewHistoryId.setText("");
        textViewMode.setText("");
        textViewTimestamp.setText("");
    }
}
