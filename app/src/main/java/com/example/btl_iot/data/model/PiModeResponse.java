package com.example.btl_iot.data.model;

public class PiModeResponse {
    private boolean success;
    private String message;
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private Long piId;
        private String description;
        private ModeType mode;

        public long getPiId() {
            return piId;
        }

        public String getDescription() {
            return description;
        }

        public ModeType getMode() {
            return mode;
        }
    }
} 