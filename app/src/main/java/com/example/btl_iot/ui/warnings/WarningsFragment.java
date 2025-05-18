package com.example.btl_iot.ui.warnings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.WarningResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.util.Constants;
import com.example.btl_iot.viewmodel.WarningViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class WarningsFragment extends Fragment implements WarningsAdapter.WarningItemListener {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private WarningsAdapter warningsAdapter;
    private WarningViewModel warningViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private TextView totalWarningsTextView;
    private TextView todayWarningsTextView;
    private TextInputEditText searchInput;
    private Button dateFilterButton;
    private Button clearFilterButton;
    private Toolbar toolbar;

    private List<WarningResponse.Warning> allWarningList = new ArrayList<>();
    private List<WarningResponse.Warning> filteredWarningList = new ArrayList<>();
    private String currentSearchQuery = "";
    private String startDateFilter = "2023-01-01";
    private String endDateFilter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);

        // Initialize UI components
        progressBar = view.findViewById(R.id.progress_bar_warning);
        recyclerView = view.findViewById(R.id.recycler_view_warning);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_warning);
        emptyView = view.findViewById(R.id.txt_empty_view_warning);
        totalWarningsTextView = view.findViewById(R.id.txt_total_warnings);
        todayWarningsTextView = view.findViewById(R.id.txt_today_warnings);
        searchInput = view.findViewById(R.id.input_search_warning);
        dateFilterButton = view.findViewById(R.id.btn_date_filter_warning);
        clearFilterButton = view.findViewById(R.id.btn_clear_filter_warning);
        toolbar = view.findViewById(R.id.toolbar_warning);

        // Set initial visibility
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        warningsAdapter = new WarningsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(warningsAdapter);

        // Setup ViewModel
        warningViewModel = new ViewModelProvider(requireActivity()).get(WarningViewModel.class);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshWarningData);

        // Setup search input
        setupSearchInput();

        // Setup filter buttons
        setupFilterButtons();

        // Setup toolbar
        setupToolbar();

        // Load warning data
        loadWarningData();

        return view;
    }

    private void setupToolbar() {
        toolbar.setTitle("Quản lý cảnh báo");
//        toolbar.inflateMenu(R.menu.warning_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_refresh) {
                refreshWarningData();
                return true;
            }
            return false;
        });
    }

    private void setupSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                filterWarningList();
            }
        });
    }

    private void setupFilterButtons() {
        dateFilterButton.setOnClickListener(v -> showDateFilterDialog());
        clearFilterButton.setOnClickListener(v -> clearFilters());
    }

    private void loadWarningData() {
        String token = getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy token. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        int page = 0;
        int limit = 20;

        updateDateFilterButtonText();

        fetchWarningData(token, page, limit, startDateFilter, endDateFilter);
    }


    private void updateDateFilterButtonText() {
        try {
            // Check if using default dates
            boolean isDefault = startDateFilter.equals("2023-01-01") &&
                    endDateFilter.equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

            if (isDefault) {
                dateFilterButton.setText("Lọc ngày");
            } else {
                // Format dates for display
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

                Date startDate = apiFormat.parse(startDateFilter);
                Date endDate = apiFormat.parse(endDateFilter);

                if (startDate != null && endDate != null) {
                    String buttonText = displayFormat.format(startDate) + " - " + displayFormat.format(endDate);
                    dateFilterButton.setText(buttonText);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("HistoryFragment", "Error updating date filter button: " + e.getMessage());
            dateFilterButton.setText("Lọc ngày");
        }
    }

    private void refreshWarningData() {
        allWarningList.clear();
        filteredWarningList.clear();
        warningsAdapter.updateData(filteredWarningList);
        loadWarningData();
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

    private void fetchWarningData(String token, int page, int limit, String start, String end) {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        warningViewModel.getWarnings(token, page, limit, start, end).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            if (resource == null) {
                Toast.makeText(getContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (resource.getStatus()) {
                case SUCCESS:
                    WarningResponse warningResponse = resource.getData();
                    if (warningResponse != null && warningResponse.isSuccess() && warningResponse.getData() != null) {
                        List<WarningResponse.Warning> warnings = warningResponse.getData().getContent();
                        if (warnings != null && !warnings.isEmpty()) {
                            allWarningList = warnings;
                            warningsAdapter.updateData(warnings);
                            updateStatistics();
                        } else {
                            showEmptyView("Không tìm thấy cảnh báo");
                        }
                    } else {
                        Toast.makeText(getContext(), "Không thể tải cảnh báo", Toast.LENGTH_SHORT).show();
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

    private void filterWarningList() {
        if (allWarningList == null || allWarningList.isEmpty()) {
            filteredWarningList = new ArrayList<>();
            warningsAdapter.updateData(filteredWarningList);
            showEmptyView("Không tìm thấy cảnh báo");
            return;
        }

        filteredWarningList = allWarningList.stream()
                .filter(warning -> warning.getInfo().toLowerCase().contains(currentSearchQuery))
                .collect(Collectors.toList());

        warningsAdapter.updateData(filteredWarningList);

        if (filteredWarningList.isEmpty()) {
            showEmptyView("Không tìm thấy cảnh báo");
        } else {
            hideEmptyView();
        }
    }

    private void showDateFilterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_date_filter, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        TextView txtStartDate = dialogView.findViewById(R.id.txt_start_date);
        TextView txtEndDate = dialogView.findViewById(R.id.txt_end_date);

        try {
            txtStartDate.setText(displayFormat.format(apiFormat.parse(startDateFilter)));
            txtEndDate.setText(displayFormat.format(apiFormat.parse(endDateFilter)));
        } catch (Exception e) {
            txtStartDate.setText("");
            txtEndDate.setText("");
        }

        txtStartDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(apiFormat.parse(startDateFilter));
            } catch (Exception ignored) {
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        txtStartDate.setText(displayFormat.format(selectedDate.getTime()));
                        startDateFilter = apiFormat.format(selectedDate.getTime());
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        txtEndDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(apiFormat.parse(endDateFilter));
            } catch (Exception ignored) {
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        txtEndDate.setText(displayFormat.format(selectedDate.getTime()));
                        endDateFilter = apiFormat.format(selectedDate.getTime());
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnApply = dialogView.findViewById(R.id.btn_apply);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnApply.setOnClickListener(v -> {
            updateDateFilterButtonText();
            refreshWarningData();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void clearFilters() {
        searchInput.setText("");
        currentSearchQuery = "";

        startDateFilter = "2023-01-01";
        endDateFilter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        updateDateFilterButtonText();

        refreshWarningData();
    }

    private void updateStatistics() {
        if (allWarningList == null || allWarningList.isEmpty()) {
            totalWarningsTextView.setText("0");
            todayWarningsTextView.setText("0");
            return;
        }

        totalWarningsTextView.setText(String.valueOf(allWarningList.size()));

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        long todayCount = allWarningList.stream()
                .filter(warning -> warning.getTimestamp() != null && warning.getTimestamp().startsWith(today))
                .count();
        todayWarningsTextView.setText(String.valueOf(todayCount));
    }

    private void showEmptyView(String message) {
        if (emptyView != null) {
            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    private void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWarningItemClicked(WarningResponse.Warning warning) {

    }
}