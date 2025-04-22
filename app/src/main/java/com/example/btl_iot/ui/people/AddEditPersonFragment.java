package com.example.btl_iot.ui.people;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.btl_iot.R;
import com.example.btl_iot.data.repository.PeopleRepository;
import com.example.btl_iot.viewmodel.PeopleViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditPersonFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 100;

    private PeopleViewModel viewModel;
    private TextView titleTextView;
    private TextView messageTextView;
    private TextInputLayout nameLayout;
    private TextInputEditText nameEditText;
    private TextInputLayout ageLayout;
    private TextInputEditText ageEditText;
    private Button saveButton;
    private Button backButton;
    private Button choosePhotoButton;
    private Button takePhotoButton;
    private Button submitButton;
    private ImageView imageView;
    private ProgressBar progressBar;
    
    private Uri selectedImageUri = null;
    
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        selectedImageUri = imageUri;
                        loadImage(imageUri);
                    }
                }
            });
            
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(requireContext(), "Cần quyền truy cập bộ nhớ để chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            });

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
        choosePhotoButton = view.findViewById(R.id.btn_choose_image);
        takePhotoButton = view.findViewById(R.id.btn_take_photo);
        progressBar = view.findViewById(R.id.progress_bar);
        submitButton = view.findViewById(R.id.btn_submit);
        
        // Setup giao diện
        setupUI();
        observeViewModel();
    }
    
    private void setupUI() {
        // Hiển thị tiêu đề
        titleTextView.setText("Thêm người dùng mới");
        
        // Ẩn thông báo "chỉ hỗ trợ xem dữ liệu" vì bây giờ chúng ta đã hỗ trợ thêm mới
        if (messageTextView != null) {
            messageTextView.setVisibility(View.GONE);
        }
        
        // Thiết lập các trường nhập liệu
        if (nameLayout != null) nameLayout.setVisibility(View.VISIBLE);
        if (nameEditText != null) nameEditText.setVisibility(View.VISIBLE);
        if (ageLayout != null) ageLayout.setVisibility(View.VISIBLE);
        if (ageEditText != null) ageEditText.setVisibility(View.VISIBLE);
        
        // Thiết lập nút chọn ảnh
        if (choosePhotoButton != null) {
            choosePhotoButton.setVisibility(View.VISIBLE);
            choosePhotoButton.setOnClickListener(v -> checkAndRequestStoragePermission());
        }
        
        // Ẩn nút chụp ảnh (để đơn giản hóa, chỉ sử dụng gallery)
        if (takePhotoButton != null) {
            takePhotoButton.setVisibility(View.GONE);
        }
        
        // Thiết lập nút submit
        if (submitButton != null) {
            submitButton.setVisibility(View.VISIBLE);
            submitButton.setOnClickListener(v -> validateAndSave());
        }
        
        // Thiết lập nút lưu (ẩn đi vì đã có nút submit)
        if (saveButton != null) {
            saveButton.setVisibility(View.GONE);
        }
        
        // Thiết lập nút quay lại
        if (backButton != null) {
            backButton.setVisibility(View.VISIBLE);
            backButton.setText("Quay lại");
            backButton.setOnClickListener(v -> navigateBack());
        }
    }
    
    private void observeViewModel() {
        viewModel.getAddPersonSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // Reset flag
                viewModel.setAddPersonSuccess(false);
                // Refresh danh sách và quay lại
                viewModel.refreshPeopleList();
                navigateBack();
            }
        });
    }
    
    private void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES instead of READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
            // For older Android versions
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openGallery();
            }
        }
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
    
    private void loadImage(Uri imageUri) {
        Glide.with(requireContext())
                .load(imageUri)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_camera)
                .centerCrop()
                .into(imageView);
    }
    
    private void validateAndSave() {
        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String ageText = ageEditText.getText() != null ? ageEditText.getText().toString().trim() : "";
        
        // Validate name
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Vui lòng nhập tên");
            return;
        } else {
            nameLayout.setError(null);
        }
        
        // Validate age
        if (TextUtils.isEmpty(ageText)) {
            ageLayout.setError("Vui lòng nhập tuổi");
            return;
        } else {
            ageLayout.setError(null);
        }
        
        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                ageLayout.setError("Tuổi phải từ 1-120");
                return;
            }
        } catch (NumberFormatException e) {
            ageLayout.setError("Tuổi không hợp lệ");
            return;
        }
        
        // Validate image
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show progress
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);
        
        // Call API
        viewModel.addPerson(name, age, selectedImageUri).observe(getViewLifecycleOwner(), result -> {
            // Hide progress
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            submitButton.setEnabled(true);
            
            if (result.getStatus() == PeopleRepository.Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Thêm người dùng thành công", Toast.LENGTH_SHORT).show();
                viewModel.setAddPersonSuccess(true);
            } else if (result.getStatus() == PeopleRepository.Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Lỗi: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateBack() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigateUp();
    }
} 