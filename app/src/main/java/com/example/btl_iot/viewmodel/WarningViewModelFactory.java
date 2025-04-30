//package com.example.btl_iot.viewmodel;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.btl_iot.data.repository.WarningRepository;
//
//public class WarningViewModelFactory implements ViewModelProvider.Factory {
//    private final WarningRepository warningRepository;
//
//    public WarningViewModelFactory(WarningRepository warningRepository) {
//        this.warningRepository = warningRepository;
//    }
//
//    @NonNull
//    @Override
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        if (modelClass.isAssignableFrom(WarningViewModel.class)) {
//            return (T) new WarningViewModel(warningRepository);
//        }
//        throw new IllegalArgumentException("Unknown ViewModel class");
//    }
//}
