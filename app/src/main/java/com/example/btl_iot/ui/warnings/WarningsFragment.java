package com.example.btl_iot.ui.warnings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.WarningResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.util.Constants;
import com.example.btl_iot.viewmodel.WarningViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WarningsFragment extends Fragment implements WarningsAdapter.WarningItemListener {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private WarningsAdapter warningsAdapter;
    private WarningViewModel warningViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private TextView totalWarningsTextView;
    private TextView todayWarningsTextView;
    private Button dateFilterButton;
    private Button clearFilterButton;
    private Toolbar toolbar;

    private List<WarningResponse.Warning> allWarningList = new ArrayList<>();
    private List<WarningResponse.Warning> filteredWarningList = new ArrayList<>();
    private String startDateFilter = "2023-01-01";
    private String endDateFilter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);

        android.util.Log.d("WarningsFragment", "onCreateView called");

        // Initialize UI components
        progressBar = view.findViewById(R.id.progress_bar_warning);
        recyclerView = view.findViewById(R.id.recycler_view_warning);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_warning);
        emptyView = view.findViewById(R.id.txt_empty_view_warning);
        totalWarningsTextView = view.findViewById(R.id.txt_total_warnings);
        todayWarningsTextView = view.findViewById(R.id.txt_today_warnings);
        dateFilterButton = view.findViewById(R.id.btn_date_filter_warning);
        clearFilterButton = view.findViewById(R.id.btn_clear_filter_warning);
        toolbar = view.findViewById(R.id.toolbar_warning);

        // Set initial visibility
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        // Setup RecyclerView
        android.util.Log.d("WarningsFragment", "Setting up RecyclerView");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        warningsAdapter = new WarningsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(warningsAdapter);
        android.util.Log.d("WarningsFragment", "RecyclerView setup complete");

        // Setup ViewModel
        warningViewModel = new ViewModelProvider(requireActivity()).get(WarningViewModel.class);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshWarningData);

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
        toolbar.inflateMenu(R.menu.warning_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_refresh) {
                refreshWarningData();
                return true;
            } else if (itemId == R.id.menu_statistics) {
                showStatisticsDialog();
                return true;
            }
            return false;
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
            android.util.Log.e("WarningsFragment", "Error updating date filter button: " + e.getMessage());
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

        android.util.Log.d("WarningsFragment", "Fetching warnings with token: " + token);
        android.util.Log.d("WarningsFragment", "Parameters: page=" + page + ", limit=" + limit + 
                       ", start=" + start + ", end=" + end);

        warningViewModel.getWarnings(token, page, limit, start, end).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            if (resource == null) {
                android.util.Log.e("WarningsFragment", "Resource is null");
                showEmptyView("Không có dữ liệu");
                return;
            }

            android.util.Log.d("WarningsFragment", "Response status: " + resource.getStatus());

            switch (resource.getStatus()) {
                case SUCCESS:
                    WarningResponse warningResponse = resource.getData();
                    android.util.Log.d("WarningsFragment", "Warning response: " + (warningResponse != null ? "not null" : "null"));
                    
                    if (warningResponse != null) {
                        android.util.Log.d("WarningsFragment", "Warning success: " + warningResponse.isSuccess());
                        android.util.Log.d("WarningsFragment", "Warning data: " + (warningResponse.getData() != null ? "not null" : "null"));
                        
                        if (warningResponse.getData() != null) {
                            List<WarningResponse.Warning> content = warningResponse.getData().getContent();
                            android.util.Log.d("WarningsFragment", "Content: " + (content != null ? ("size=" + content.size()) : "null"));
                            
                            if (content != null && !content.isEmpty()) {
                                android.util.Log.d("WarningsFragment", "First item id: " + content.get(0).getWarningId());
                            }
                        }
                    }
                    
                    if (warningResponse != null && warningResponse.isSuccess() && warningResponse.getData() != null) {
                        List<WarningResponse.Warning> warnings = warningResponse.getData().getContent();
                        if (warnings != null && !warnings.isEmpty()) {
                            // Store all warning records
                            allWarningList = new ArrayList<>(warnings);
                            android.util.Log.d("WarningsFragment", "All warning list updated with " + allWarningList.size() + " items");
                            
                            // Apply current filters
                            filterWarningList();
                            
                            // Update statistics
                            updateStatistics();
                        } else {
                            android.util.Log.d("WarningsFragment", "No warnings in response");
                            showEmptyView("Không tìm thấy cảnh báo");
                        }
                    } else {
                        android.util.Log.d("WarningsFragment", "Invalid warning response structure");
                        showEmptyView("Không thể tải cảnh báo");
                    }
                    break;

                case ERROR:
                    android.util.Log.e("WarningsFragment", "Error: " + resource.getMessage());
                    showEmptyView(resource.getMessage());
                    break;

                case LOADING:
                    android.util.Log.d("WarningsFragment", "Loading...");
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void filterWarningList() {
        android.util.Log.d("WarningsFragment", "Filtering warning list, allWarningList size: " + 
            (allWarningList != null ? allWarningList.size() : "null"));
        
        if (allWarningList == null || allWarningList.isEmpty()) {
            filteredWarningList = new ArrayList<>();
            warningsAdapter.updateData(filteredWarningList);
            showEmptyView("Không tìm thấy cảnh báo");
            return;
        }
        
        // Use all warnings as filtered list (no text search)
        filteredWarningList = new ArrayList<>(allWarningList);
        
        android.util.Log.d("WarningsFragment", "Filtered list size after filtering: " + filteredWarningList.size());
        
        // Update adapter with filtered list
        warningsAdapter.updateData(filteredWarningList);
        android.util.Log.d("WarningsFragment", "Updated adapter with filtered data");
        
        // Show empty view if needed
        if (filteredWarningList.isEmpty()) {
            android.util.Log.d("WarningsFragment", "Filtered list is empty, showing empty view");
            showEmptyView("Không tìm thấy cảnh báo phù hợp");
        } else {
            android.util.Log.d("WarningsFragment", "Filtered list has data, hiding empty view");
            hideEmptyView();
        }
    }

    private void showDateFilterDialog() {
        // Inflate custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_date_filter, null);
        
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Set initial values
        TextView txtStartDate = dialogView.findViewById(R.id.txt_start_date);
        TextView txtEndDate = dialogView.findViewById(R.id.txt_end_date);
        
        // Convert stored dates to display format
        try {
            Date startDate = apiFormat.parse(startDateFilter);
            Date endDate = apiFormat.parse(endDateFilter);
            if (startDate != null && endDate != null) {
                txtStartDate.setText(displayFormat.format(startDate));
                txtEndDate.setText(displayFormat.format(endDate));
            }
        } catch (Exception e) {
            android.util.Log.e("WarningsFragment", "Date parsing error: " + e.getMessage());
        }
        
        // Set click listeners for date fields
        txtStartDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            try {
                Date date = displayFormat.parse(txtStartDate.getText().toString());
                if (date != null) {
                    cal.setTime(date);
                }
            } catch (Exception e) {
                android.util.Log.e("WarningsFragment", "Date parsing error: " + e.getMessage());
            }
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        txtStartDate.setText(displayFormat.format(selectedDate.getTime()));
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
                Date date = displayFormat.parse(txtEndDate.getText().toString());
                if (date != null) {
                    cal.setTime(date);
                }
            } catch (Exception e) {
                android.util.Log.e("WarningsFragment", "Date parsing error: " + e.getMessage());
            }
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        txtEndDate.setText(displayFormat.format(selectedDate.getTime()));
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        // Set click listeners for action buttons
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnApply = dialogView.findViewById(R.id.btn_apply);
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnApply.setOnClickListener(v -> {
            try {
                // Parse selected dates
                Date startDate = displayFormat.parse(txtStartDate.getText().toString());
                Date endDate = displayFormat.parse(txtEndDate.getText().toString());
                
                if (startDate != null && endDate != null) {
                    // Convert to API format
                    startDateFilter = apiFormat.format(startDate);
                    endDateFilter = apiFormat.format(endDate);
                    
                    android.util.Log.d("WarningsFragment", "Applying date filter: " + startDateFilter + " to " + endDateFilter);
                    
                    // Update filter button text
                    updateDateFilterButtonText();
                    
                    // Apply filter
                    refreshWarningData();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                android.util.Log.e("WarningsFragment", "Date parsing error: " + e.getMessage());
            }
            
            dialog.dismiss();
        });
        
        // Show dialog
        dialog.show();
    }

    private void clearFilters() {
        // Reset date filters to default
        startDateFilter = "2023-01-01";
        endDateFilter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // Update date filter button text
        updateDateFilterButtonText();
        
        // Refresh data
        refreshWarningData();
    }

    private void updateStatistics() {
        if (allWarningList == null || allWarningList.isEmpty()) {
            totalWarningsTextView.setText("0");
            todayWarningsTextView.setText("0");
            return;
        }
        
        // Total warnings
        totalWarningsTextView.setText(String.valueOf(allWarningList.size()));
        
        // Today's warnings
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        long todayCount = allWarningList.stream()
                .filter(warning -> warning.getTimestamp() != null && warning.getTimestamp().startsWith(today))
                .count();
        todayWarningsTextView.setText(String.valueOf(todayCount));
    }

    private void showEmptyView(String message) {
        android.util.Log.d("WarningsFragment", "Showing empty view with message: " + message);
        if (emptyView != null) {
            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }
    
    private void hideEmptyView() {
        android.util.Log.d("WarningsFragment", "Hiding empty view");
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    // Implement WarningItemListener methods
    @Override
    public void onWarningItemClick(WarningResponse.Warning warning) {
        viewWarningDetail(warning);
    }

    @Override
    public void onViewDetailsClick(WarningResponse.Warning warning) {
        viewWarningDetail(warning);
    }

    @Override
    public void onDeleteClick(WarningResponse.Warning warning) {
        confirmDeleteWarning(warning);
    }
    
    @Override
    public void onOptionsClick(WarningResponse.Warning warning, View view) {
        showOptionsMenu(warning, view);
    }
    
    private void showOptionsMenu(WarningResponse.Warning warning, View view) {
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.inflate(R.menu.warning_item_menu);
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_view_details) {
                viewWarningDetail(warning);
                return true;
            } else if (itemId == R.id.menu_delete) {
                confirmDeleteWarning(warning);
                return true;
            }
            return false;
        });
        popup.show();
    }
    
    private void viewWarningDetail(WarningResponse.Warning warning) {
        // Lưu warning được chọn vào ViewModel để xem chi tiết
        warningViewModel.setSelectedWarning(warning);
        
        // Chuyển hướng đến trang chi tiết cảnh báo
        try {
            Bundle args = new Bundle();
            args.putInt("warningId", warning.getWarningId());
            
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_navigation_warnings_to_warningDetail, args);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi chuyển hướng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void confirmDeleteWarning(WarningResponse.Warning warning) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa cảnh báo")
                .setMessage("Bạn có chắc chắn muốn xóa cảnh báo này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteWarning(warning))
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteWarning(WarningResponse.Warning warning) {
        progressBar.setVisibility(View.VISIBLE);
        
        // Lấy token xác thực
        String token = getAuthToken();
        if (token == null || token.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Không tìm thấy token. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Gọi API để xóa warning
        warningViewModel.deleteWarning(token, warning.getWarningId()).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null && resource.getData().isSuccess()) {
                    // Xóa thành công
                    Toast.makeText(requireContext(), "Xóa cảnh báo thành công", Toast.LENGTH_SHORT).show();
                    
                    // Remove from lists and update adapter
                    allWarningList.remove(warning);
                    filteredWarningList.remove(warning);
                    warningsAdapter.updateData(filteredWarningList);
                    
                    // Update statistics
                    updateStatistics();
                    
                    // Show empty view if needed
                    if (filteredWarningList.isEmpty()) {
                        showEmptyView("Không tìm thấy cảnh báo");
                    }
                } else {
                    // Phản hồi từ server không thành công
                    String errorMessage = (resource.getData() != null) ? resource.getData().getMessage() : "Không xác định";
                    Toast.makeText(requireContext(), "Xóa cảnh báo thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                // Xảy ra lỗi khi gọi API
                Toast.makeText(requireContext(), "Xóa cảnh báo thất bại: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStatisticsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_warning_statistics, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Get references to statistics TextViews
        TextView totalWarningsTextView = dialogView.findViewById(R.id.stat_total_warnings);
        TextView todayWarningsTextView = dialogView.findViewById(R.id.stat_today_warnings);
        TextView weekWarningsTextView = dialogView.findViewById(R.id.stat_week_warnings);
        TextView monthWarningsTextView = dialogView.findViewById(R.id.stat_month_warnings);

        // Calculate statistics
        int totalWarnings = allWarningList.size();
        
        // Get today's date
        Calendar calendar = Calendar.getInstance();
        String todayString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        
        // Calculate today's warnings
        int todayWarnings = 0;
        for (WarningResponse.Warning warning : allWarningList) {
            if (warning.getTimestamp().startsWith(todayString)) {
                todayWarnings++;
            }
        }
        
        // Calculate this week's warnings
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        String weekStartString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        int weekWarnings = 0;
        for (WarningResponse.Warning warning : allWarningList) {
            if (warning.getTimestamp().compareTo(weekStartString) >= 0) {
                weekWarnings++;
            }
        }
        
        // Calculate this month's warnings
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String monthStartString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        int monthWarnings = 0;
        for (WarningResponse.Warning warning : allWarningList) {
            if (warning.getTimestamp().compareTo(monthStartString) >= 0) {
                monthWarnings++;
            }
        }
        
        // Update UI
        totalWarningsTextView.setText(String.valueOf(totalWarnings));
        todayWarningsTextView.setText(String.valueOf(todayWarnings));
        weekWarningsTextView.setText(String.valueOf(weekWarnings));
        monthWarningsTextView.setText(String.valueOf(monthWarnings));
        
        dialog.show();
    }
}