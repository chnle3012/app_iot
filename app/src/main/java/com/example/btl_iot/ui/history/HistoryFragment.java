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

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(null, this);
        recyclerView.setAdapter(historyAdapter);

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
        toolbar.setTitle("History Management");
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
            Toast.makeText(getContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        int page = 0;
        int limit = 20; // Get more records to enable better filtering

        fetchHistoryData(token, page, limit, startDateFilter, endDateFilter);
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

        historyViewModel.getHistory(token, page, limit, start, end).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            if (resource == null) {
                showEmptyView("No data available");
                return;
            }

            switch (resource.getStatus()) {
                case SUCCESS:
                    HistoryResponse historyResponse = resource.getData();
                    if (historyResponse != null && historyResponse.isSuccess() && historyResponse.getData() != null) {
                        List<HistoryResponse.History> histories = historyResponse.getData().getContent();
                        if (histories != null && !histories.isEmpty()) {
                            // Store all history records
                            allHistoryList = new ArrayList<>(histories);
                            
                            // Apply current filters
                            filterHistoryList();
                            
                            // Update statistics
                            updateStatistics();
                        } else {
                            showEmptyView("No history found");
                        }
                    } else {
                        showEmptyView("Failed to load history");
                    }
                    break;

                case ERROR:
                    showEmptyView(resource.getMessage());
                    break;

                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }
    
    private void filterHistoryList() {
        if (allHistoryList == null || allHistoryList.isEmpty()) {
            filteredHistoryList = new ArrayList<>();
            historyAdapter.updateData(filteredHistoryList);
            showEmptyView("No history records found");
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
        
        // Update adapter with filtered list
        historyAdapter.updateData(filteredHistoryList);
        
        // Show empty view if needed
        if (filteredHistoryList.isEmpty()) {
            showEmptyView("No matching records found");
        } else {
            hideEmptyView();
        }
    }
    
    private void showDateFilterDialog() {
        // Show date range picker dialog
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        // First show start date picker
        DatePickerDialog startDatePicker = new DatePickerDialog(requireContext(), 
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format selected start date
                    Calendar startCal = Calendar.getInstance();
                    startCal.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    startDateFilter = sdf.format(startCal.getTime());
                    
                    // Then show end date picker
                    DatePickerDialog endDatePicker = new DatePickerDialog(requireContext(),
                            (endView, endYear, endMonth, endDay) -> {
                                // Format selected end date
                                Calendar endCal = Calendar.getInstance();
                                endCal.set(endYear, endMonth, endDay);
                                endDateFilter = sdf.format(endCal.getTime());
                                
                                // Apply date filter
                                refreshHistoryData();
                            }, year, month, day);
                    
                    endDatePicker.show();
                }, year, month, day);
        
        startDatePicker.show();
    }
    
    private void clearFilters() {
        // Clear search query
        searchInput.setText("");
        currentSearchQuery = "";
        
        // Reset date filters to default
        startDateFilter = "2023-01-01";
        endDateFilter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
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
        builder.setTitle("History Statistics");
        
        View view = getLayoutInflater().inflate(R.layout.dialog_history_statistics, null);
        // TODO: Add statistics data to the view
        
        builder.setView(view);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showEmptyView(String message) {
        if (emptyView != null) {
            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
        }
        recyclerView.setVisibility(View.GONE);
    }
    
    private void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        recyclerView.setVisibility(View.VISIBLE);
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
                .setTitle("Delete History")
                .setMessage("Are you sure you want to delete this history record?")
                .setPositiveButton("Delete", (dialog, which) -> deleteHistory(history))
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void deleteHistory(HistoryResponse.History history) {
        progressBar.setVisibility(View.VISIBLE);
        
        // Call API to delete history
        historyViewModel.deleteHistory(history.getHistoryId()).observe(getViewLifecycleOwner(), result -> {
            progressBar.setVisibility(View.GONE);
            
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), "History deleted successfully", Toast.LENGTH_SHORT).show();
                
                // Remove from lists and update adapter
                allHistoryList.remove(history);
                filteredHistoryList.remove(history);
                historyAdapter.updateData(filteredHistoryList);
                
                // Update statistics
                updateStatistics();
                
                // Show empty view if needed
                if (filteredHistoryList.isEmpty()) {
                    showEmptyView("No history records found");
                }
            } else {
                Toast.makeText(requireContext(), "Failed to delete history: " + result.getMessage(), Toast.LENGTH_SHORT).show();
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
