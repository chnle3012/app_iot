package com.example.btl_iot.data.model;

import com.google.gson.annotations.SerializedName;

public class DeleteWarningResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
} 