package com.example.btl_iot.ui.people;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.btl_iot.data.model.Person;
import com.example.btl_iot.data.repository.PeopleRepository;
import com.example.btl_iot.viewmodel.PeopleViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class AddEditPersonFragment extends Fragment {

    private static final String TAG = "AddEditPersonFragment";
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    private PeopleViewModel viewModel;
    private TextView titleTextView;
    private TextView messageTextView;
    private TextInputLayout nameLayout;
    private TextInputEditText nameEditText;
    private TextInputLayout identificationLayout;
    private TextInputEditText identificationEditText;
    private TextInputLayout birthdayLayout;
    private TextInputEditText birthdayEditText;
    private RadioGroup genderGroup;
    private Button saveButton;
    private Button backButton;
    private Button submitButton;
    private Button choosePhotoButton;
    private Button takePhotoButton;
    private ImageView imageView;
    private ProgressBar progressBar;
    
    private Uri selectedImageUri = null;
    private Person currentPerson = null;
    private boolean isEditMode = false;
    private boolean hasSelectedNewImage = false;
    
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        Log.d(TAG, "Đã chọn ảnh mới từ gallery: " + imageUri);
                        selectedImageUri = imageUri;
                        hasSelectedNewImage = true;
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
        identificationEditText = view.findViewById(R.id.input_identification_id);
        birthdayEditText = view.findViewById(R.id.input_birthday);
        genderGroup = view.findViewById(R.id.radio_group_gender);
        saveButton = view.findViewById(R.id.btn_save);
        backButton = view.findViewById(R.id.btn_back);
        imageView = view.findViewById(R.id.img_person_avatar);
        choosePhotoButton = view.findViewById(R.id.btn_choose_image);
        takePhotoButton = view.findViewById(R.id.btn_take_photo);
        progressBar = view.findViewById(R.id.progress_bar);
        submitButton = view.findViewById(R.id.btn_submit);
        
        // Kiểm tra xem đang ở chế độ chỉnh sửa hay thêm mới
        checkEditMode();
        
        // Setup giao diện
        setupUI();
        observeViewModel();
    }

    private void checkEditMode() {
        Integer selectedId = viewModel.getSelectedPersonId().getValue();
        Person selectedPerson = viewModel.getSelectedPerson().getValue();
        if (selectedId != null && selectedId > 0 && selectedPerson != null) {
            isEditMode = true;
            currentPerson = selectedPerson;
            prepareEditMode(selectedPerson);
        } else if (selectedId != null && selectedId > 0) {
            isEditMode = true;
            loadPersonDetail(selectedId);
        }
    }

    private void prepareEditMode(Person person) {
        nameEditText.setText(person.getName());
        identificationEditText.setText(person.getIdentificationId());
        birthdayEditText.setText(String.valueOf(person.getBirthday()));
        String gender = person.getGender();
        if (gender != null) {
            if (gender.equalsIgnoreCase("Nam")) genderGroup.check(R.id.radio_male);
            else if (gender.equalsIgnoreCase("Nữ")) genderGroup.check(R.id.radio_female);
        }

        if (person.getFaceImagePath() != null) {
            selectedImageUri = Uri.parse(person.getFaceImagePath());
            loadImage(selectedImageUri);
        }
    }
    
    private void loadPersonDetail(int personId) {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getPersonDetail(personId).observe(getViewLifecycleOwner(), resource -> {
            progressBar.setVisibility(View.GONE);
            
            if (resource.getStatus() == PeopleRepository.Resource.Status.SUCCESS && resource.getData() != null) {
                currentPerson = resource.getData();
                prepareEditMode(currentPerson);
            } else if (resource.getStatus() == PeopleRepository.Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Lỗi: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                navigateBack();
            }
        });
    }


    private void setupUI() {
        titleTextView.setText(isEditMode? "Chỉnh sửa người dùng" : "Thêm người dùng mới");
        messageTextView.setVisibility(isEditMode? View.VISIBLE: View.GONE);

        // Buttons
        choosePhotoButton.setOnClickListener(v -> checkAndRequestStoragePermission());
        takePhotoButton.setVisibility(View.GONE);
        submitButton.setOnClickListener(v -> validateAndSave());
        saveButton.setVisibility(View.GONE);
        backButton.setOnClickListener(v -> navigateBack());
    }
    
    private void observeViewModel() {
        viewModel.getAddPersonSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                viewModel.setAddPersonSuccess(false);
                viewModel.refreshPeopleList();
                navigateBack();
            }
        });
        
        viewModel.getUpdatePersonSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                viewModel.setUpdatePersonSuccess(false);
                viewModel.refreshPeopleList();
                navigateBack();
            }
        });
    }
    
    private void validateAndSave() {
        String name = nameEditText.getText().toString().trim();
        String identificationId = identificationEditText.getText().toString().trim();
        String birthday = birthdayEditText.getText().toString().trim();
        int genderId = genderGroup.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Vui lòng nhập tên");
            return;
        } else nameLayout.setError(null);

        if (TextUtils.isEmpty(birthday)) {
            birthdayEditText.setError("Vui lòng chọn ngày sinh");
            return;
        } else birthdayEditText.setError(null);

        if (genderId == -1) {
            Toast.makeText(requireContext(), "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }
        String gender = ((RadioButton) requireView().findViewById(genderId)).getText().toString();

        if (!isEditMode && selectedImageUri == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show progress
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);
        
        // Call API - phân biệt giữa thêm mới và cập nhật
        if (isEditMode) {
            viewModel.updatePerson(currentPerson.getPeopleId(), name, identificationId, gender, birthday, hasSelectedNewImage?selectedImageUri:null)
                    .observe(getViewLifecycleOwner(), this::handleResult);
        } else {
            viewModel.addPerson(name, identificationId, gender, birthday, selectedImageUri)
                    .observe(getViewLifecycleOwner(), this::handleResult);
        }
//        if (isEditMode) {
//            updatePerson(name, age);
//        } else {
//            addPerson(name, age);
//        }
    }

    private void handleResult(PeopleRepository.Resource<Person> result){
        progressBar.setVisibility(View.GONE);
        submitButton.setEnabled(true);
        if (result.getStatus()==PeopleRepository.Resource.Status.SUCCESS) {
            Toast.makeText(requireContext(), isEditMode?"Cập nhật thành công":"Thêm thành công", Toast.LENGTH_SHORT).show();
            if (isEditMode) viewModel.setUpdatePersonSuccess(true); else viewModel.setAddPersonSuccess(true);
        } else {
            Toast.makeText(requireContext(), "Lỗi: " + result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openGallery();
            }
        } else {
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

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day   = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    birthdayEditText.setText(date);
                    birthdayEditText.setError(null);
                },
                year, month, day
        );

        picker.getDatePicker().setMaxDate(System.currentTimeMillis());

        picker.show();
    }


    private void navigateBack() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigateUp();
    }
} 