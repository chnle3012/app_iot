package com.example.btl_iot.ui.people;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.Person;
import com.example.btl_iot.data.repository.PeopleRepository;
import com.example.btl_iot.viewmodel.PeopleViewModel;

import java.util.List;

public class PeopleManagementFragment extends Fragment implements PeopleAdapter.PersonClickListener {
    private static final String TAG = "PeopleManagementFrag";
    
    private PeopleViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private PeopleAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use ApplicationContext to create the ViewModel
        viewModel = new ViewModelProvider(this, 
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(PeopleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerView = view.findViewById(R.id.recycler_people);
        emptyView = view.findViewById(R.id.txt_empty_view);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        
        setupRecyclerView();
        setupSwipeRefresh();
        observeViewModel();
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView");
        adapter = new PeopleAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshPeopleList();
        });
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
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    adapter.submitList(people);
                    
                    Log.d(TAG, "People list loaded successfully: " + people.size() + " items");
                } else {
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
    }

    @Override
    public void onPersonClick(Person person) {
        // Handle person click if needed
        Toast.makeText(requireContext(), "Clicked: " + person.getName(), Toast.LENGTH_SHORT).show();
    }
} 