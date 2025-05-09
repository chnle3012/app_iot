package com.example.btl_iot.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.model.Person;
import com.example.btl_iot.data.repository.PeopleRepository;

import java.util.List;

public class PeopleViewModel extends AndroidViewModel {
    private final PeopleRepository repository;
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> addPersonSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> updatePersonSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> selectedPersonId = new MutableLiveData<>(-1);
    private final MutableLiveData<Person> selectedPerson = new MutableLiveData<>();

    public PeopleViewModel(Application application) {
        super(application);
        repository = new PeopleRepository();
    }

    public LiveData<PeopleRepository.Resource<List<Person>>> getPeopleList() {
        return repository.getPeopleList();
    }
    
    public LiveData<PeopleRepository.Resource<Person>> getPersonDetail(int peopleId) {
        return repository.getPersonDetail(peopleId);
    }
    
    public LiveData<PeopleRepository.Resource<Person>> addPerson(String name, int age, Uri imageUri) {
        return repository.addPerson(name, age, imageUri, getApplication());
    }
    
    public LiveData<PeopleRepository.Resource<Person>> updatePerson(int peopleId, String name, int age, Uri imageUri) {
        return repository.updatePerson(peopleId, name, age, imageUri, getApplication());
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<Boolean> getAddPersonSuccess() {
        return addPersonSuccess;
    }
    
    public void setAddPersonSuccess(boolean success) {
        addPersonSuccess.setValue(success);
    }
    
    public LiveData<Boolean> getUpdatePersonSuccess() {
        return updatePersonSuccess;
    }
    
    public void setUpdatePersonSuccess(boolean success) {
        updatePersonSuccess.setValue(success);
    }
    
    public LiveData<Integer> getSelectedPersonId() {
        return selectedPersonId;
    }
    
    public void setSelectedPersonId(int id) {
        selectedPersonId.setValue(id);
    }
    
    public LiveData<Person> getSelectedPerson() {
        return selectedPerson;
    }
    
    public void setSelectedPerson(Person person) {
        selectedPerson.setValue(person);
    }

    public void setToastMessage(String message) {
        toastMessage.setValue(message);
    }

    public void refreshPeopleList() {
        // This will trigger the API call again
        repository.getPeopleList();
    }
} 