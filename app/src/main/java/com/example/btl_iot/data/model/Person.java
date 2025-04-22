package com.example.btl_iot.data.model;

import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("peopleId")
    private int id;
    
    private String name;
    private int age;
    private String faceImagePath;

    public Person(int id, String name, int age, String faceImagePath) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.faceImagePath = faceImagePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getFaceImagePath() {
        return faceImagePath;
    }
} 