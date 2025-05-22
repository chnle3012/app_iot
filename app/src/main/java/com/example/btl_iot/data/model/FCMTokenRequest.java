package com.example.btl_iot.data.model;

public class FCMTokenRequest {
    private String token;

    public FCMTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
} 