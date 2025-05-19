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
        private int warningId;
        private String info; // Thay từ message thành info
        private String timestamp;
        private String imagePath; // Thêm imagePath nếu cần sử dụng

        // Getter và Setter cho warningId
        public int getWarningId() {
            return warningId;
        }

        public void setWarningId(int warningId) {
            this.warningId = warningId;
        }

        // Getter và Setter cho info (thay vì message)
        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        // Getter và Setter cho timestamp
        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        // Getter và Setter cho imagePath (nếu cần)
        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }
}
