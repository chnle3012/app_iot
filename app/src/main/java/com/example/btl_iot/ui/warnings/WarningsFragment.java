package com.example.btl_iot.ui.warnings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.btl_iot.data.model.WarningResponse;
import com.example.btl_iot.util.Constants;
import com.example.btl_iot.viewmodel.WarningViewModel;

import java.util.List;

public class WarningsFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private WarningsAdapter warningAdapter;
    private WarningViewModel warningViewModel;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);

        // Ensure the IDs match those in fragment_warning.xml
        progressBar = view.findViewById(R.id.progress_bar_warning); // Correct ID
        recyclerView = view.findViewById(R.id.recycler_view_warning);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        warningAdapter = new WarningsAdapter(null); // Adapter initially has no data
        recyclerView.setAdapter(warningAdapter);

        warningViewModel = new ViewModelProvider(this).get(WarningViewModel.class);

        String token = getAuthToken(); // Replace with actual token
        int page = 0;
        int limit = 20;
        String start = "2023-01-01";
        String end = "2025-12-31";

        observeWarningData(token, page, limit, start, end);

        return view;
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

    private void observeWarningData(String token, int page, int limit, String start, String end) {
        progressBar.setVisibility(View.VISIBLE);

        warningViewModel.getWarnings(token, page, limit, start, end).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);

            if (resource == null) {
                Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (resource.getStatus()) {
                case SUCCESS:
                    WarningResponse warningResponse = resource.getData();
                    if (warningResponse != null && warningResponse.isSuccess() && warningResponse.getData() != null) {
                        List<WarningResponse.Warning> warnings = warningResponse.getData().getContent();
                        if (warnings != null && !warnings.isEmpty()) {
                            warningAdapter.updateData(warnings); // Cập nhật dữ liệu cho adapter
                        } else {
                            Toast.makeText(getContext(), "No warnings found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load warnings", Toast.LENGTH_SHORT).show();
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
