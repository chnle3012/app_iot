package com.example.btl_iot.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.viewmodel.HistoryViewModel;

import java.util.List;

public class HistoryFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private HistoryViewModel historyViewModel;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view_history);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(null); // Adapter ban đầu không có dữ liệu
        recyclerView.setAdapter(historyAdapter);

        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoaWV1dDUiLCJpYXQiOjE3NDUzMTEzOTUsImV4cCI6MTc0NTM5Nzc5NX0.gQpEVKihKJjUY3BvltEcHzFiQvz2nhPa9F5PmqafqhY"; // Thay bằng token thực tế
        int page = 0;
        int limit = 20;
        String start = "2023-01-01";
        String end = "2025-12-31";

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
                            historyAdapter.updateData(histories); // Cập nhật dữ liệu cho adapter
                        } else {
                            Toast.makeText(getContext(), "No history found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case ERROR:
                    Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    break;

                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }
}