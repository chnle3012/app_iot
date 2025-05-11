package com.example.btl_iot.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.model.HistoryResponse;
import com.example.btl_iot.data.repository.AuthRepository;
import com.example.btl_iot.data.repository.HistoryRepository;

public class HistoryViewModel extends AndroidViewModel {
    private HistoryRepository historyRepository;
    private MutableLiveData<HistoryResponse.History> selectedHistory = new MutableLiveData<>();

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepository = new HistoryRepository();
    }

    // 5 tham số: token, page, limit, start, end
    public LiveData<AuthRepository.Resource<HistoryResponse>> getHistory(
            String token, Integer page, Integer limit, String start, String end
    ) {
        return historyRepository.getHistory(token, page, limit, start, end);
    }
    
    // Store selected history for detail view
    public void setSelectedHistory(HistoryResponse.History history) {
        selectedHistory.setValue(history);
    }
    
    // Get selected history
    public LiveData<HistoryResponse.History> getSelectedHistory() {
        return selectedHistory;
    }
    
    // Delete history record
    public LiveData<DeleteResult> deleteHistory(int historyId) {
        MutableLiveData<DeleteResult> result = new MutableLiveData<>();
        
        // In a real app, this would call the API to delete the history
        // For now, we'll simulate success
        result.setValue(new DeleteResult(true, "History deleted successfully"));
        
        return result;
    }
    
    // Helper class for delete operation result
    public static class DeleteResult {
        private final boolean success;
        private final String message;
        
        public DeleteResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
