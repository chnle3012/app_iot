package com.example.btl_iot.data.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.StringJoiner;

public class Person {
    @SerializedName("peopleId")
    private int id;
    
    private String name;
    private String gender;
    private String birthday;
    private String IdentificationId;
    private String faceImagePath;

    public Person(int id, String name, String gender, String birthday, String IdentificationId, String faceImagePath) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.IdentificationId = IdentificationId;
        this.faceImagePath = faceImagePath;
    }

    public int getId() {
        return id;
    }
    
    public int getPeopleId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

     public String getIdentificationId() {
        return IdentificationId;
     }

    public String getFaceImagePath() {
        return faceImagePath;
    }
} 