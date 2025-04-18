package com.example.btl_iot.ui.warnings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.Warning;
import java.util.ArrayList;
import java.util.List;

public class WarningsFragment extends Fragment {

    private RecyclerView recyclerView;
    private WarningsAdapter adapter;
    private List<Warning> warningList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warnings, container, false);

        recyclerView = view.findViewById(R.id.recycler_warnings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        warningList = new ArrayList<>();
        // Fake data
        warningList.add(new Warning("Warning 1"));
        warningList.add(new Warning("Warning 2"));

        adapter = new WarningsAdapter(warningList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
