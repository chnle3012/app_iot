package com.example.btl_iot.data.model;

import com.google.gson.annotations.SerializedName;

public class HistoryResponse {
    private boolean success;
    private String message;
    private History data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public History getData() {
        return data;
    }
}