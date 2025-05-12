package com.example.btl_iot.ui.history;

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
import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.util.Constants;
import com.example.btl_iot.viewmodel.HistoryViewModel;
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

public class HistoryFragment extends Fragment implements HistoryAdapter.HistoryItemListener {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private HistoryViewModel historyViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private TextView totalRecordsTextView;
    private TextView todayRecordsTextView;
    private TextView uniquePeopleTextView;
    private TextInputEditText searchInput;
    private Button dateFilterButton;
    private Button clearFilterButton;
    private Toolbar toolbar;
    
    private List<HistoryResponse.History> allHistoryList = new ArrayList<>();
    private List<HistoryResponse.History> filteredHistoryList = new ArrayList<>();
    private String currentSearchQuery = "";
    private String startDateFilter = "2023-01-01";
    private String endDateFilter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        android.util.Log.d("HistoryFragment", "onCreateView called");

        // Initialize UI components
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view_history);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        emptyView = view.findViewById(R.id.txt_empty_view);
        totalRecordsTextView = view.findViewById(R.id.txt_total_records);
        todayRecordsTextView = view.findViewById(R.id.txt_today_records);
        uniquePeopleTextView = view.findViewById(R.id.txt_unique_people);
        searchInput = view.findViewById(R.id.input_search);
        dateFilterButton = view.findViewById(R.id.btn_date_filter);
        clearFilterButton = view.findViewById(R.id.btn_clear_filter);
        toolbar = view.findViewById(R.id.toolbar_history);

        // Set initial visibility
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        // Setup RecyclerView
        android.util.Log.d("HistoryFragment", "Setting up RecyclerView");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(historyAdapter);
        android.util.Log.d("HistoryFragment", "RecyclerView setup complete");

        // Setup ViewModel
        historyViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshHistoryData);
        
        // Setup search input
        setupSearchInput();
        
        // Setup filter buttons
        setupFilterButtons();
        
        // Setup toolbar
        setupToolbar();

        // Load history data
        loadHistoryData();

        return view;
    }
    
    private void setupToolbar() {
        toolbar.setTitle("Quản lý lịch sử");
        toolbar.inflateMenu(R.menu.history_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_refresh) {
                refreshHistoryData();
                return true;
            } else if (itemId == R.id.menu_statistics) {
                showStatisticsDialog();
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
                filterHistoryList();
            }
        });
    }
    
    private void setupFilterButtons() {
        dateFilterButton.setOnClickListener(v -> showDateFilterDialog());
        clearFilterButton.setOnClickListener(v -> clearFilters());
    }

    private void loadHistoryData() {
        String token = getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy token. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        int page = 0;
        int limit = 20; // Get more records to enable better filtering

        // Update date filter button text to show current filter
        updateDateFilterButtonText();

        fetchHistoryData(token, page, limit, startDateFilter, endDateFilter);
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
    
    private void refreshHistoryData() {
        allHistoryList.clear();
        filteredHistoryList.clear();
        historyAdapter.updateData(filteredHistoryList);
        loadHistoryData();
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

    private void fetchHistoryData(String token, int page, int limit, String start, String end) {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        android.util.Log.d("HistoryFragment", "Fetching history with token: " + token);
        android.util.Log.d("HistoryFragment", "Parameters: page=" + page + ", limit=" + limit + 
                       ", start=" + start + ", end=" + end);

        historyViewModel.getHistory(token, page, limit, start, end).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            if (resource == null) {
                android.util.Log.e("HistoryFragment", "Resource is null");
                showEmptyView("Không có dữ liệu");
                return;
            }

            android.util.Log.d("HistoryFragment", "Response status: " + resource.getStatus());


            switch (resource.getStatus()) {
                case SUCCESS:
                    HistoryResponse historyResponse = resource.getData();
                    android.util.Log.d("HistoryFragment", "History response: " + (historyResponse != null ? "not null" : "null"));
                    
                    if (historyResponse != null) {
                        android.util.Log.d("HistoryFragment", "History success: " + historyResponse.isSuccess());
                        android.util.Log.d("HistoryFragment", "History data: " + (historyResponse.getData() != null ? "not null" : "null"));
                        
                        if (historyResponse.getData() != null) {
                            List<HistoryResponse.History> content = historyResponse.getData().getContent();
                            android.util.Log.d("HistoryFragment", "Content: " + (content != null ? ("size=" + content.size()) : "null"));
                            
                            if (content != null && !content.isEmpty()) {
                                android.util.Log.d("HistoryFragment", "First item id: " + content.get(0).getHistoryId());
                            }
                        }
                    }
                    
                    if (historyResponse != null && historyResponse.isSuccess() && historyResponse.getData() != null) {
                        List<HistoryResponse.History> histories = historyResponse.getData().getContent();
                        if (histories != null && !histories.isEmpty()) {
                            // Store all history records
                            allHistoryList = new ArrayList<>(histories);
                            android.util.Log.d("HistoryFragment", "All history list updated with " + allHistoryList.size() + " items");
                            
                            // Apply current filters
                            filterHistoryList();
                            
                            // Update statistics
                            updateStatistics();
                        } else {
                            android.util.Log.d("HistoryFragment", "No histories in response");
                            showEmptyView("Không tìm thấy lịch sử");
                        }
                    } else {
                        android.util.Log.d("HistoryFragment", "Invalid history response structure");
                        showEmptyView("Không thể tải lịch sử");
                    }
                    break;

                case ERROR:
                    android.util.Log.e("HistoryFragment", "Error: " + resource.getMessage());
                    showEmptyView(resource.getMessage());
                    break;

                case LOADING:
                    android.util.Log.d("HistoryFragment", "Loading...");
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }
    
    private void filterHistoryList() {
        android.util.Log.d("HistoryFragment", "Filtering history list, allHistoryList size: " + 
            (allHistoryList != null ? allHistoryList.size() : "null"));
        
        if (allHistoryList == null || allHistoryList.isEmpty()) {
            filteredHistoryList = new ArrayList<>();
            historyAdapter.updateData(filteredHistoryList);
            showEmptyView("Không tìm thấy bản ghi lịch sử");
            return;
        }
        
        // Apply search filter
        filteredHistoryList = allHistoryList.stream()
                .filter(history -> {
                    if (currentSearchQuery.isEmpty()) {
                        return true;
                    }
                    
                    // Search by person name
                    if (history.getPeople() != null && 
                        history.getPeople().getName() != null && 
                        history.getPeople().getName().toLowerCase().contains(currentSearchQuery)) {
                        return true;
                    }
                    
                    // Search by history ID
                    return String.valueOf(history.getHistoryId()).contains(currentSearchQuery);
                })
                .collect(Collectors.toList());
        
        android.util.Log.d("HistoryFragment", "Filtered list size after filtering: " + filteredHistoryList.size());
        
        // Update adapter with filtered list
        historyAdapter.updateData(filteredHistoryList);
        android.util.Log.d("HistoryFragment", "Updated adapter with filtered data");
        
        // Show empty view if needed
        if (filteredHistoryList.isEmpty()) {
            android.util.Log.d("HistoryFragment", "Filtered list is empty, showing empty view");
            showEmptyView("Không tìm thấy bản ghi phù hợp");
        } else {
            android.util.Log.d("HistoryFragment", "Filtered list has data, hiding empty view");
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
            android.util.Log.e("HistoryFragment", "Date parsing error: " + e.getMessage());
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
                android.util.Log.e("HistoryFragment", "Date parsing error: " + e.getMessage());
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
                android.util.Log.e("HistoryFragment", "Date parsing error: " + e.getMessage());
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
                    
                    android.util.Log.d("HistoryFragment", "Applying date filter: " + startDateFilter + " to " + endDateFilter);
                    
                    // Update filter button text
                    updateDateFilterButtonText();
                    
                    // Apply filter
                    refreshHistoryData();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                android.util.Log.e("HistoryFragment", "Date parsing error: " + e.getMessage());
            }
            
            dialog.dismiss();
        });
        
        // Show dialog
        dialog.show();
    }
    
    private void clearFilters() {
        // Clear search query
        searchInput.setText("");
        currentSearchQuery = "";
        
        // Reset date filters to default
        startDateFilter = "2023-01-01";
        endDateFilter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // Update date filter button text
        updateDateFilterButtonText();
        
        // Refresh data
        refreshHistoryData();
    }
    
    private void updateStatistics() {
        if (allHistoryList == null || allHistoryList.isEmpty()) {
            totalRecordsTextView.setText("0");
            todayRecordsTextView.setText("0");
            uniquePeopleTextView.setText("0");
            return;
        }
        
        // Total records
        totalRecordsTextView.setText(String.valueOf(allHistoryList.size()));
        
        // Today's records
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        long todayCount = allHistoryList.stream()
                .filter(history -> history.getTimestamp() != null && history.getTimestamp().startsWith(today))
                .count();
        todayRecordsTextView.setText(String.valueOf(todayCount));
        
        // Unique people
        Set<Integer> uniquePeopleIds = new HashSet<>();
        for (HistoryResponse.History history : allHistoryList) {
            if (history.getPeople() != null) {
                uniquePeopleIds.add(history.getPeople().getPeopleId());
            }
        }
        uniquePeopleTextView.setText(String.valueOf(uniquePeopleIds.size()));
    }
    
    private void showStatisticsDialog() {
        // Create a dialog to show more detailed statistics
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thống kê lịch sử");
        
        View view = getLayoutInflater().inflate(R.layout.dialog_history_statistics, null);
        // TODO: Add statistics data to the view
        
        builder.setView(view);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showEmptyView(String message) {
        android.util.Log.d("HistoryFragment", "Showing empty view with message: " + message);
        if (emptyView != null) {
            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }
    
    private void hideEmptyView() {
        android.util.Log.d("HistoryFragment", "Hiding empty view");
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    // HistoryItemListener implementation
    @Override
    public void onHistoryItemClick(HistoryResponse.History history) {
        viewHistoryDetail(history);
    }

    @Override
    public void onViewDetailsClick(HistoryResponse.History history) {
        viewHistoryDetail(history);
    }

    @Override
    public void onDeleteClick(HistoryResponse.History history) {
        confirmDeleteHistory(history);
    }

    @Override
    public void onOptionsClick(HistoryResponse.History history, View view) {
        showOptionsMenu(history, view);
    }
    
    private void viewHistoryDetail(HistoryResponse.History history) {
        // Store selected history in ViewModel for detail view
        historyViewModel.setSelectedHistory(history);
        
        // Navigate to history detail fragment
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putInt("historyId", history.getHistoryId());
        navController.navigate(R.id.action_navigation_history_to_historyDetail, args);
    }
    
    private void confirmDeleteHistory(HistoryResponse.History history) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa lịch sử")
                .setMessage("Bạn có chắc chắn muốn xóa bản ghi lịch sử này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteHistory(history))
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deleteHistory(HistoryResponse.History history) {
        progressBar.setVisibility(View.VISIBLE);
        
        // Call API to delete history
        historyViewModel.deleteHistory(history.getHistoryId()).observe(getViewLifecycleOwner(), result -> {
            progressBar.setVisibility(View.GONE);
            
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "Xóa lịch sử thành công", Toast.LENGTH_SHORT).show();
                
                // Remove from lists and update adapter
                allHistoryList.remove(history);
                filteredHistoryList.remove(history);
                historyAdapter.updateData(filteredHistoryList);
                
                // Update statistics
                updateStatistics();
                
                // Show empty view if needed
                if (filteredHistoryList.isEmpty()) {
                    showEmptyView("Không tìm thấy bản ghi lịch sử");
                }
            } else {
                Toast.makeText(requireContext(), "Xóa lịch sử thất bại: " + result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showOptionsMenu(HistoryResponse.History history, View view) {
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.inflate(R.menu.history_item_menu);
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_view_details) {
                viewHistoryDetail(history);
                return true;
            } else if (itemId == R.id.menu_delete) {
                confirmDeleteHistory(history);
                return true;
            }
            return false;
        });
        popup.show();
    }
}
