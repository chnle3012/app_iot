package com.example.btl_iot.viewmodel;

import android.app.Application;

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

    public PeopleViewModel(Application application) {
        super(application);
        repository = new PeopleRepository();
    }

    public LiveData<PeopleRepository.Resource<List<Person>>> getPeopleList() {
        return repository.getPeopleList();
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setToastMessage(String message) {
        toastMessage.setValue(message);
    }

    public void refreshPeopleList() {
        // This will trigger the API call again
        repository.getPeopleList();
    }
} 