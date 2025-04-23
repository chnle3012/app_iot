package com.example.btl_iot.data.model;

import java.util.List;

public class WarningResponse {
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
        private List<Warning> content;
        private int totalElements;
        private int totalPages;
        private boolean last;
        private boolean first;

        public List<Warning> getContent() {
            return content;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public boolean isLast() {
            return last;
        }

        public boolean isFirst() {
            return first;
        }
    }

    public static class Warning {
        private int id;
        private String message;
        private String timestamp;

        // Getter and Setter for id
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        // Getter and Setter for message
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        // Getter and Setter for timestamp
        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
