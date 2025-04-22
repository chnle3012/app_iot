package com.example.btl_iot.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.model.User;
import com.example.btl_iot.data.repository.PeopleRepository;
import com.example.btl_iot.util.ImageUtils;

import java.util.List;

public class PeopleViewModel extends AndroidViewModel {
    private final PeopleRepository repository;
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationComplete = new MutableLiveData<>();
    private final MutableLiveData<List<User>> peopleList = new MutableLiveData<>();

    public PeopleViewModel(Application application) {
        super(application);
        repository = new PeopleRepository(application);
    }

    public LiveData<List<User>> getPeopleList() {
        return repository.getPeopleList();
    }

    public LiveData<User> getPersonById(int personId) {
        return repository.getPersonById(personId);
    }

    public void addPerson(String name, int age, Uri imageUri) {
        addPerson(name, age, null, imageUri);
    }

    public void addPerson(String name, int age, String gender, Uri imageUri) {
        if (name.isEmpty()) {
            toastMessage.setValue("Name cannot be empty");
            return;
        }

        if (age <= 0) {
            toastMessage.setValue("Age must be greater than 0");
            return;
        }

        String imagePath = null;
        if (imageUri != null) {
            imagePath = ImageUtils.saveImageToInternalStorage(getApplication(), imageUri);
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setAge(age);
        newUser.setGender(gender);
        newUser.setImageUrl(imagePath);
        
        repository.addPerson(newUser);
        toastMessage.setValue("Person added successfully");
        operationComplete.setValue(true);
    }

    public void updatePerson(int personId, String name, int age, Uri imageUri, String currentImagePath) {
        updatePerson(personId, name, age, null, imageUri, currentImagePath);
    }

    public void updatePerson(int personId, String name, int age, String gender, Uri imageUri, String currentImagePath) {
        if (name.isEmpty()) {
            toastMessage.setValue("Name cannot be empty");
            return;
        }

        if (age <= 0) {
            toastMessage.setValue("Age must be greater than 0");
            return;
        }

        // Handle image update
        String imagePath = currentImagePath;
        if (imageUri != null) {
            // Delete old image if exists and if we're updating with a new one
            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                ImageUtils.deleteImageFromStorage(getApplication(), currentImagePath);
            }
            imagePath = ImageUtils.saveImageToInternalStorage(getApplication(), imageUri);
        }

        User updatedUser = new User(personId, name, age, imagePath);
        updatedUser.setGender(gender);
        repository.updatePerson(updatedUser);
        toastMessage.setValue("Person updated successfully");
        operationComplete.setValue(true);
    }

    public void deletePerson(int personId) {
        repository.deletePerson(personId);
        toastMessage.setValue("Person deleted successfully");
        operationComplete.setValue(true);
    }

    /**
     * Delete all users
     */
    public void deleteAllUsers() {
        repository.deleteAllUsers();
        toastMessage.setValue("All people deleted successfully");
        operationComplete.setValue(true);
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getOperationComplete() {
        return operationComplete;
    }

    public void resetOperationStatus() {
        operationComplete.setValue(false);
    }

    public PeopleRepository getRepository() {
        return repository;
    }
    
    /**
     * Load the people list from the repository
     */
    public void loadPeopleList() {
        // This triggers the LiveData to be updated from the repository
        peopleList.setValue(repository.getPeopleList().getValue());
    }
    
    /**
     * Method to add sample data for testing
     */
    public void addSampleData() {
        repository.addSampleData();
        // Refresh people list after adding sample data
        loadPeopleList();
        // Set operation completed to true to notify observers
        operationComplete.setValue(true);
    }
} 