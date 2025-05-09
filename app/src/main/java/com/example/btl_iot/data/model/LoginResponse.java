package com.example.btl_iot.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private boolean success;
    private String message;
    private TokenData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public TokenData getData() {
        return data;
    }

    public static class TokenData {
        private String token;
        
        @SerializedName("tokenType")
        private String tokenType;
        
        private long expiration;

        public String getToken() {
            return token;
        }

        public String getTokenType() {
            return tokenType;
        }

        public long getExpiration() {
            return expiration;
        }
        
        public String getFormattedToken() {
            return tokenType + " " + token;
        }
    }
} 