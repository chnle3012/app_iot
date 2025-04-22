package com.example.btl_iot.ui.people;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.btl_iot.R;
import com.example.btl_iot.viewmodel.PeopleViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditPersonFragment extends Fragment {

    private PeopleViewModel viewModel;
    private TextView titleTextView;
    private TextView messageTextView;
    private TextInputLayout nameLayout;
    private TextInputEditText nameEditText;
    private TextInputLayout ageLayout;
    private TextInputEditText ageEditText;
    private Button saveButton;
    private Button backButton;
    private ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PeopleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo các view
        titleTextView = view.findViewById(R.id.txt_title);
        messageTextView = view.findViewById(R.id.txt_message);
        nameLayout = view.findViewById(R.id.layout_name);
        nameEditText = view.findViewById(R.id.input_name);
        ageLayout = view.findViewById(R.id.layout_age);
        ageEditText = view.findViewById(R.id.input_age);
        saveButton = view.findViewById(R.id.btn_save);
        backButton = view.findViewById(R.id.btn_back);
        imageView = view.findViewById(R.id.img_person_avatar);
        
        // Setup giao diện
        setupUI();
    }
    
    private void setupUI() {
        // Hiển thị tiêu đề
        titleTextView.setText("Thêm người dùng mới");
        
        // Hiển thị thông báo rằng API chỉ hỗ trợ xem dữ liệu
        if (messageTextView != null) {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText("Lưu ý: API hiện tại chỉ hỗ trợ xem dữ liệu người dùng. Chức năng thêm mới sẽ không hoạt động.");
        }
        
        // Thiết lập các trường nhập liệu (demo UI)
        if (nameLayout != null) nameLayout.setVisibility(View.VISIBLE);
        if (nameEditText != null) nameEditText.setVisibility(View.VISIBLE);
        if (ageLayout != null) ageLayout.setVisibility(View.VISIBLE);
        if (ageEditText != null) ageEditText.setVisibility(View.VISIBLE);
        
        // Thiết lập nút lưu
        if (saveButton != null) {
            saveButton.setText("Lưu");
            saveButton.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "API chưa hỗ trợ chức năng thêm mới người dùng", Toast.LENGTH_LONG).show();
            });
        }
        
        // Thiết lập nút quay lại
        if (backButton != null) {
            backButton.setVisibility(View.VISIBLE);
            backButton.setText("Quay lại");
            backButton.setOnClickListener(v -> navigateBack());
        }
    }

    private void navigateBack() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigateUp();
    }
} 