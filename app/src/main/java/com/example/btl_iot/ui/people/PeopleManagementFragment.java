package com.example.btl_iot.ui.people;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.Person;
import com.example.btl_iot.data.repository.PeopleRepository;
import com.example.btl_iot.viewmodel.PeopleViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PeopleManagementFragment extends Fragment implements PeopleAdapter.PersonClickListener {
    private static final String TAG = "PeopleManagementFrag";
    
    private PeopleViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private PeopleAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAddPerson;
    private TextInputEditText searchInput;
    private View rootView;
    
    private List<Person> allPeopleList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use ApplicationContext to create the ViewModel
        viewModel = new ViewModelProvider(requireActivity(), 
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(PeopleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_people_management, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recycler_people);
        emptyView = view.findViewById(R.id.txt_empty_view);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        fabAddPerson = view.findViewById(R.id.fab_add_person);
        searchInput = view.findViewById(R.id.input_search);
        
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
        setupSearch();
        setupTouchListener();
        observeViewModel();
    }

    private void setupTouchListener() {
        // Set up touch listener for clearing focus when clicking outside search input
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (searchInput.isFocused()) {
                    // Get the search input location
                    int[] searchLocation = new int[2];
                    searchInput.getLocationOnScreen(searchLocation);
                    int searchX = searchLocation[0];
                    int searchY = searchLocation[1];
                    int searchWidth = searchInput.getWidth();
                    int searchHeight = searchInput.getHeight();
                    
                    // Check if the touch is outside the search input
                    if (event.getRawX() < searchX || event.getRawX() > searchX + searchWidth ||
                        event.getRawY() < searchY || event.getRawY() > searchY + searchHeight) {
                        clearSearchFocus();
                        return true;
                    }
                }
            }
            return false;
        });
        
        // Also setup clear focus for recyclerView
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (searchInput.isFocused()) {
                    clearSearchFocus();
                }
            }
            return false;
        });
    }
    
    private void clearSearchFocus() {
        searchInput.clearFocus();
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView");
        adapter = new PeopleAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            searchInput.setText("");
            viewModel.refreshPeopleList();
        });
    }
    
    private void setupFab() {
        fabAddPerson.setOnClickListener(v -> {
            // Reset người dùng đang chọn
            viewModel.setSelectedPersonId(-1);
            viewModel.setSelectedPerson(null);
            
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_navigation_people_to_addEditPerson);
        });
    }
    
    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterPeople(s.toString());
            }
        });
        
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterPeople(searchInput.getText().toString());
                return true;
            }
            return false;
        });
    }
    
    private void filterPeople(String query) {
        if (allPeopleList.isEmpty()) {
            return;
        }
        
        query = query.toLowerCase().trim();
        
        if (query.isEmpty()) {
            adapter.submitList(allPeopleList);
            updateEmptyView(allPeopleList);
            return;
        }
        
        List<Person> filteredList = new ArrayList<>();
        
        for (Person person : allPeopleList) {
            if ((person.getName() != null && person.getName().toLowerCase().contains(query)) ||
                (person.getIdentificationId() != null && person.getIdentificationId().toLowerCase().contains(query))) {
                filteredList.add(person);
            }
        }
        
        adapter.submitList(filteredList);
        updateEmptyView(filteredList);
    }
    
    private void updateEmptyView(List<Person> list) {
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            if (!searchInput.getText().toString().trim().isEmpty()) {
                emptyView.setText("Không tìm thấy người dùng phù hợp");
            } else {
                emptyView.setText(R.string.no_people_found);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void observeViewModel() {
        viewModel.getPeopleList().observe(getViewLifecycleOwner(), resource -> {
            swipeRefreshLayout.setRefreshing(false);
            
            if (resource.getStatus() == PeopleRepository.Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            } else if (resource.getStatus() == PeopleRepository.Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                
                List<Person> people = resource.getData();
                if (people != null && !people.isEmpty()) {
                    allPeopleList = new ArrayList<>(people);
                    
                    String currentQuery = searchInput.getText().toString().trim();
                    if (currentQuery.isEmpty()) {
                        adapter.submitList(people);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        filterPeople(currentQuery);
                    }
                    
                    Log.d(TAG, "People list loaded successfully: " + people.size() + " items");
                } else {
                    allPeopleList.clear();
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(R.string.no_people_found);
                    Log.d(TAG, "People list is empty");
                }
            } else if (resource.getStatus() == PeopleRepository.Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.no_people_found) + "\n" + resource.getMessage());
                
                // Show error toast
                Toast.makeText(requireContext(), "Error: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error loading people: " + resource.getMessage());
            }
        });
        
        // Observe delete success
        viewModel.getDeletePersonSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                viewModel.setDeletePersonSuccess(false);
                // Refresh the list after successful deletion
                viewModel.refreshPeopleList();
            }
        });
    }

    @Override
    public void onPersonClick(Person person) {
        // Lưu thông tin người dùng đã chọn vào ViewModel
        Log.d(TAG, "Person clicked: " + person.getName() + ", ID: " + person.getPeopleId());
        viewModel.setSelectedPersonId(person.getPeopleId());
        viewModel.setSelectedPerson(person);
        
        // Điều hướng đến màn hình chỉnh sửa
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_navigation_people_to_addEditPerson);
    }
    
    @Override
    public void onDeleteClick(Person person) {
        confirmDeletePerson(person);
    }
    
    private void confirmDeletePerson(Person person) {
        if (person == null) return;
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa " + person.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deletePerson(person))
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void deletePerson(Person person) {
        if (person == null) return;
        progressBar.setVisibility(View.VISIBLE);
        
        viewModel.deletePerson(person.getPeopleId())
            .observe(getViewLifecycleOwner(), resource -> {
                progressBar.setVisibility(View.GONE);
                if (resource.getStatus() == PeopleRepository.Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                    viewModel.setDeletePersonSuccess(true);
                } else if (resource.getMessage() != null) {
                    Toast.makeText(requireContext(), "Lỗi xóa: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }
} 