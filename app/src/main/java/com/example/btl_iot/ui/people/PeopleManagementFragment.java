package com.example.btl_iot.ui.people;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.example.btl_iot.R;
import com.example.btl_iot.data.model.User;
import com.example.btl_iot.viewmodel.PeopleViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PeopleManagementFragment extends Fragment implements PeopleAdapter.PersonClickListener {
    private static final String TAG = "PeopleManagementFrag";
    
    private PeopleViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private PeopleAdapter adapter;
    private FloatingActionButton fab;
    private Button btnAddSampleData;
    private Button btnClearData;

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
        fab = view.findViewById(R.id.fab_add_person);
        btnAddSampleData = view.findViewById(R.id.btnAddSampleData);
        btnClearData = view.findViewById(R.id.btnClearData);
        
        setupRecyclerView();
        setupFab();
        setupButtonListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView");
        adapter = new PeopleAdapter(this);
        // Ensure LayoutManager is set explicitly
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        fab.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_navigation_people_to_addEditPerson);
        });
    }

    private void setupButtonListeners() {
        btnAddSampleData.setOnClickListener(v -> {
            viewModel.addSampleData();
            Toast.makeText(requireContext(), getString(R.string.sample_data_added), Toast.LENGTH_SHORT).show();
        });
        
        btnClearData.setOnClickListener(v -> {
            // Get current list of people
            List<User> currentPeople = viewModel.getPeopleList().getValue();
            if (currentPeople != null && !currentPeople.isEmpty()) {
                // Show confirmation dialog
                new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.clear_all_data))
                    .setMessage(getString(R.string.confirm_clear_data))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        // Delete all people one by one
                        List<User> peopleCopy = new ArrayList<>(currentPeople);
                        for (User person : peopleCopy) {
                            viewModel.deletePerson(person.getId());
                        }
                        Toast.makeText(requireContext(), getString(R.string.all_data_cleared), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
            } else {
                Toast.makeText(requireContext(), getString(R.string.no_data_to_clear), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getPeopleList().observe(getViewLifecycleOwner(), users -> {
            Log.d(TAG, "People list updated, size: " + (users != null ? users.size() : 0));
            
            if (users != null) {
                for (User user : users) {
                    Log.d(TAG, "User: " + user.getName() + ", Age: " + user.getAge());
                }
                
                // Update UI based on whether list is empty
                if (users.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
                
                adapter.submitList(users);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onPersonClick(User user) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putInt("personId", user.getId());
        navController.navigate(R.id.action_navigation_people_to_addEditPerson, args);
    }
} 