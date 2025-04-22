package com.example.btl_iot.ui.people;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.User;
import com.example.btl_iot.viewmodel.PeopleViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditPersonFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 101;

    private PeopleViewModel viewModel;
    private TextView titleTextView;
    private ImageView avatarImageView;
    private TextInputEditText nameEditText;
    private TextInputEditText ageEditText;
    private Button choosePhotoButton;
    private Button takePhotoButton;
    private Button saveButton;
    private Button deleteButton;

    private int personId = -1;
    private Uri currentPhotoUri;
    private String currentImagePath;
    private boolean isEditMode = false;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        currentPhotoUri = selectedImageUri;
                        loadImage(selectedImageUri);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && currentPhotoUri != null) {
                    loadImage(currentPhotoUri);
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
        
        // Initialize views
        titleTextView = view.findViewById(R.id.txt_title);
        avatarImageView = view.findViewById(R.id.img_person_avatar);
        nameEditText = view.findViewById(R.id.input_name);
        ageEditText = view.findViewById(R.id.input_age);
        choosePhotoButton = view.findViewById(R.id.btn_choose_image);
        takePhotoButton = view.findViewById(R.id.btn_take_photo);
        saveButton = view.findViewById(R.id.btn_save);
        deleteButton = view.findViewById(R.id.btn_delete);

        // Get arguments
        Bundle args = getArguments();
        if (args != null) {
            personId = args.getInt("personId", -1);
            isEditMode = personId != -1;
        }

        setupViews();
        setupButtons();
        observeViewModel();

        // Load user data if in edit mode
        if (isEditMode) {
            loadUserData();
        }
    }

    private void setupViews() {
        titleTextView.setText(isEditMode ? "Edit Person" : "Add Person");
        deleteButton.setVisibility(isEditMode ? View.VISIBLE : View.INVISIBLE);
    }

    private void setupButtons() {
        choosePhotoButton.setOnClickListener(v -> openGallery());
        takePhotoButton.setOnClickListener(v -> checkCameraPermission());
        saveButton.setOnClickListener(v -> savePerson());
        deleteButton.setOnClickListener(v -> deletePerson());
    }

    private void observeViewModel() {
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (!TextUtils.isEmpty(message)) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOperationComplete().observe(getViewLifecycleOwner(), isComplete -> {
            if (isComplete) {
                viewModel.resetOperationStatus();
                navigateBack();
            }
        });
    }

    private void loadUserData() {
        viewModel.getPersonById(personId).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nameEditText.setText(user.getName());
                ageEditText.setText(String.valueOf(user.getAge()));
                currentImagePath = user.getImageUrl();
                
                if (currentImagePath != null && !currentImagePath.isEmpty()) {
                    Glide.with(requireContext())
                            .load(new File(currentImagePath))
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_camera)
                            .centerCrop()
                            .into(avatarImageView);
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                currentPhotoUri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                cameraLauncher.launch(intent);
            }
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = requireContext().getExternalFilesDir(null);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(avatarImageView);
    }

    private void savePerson() {
        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String ageText = ageEditText.getText() != null ? ageEditText.getText().toString().trim() : "";
        
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int age;
        try {
            age = TextUtils.isEmpty(ageText) ? 0 : Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (isEditMode) {
            viewModel.updatePerson(personId, name, age, currentPhotoUri, currentImagePath);
        } else {
            viewModel.addPerson(name, age, currentPhotoUri);
        }
    }

    private void deletePerson() {
        if (isEditMode) {
            viewModel.deletePerson(personId);
        }
    }

    private void navigateBack() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 