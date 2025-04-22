package com.example.btl_iot.ui.people;

import android.os.Bundle;
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
import androidx.navigation.Navigation;

import com.example.btl_iot.R;
import com.example.btl_iot.viewmodel.PeopleViewModel;

public class AddEditPersonFragment extends Fragment {

    private PeopleViewModel viewModel;
    private TextView messageTextView;
    private Button backButton;

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
        
        // Hiển thị thông báo
        messageTextView = view.findViewById(R.id.txt_title);
        messageTextView.setText("Chức năng này đã bị vô hiệu hóa. API chỉ hỗ trợ xem dữ liệu.");

        // Ẩn các control không cần thiết
        hideUnnecessaryControls(view);
        
        // Thêm nút quay lại
        backButton = view.findViewById(R.id.btn_save);
        backButton.setText("Quay lại");
        backButton.setOnClickListener(v -> navigateBack());
    }
    
    private void hideUnnecessaryControls(View view) {
        View inputName = view.findViewById(R.id.input_name);
        View inputAge = view.findViewById(R.id.input_age);
        View btnChooseImage = view.findViewById(R.id.btn_choose_image);
        View btnTakePhoto = view.findViewById(R.id.btn_take_photo);
        View btnDelete = view.findViewById(R.id.btn_delete);
        
        if (inputName != null) inputName.setVisibility(View.GONE);
        if (inputAge != null) inputAge.setVisibility(View.GONE);
        if (btnChooseImage != null) btnChooseImage.setVisibility(View.GONE);
        if (btnTakePhoto != null) btnTakePhoto.setVisibility(View.GONE);
        if (btnDelete != null) btnDelete.setVisibility(View.GONE);
    }

    private void navigateBack() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigateUp();
    }
} 