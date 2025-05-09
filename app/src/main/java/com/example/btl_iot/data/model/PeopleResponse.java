package com.example.btl_iot.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PeopleResponse {
    private boolean success;
    private String message;
    private PeopleData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public PeopleData getData() {
        return data;
    }

    public static class PeopleData {
        private List<Person> content;
        private Pageable pageable;
        private boolean last;
        private int totalElements;
        private int totalPages;
        private boolean first;
        private int size;
        private int number;
        private Sort sort;
        private int numberOfElements;
        private boolean empty;

        public List<Person> getContent() {
            return content;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }
    }

    public static class Pageable {
        private int pageNumber;
        private int pageSize;
        private Sort sort;
        private int offset;
        private boolean paged;
        private boolean unpaged;
    }

    public static class Sort {
        private boolean sorted;
        private boolean empty;
        private boolean unsorted;
    }
} 