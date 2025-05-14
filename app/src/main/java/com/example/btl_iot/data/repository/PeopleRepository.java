package com.example.btl_iot.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.data.api.ApiService;
import com.example.btl_iot.data.model.AddPersonResponse;
import com.example.btl_iot.data.model.DeletePersonResponse;
import com.example.btl_iot.data.model.PeopleResponse;
import com.example.btl_iot.data.model.Person;
import com.example.btl_iot.data.model.PersonDetailResponse;
import com.example.btl_iot.util.FileUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleRepository {
    private static final String TAG = "PeopleRepository";
    
    private final ApiService apiService;
    private final MutableLiveData<Resource<List<Person>>> peopleList = new MutableLiveData<>();
    private final MutableLiveData<Resource<Person>> addPersonResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Person>> personDetailResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Person>> updatePersonResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> deletePersonResult = new MutableLiveData<>();

    public PeopleRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<Person>>> getPeopleList() {
        loadPeopleFromApi();
        return peopleList;
    }

    public LiveData<Resource<Person>> getPersonDetail(int peopleId) {
        // Show loading state
        personDetailResult.setValue(Resource.loading(null));
        
        // Make API call
        apiService.getPersonDetail(peopleId).enqueue(new Callback<PersonDetailResponse>() {
            @Override
            public void onResponse(Call<PersonDetailResponse> call, Response<PersonDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PersonDetailResponse detailResponse = response.body();
                    if (detailResponse.isSuccess() && detailResponse.getData() != null) {
                        Person person = detailResponse.getData();
                        personDetailResult.setValue(Resource.success(person));
                    } else {
                        String errorMsg = detailResponse.getMessage() != null ? 
                                detailResponse.getMessage() : "Lỗi khi lấy chi tiết người dùng";
                        personDetailResult.setValue(Resource.error(errorMsg, null));
                    }
                } else {
                    personDetailResult.setValue(Resource.error("Lỗi: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<PersonDetailResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                personDetailResult.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
        
        return personDetailResult;
    }

    public LiveData<Resource<Person>> addPerson(String name, String identificationId, String gender, String birthday, Uri imageUri, Context context) {
        // Show loading state
        addPersonResult.setValue(Resource.loading(null));
        
        try {
            // Tạo RequestBody cho name và age
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), identificationId);
            RequestBody genderBody = RequestBody.create(MediaType.parse("text/plain"), gender);
            RequestBody birthdayBody = RequestBody.create(MediaType.parse("text/plain"), birthday);
            
            // Tạo MultipartBody.Part cho file ảnh
            File imageFile = FileUtils.getFileFromUri(context, imageUri);
            if (imageFile == null) {
                addPersonResult.setValue(Resource.error("Không thể đọc file ảnh", null));
                return addPersonResult;
            }
            
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
            
            // Gọi API
            apiService.addPerson(nameBody, idBody, genderBody, birthdayBody, filePart).enqueue(new Callback<AddPersonResponse>() {
                @Override
                public void onResponse(Call<AddPersonResponse> call, Response<AddPersonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AddPersonResponse addResponse = response.body();
                        if (addResponse.isSuccess() && addResponse.getData() != null) {
                            Person newPerson = addResponse.getData();
                            addPersonResult.setValue(Resource.success(newPerson));
                        } else {
                            String errorMsg = addResponse.getMessage() != null ? 
                                    addResponse.getMessage() : "Lỗi khi thêm người dùng";
                            addPersonResult.setValue(Resource.error(errorMsg, null));
                        }
                    } else {
                        addPersonResult.setValue(Resource.error("Lỗi: " + response.code(), null));
                    }
                }

                @Override
                public void onFailure(Call<AddPersonResponse> call, Throwable t) {
                    Log.e(TAG, "API call failed", t);
                    addPersonResult.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            addPersonResult.setValue(Resource.error("Lỗi: " + e.getMessage(), null));
        }
        
        return addPersonResult;
    }
    
    public LiveData<Resource<Person>> updatePerson(int peopleId, String name, String identificationId, String gender, String birthday, Uri imageUri, Context context) {
        // Show loading state
        updatePersonResult.setValue(Resource.loading(null));
        
        try {
            // Kiểm tra xem có ảnh mới cần upload không
            boolean hasNewImage = imageUri != null && !imageUri.toString().startsWith("http");
            
            if (hasNewImage) {
                // Có ảnh mới, sử dụng API updatePerson có kèm file
                Log.d(TAG, "Đang cập nhật người dùng với ảnh mới");
                
                // Tạo RequestBody cho name và age
                RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
                RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), identificationId);
                RequestBody genderBody = RequestBody.create(MediaType.parse("text/plain"), gender);
                RequestBody birthdayBody = RequestBody.create(MediaType.parse("text/plain"), birthday);
                
                // Tạo MultipartBody.Part cho file ảnh mới
                File imageFile = FileUtils.getFileFromUri(context, imageUri);
                if (imageFile == null) {
                    updatePersonResult.setValue(Resource.error("Không thể đọc file ảnh", null));
                    return updatePersonResult;
                }
                
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
                Log.d(TAG, "Đang upload ảnh mới: " + imageFile.getName());
                
                // Gọi API với ảnh
                apiService.updatePerson(peopleId, nameBody, genderBody, birthdayBody, filePart).enqueue(new Callback<AddPersonResponse>() {
                    @Override
                    public void onResponse(Call<AddPersonResponse> call, Response<AddPersonResponse> response) {
                        handleUpdateResponse(response);
                    }

                    @Override
                    public void onFailure(Call<AddPersonResponse> call, Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        updatePersonResult.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                    }
                });
            } else {
                // Không có ảnh mới, chỉ cập nhật thông tin cơ bản
                Log.d(TAG, "Đang cập nhật người dùng không kèm ảnh mới");
                
                // Gọi API không kèm ảnh
                apiService.updatePersonWithoutImage(peopleId, name, gender, birthday).enqueue(new Callback<AddPersonResponse>() {
                    @Override
                    public void onResponse(Call<AddPersonResponse> call, Response<AddPersonResponse> response) {
                        handleUpdateResponse(response);
                    }

                    @Override
                    public void onFailure(Call<AddPersonResponse> call, Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        updatePersonResult.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            updatePersonResult.setValue(Resource.error("Lỗi: " + e.getMessage(), null));
        }
        
        return updatePersonResult;
    }

    public LiveData<Resource<Void>> deletePerson(int peopleId,
                                                 Context context) {
        deletePersonResult.setValue(Resource.loading(null));
        apiService.deletePerson(peopleId).enqueue(new Callback<DeletePersonResponse>() {
            @Override
            public void onResponse(Call<DeletePersonResponse> call, Response<DeletePersonResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    deletePersonResult.setValue(Resource.success(null));
                } else {
                    String msg = "Error deleting: " + (response.code());
                    deletePersonResult.setValue(Resource.error(msg, null));
                }
            }
            @Override
            public void onFailure(Call<DeletePersonResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                deletePersonResult.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return deletePersonResult;
    }

    // Phương thức xử lý response chung cho cả hai trường hợp
    private void handleUpdateResponse(Response<AddPersonResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            AddPersonResponse updateResponse = response.body();
            if (updateResponse.isSuccess() && updateResponse.getData() != null) {
                Person updatedPerson = updateResponse.getData();
                updatePersonResult.setValue(Resource.success(updatedPerson));
            } else {
                String errorMsg = updateResponse.getMessage() != null ? 
                        updateResponse.getMessage() : "Lỗi khi cập nhật người dùng";
                updatePersonResult.setValue(Resource.error(errorMsg, null));
            }
        } else {
            String errorBody = null;
            try {
                if (response.errorBody() != null) {
                    errorBody = response.errorBody().string();
                }
            } catch (Exception e) {
                Log.e(TAG, "Không thể đọc error body", e);
            }
            
            String errorMsg = "Lỗi: " + response.code();
            if (errorBody != null && !errorBody.isEmpty()) {
                errorMsg += " - " + errorBody;
            }
            
            updatePersonResult.setValue(Resource.error(errorMsg, null));
        }
    }

    private void loadPeopleFromApi() {
        // Show loading state
        peopleList.setValue(Resource.loading(null));
        
        // Make API call
        apiService.getPeople().enqueue(new Callback<PeopleResponse>() {
            @Override
            public void onResponse(Call<PeopleResponse> call, Response<PeopleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PeopleResponse peopleResponse = response.body();
                    if (peopleResponse.isSuccess() && peopleResponse.getData() != null) {
                        List<Person> people = peopleResponse.getData().getContent();
                        peopleList.setValue(Resource.success(people));
                    } else {
                        String errorMsg = peopleResponse.getMessage() != null ? 
                                peopleResponse.getMessage() : "Error fetching people";
                        peopleList.setValue(Resource.error(errorMsg, null));
                    }
                } else {
                    peopleList.setValue(Resource.error("Error: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<PeopleResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                peopleList.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }
    
    // Resource class for handling loading, success, and error states
    public static class Resource<T> {
        public enum Status { SUCCESS, ERROR, LOADING }
        
        private final Status status;
        private final T data;
        private final String message;
        
        private Resource(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }
        
        public static <T> Resource<T> success(T data) {
            return new Resource<>(Status.SUCCESS, data, null);
        }
        
        public static <T> Resource<T> error(String msg, T data) {
            return new Resource<>(Status.ERROR, data, msg);
        }
        
        public static <T> Resource<T> loading(T data) {
            return new Resource<>(Status.LOADING, data, null);
        }
        
        public Status getStatus() {
            return status;
        }
        
        public T getData() {
            return data;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 