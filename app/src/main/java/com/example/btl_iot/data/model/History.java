package com.example.btl_iot.data.model;

public class History {
    private long historyId;
    private String timestamp;
    private People people;
    private String imagePath;
    private String mode;

    public long getHistoryId() {
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

    public static class People {
        private long peopleId;
        private String name;
        private String birthday;
        private String faceImagePath;

        public long getPeopleId() {
            return peopleId;
        }

        public String getName() {
            return name;
        }

        public String getBirthday() {
            return birthday;
        }
        public String getFaceImagePath() {
            return faceImagePath;
        }
    }
}