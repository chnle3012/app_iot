package com.example.btl_iot.data.model;

import java.util.List;

public class HistoryResponse {
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
        private List<History> content;
        private int totalElements;
        private int totalPages;
        private boolean last;
        private boolean first;

        public List<History> getContent() {
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

    public static class History {
        private int historyId;
        private String timestamp;
        private People people;
        private String imagePath;
        private String mode;

        public int getHistoryId() {
            return historyId;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public People getPeople() {
            return people;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getMode() {
            return mode;
        }
    }

    public static class People {

        private int peopleId;
        private String name;

        private int age;

        private String imagePath;

       public int getPeopleId() {
           return peopleId;
       }

        public String getName() {
            return name;
        }

        public int getAge() {
           return age;
        }

        public String getFaceImagePath() {
            return imagePath;
        }

    }
}