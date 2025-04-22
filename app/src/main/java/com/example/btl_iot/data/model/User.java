package com.example.btl_iot.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int id;
    private String name;
    private int age;
    private String imageUrl;
    private String gender;

    public User() {
        // Default constructor
    }

    public User(int id, String name, int age, String imageUrl) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.imageUrl = imageUrl;
    }
    
    public User(int id, String name, int age, String imageUrl, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.imageUrl = imageUrl;
        this.gender = gender;
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        age = in.readInt();
        imageUrl = in.readString();
        gender = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeString(imageUrl);
        dest.writeString(gender);
    }
} 