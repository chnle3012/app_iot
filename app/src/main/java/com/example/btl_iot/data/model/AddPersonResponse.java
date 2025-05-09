package com.example.btl_iot.data.model;

import com.google.gson.annotations.SerializedName;

public class AddPersonResponse {
    private boolean success;
    private String message;
    private Person data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Person getData() {
        return data;
    }
} 