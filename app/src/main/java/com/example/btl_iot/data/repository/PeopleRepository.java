package com.example.btl_iot.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.btl_iot.data.model.User;
import com.example.btl_iot.util.ImageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PeopleRepository {
    private static final String TAG = "PeopleRepository";
    private static final String PREF_NAME = "people_prefs";
    private static final String KEY_PEOPLE = "people_list";
    private static final String KEY_LAST_ID = "last_id";

    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<List<User>> peopleList = new MutableLiveData<>();
    private final Context context;

    public PeopleRepository(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadPeopleFromPrefs();
        
        // If the list is empty, add some sample data
        if (peopleList.getValue() == null || peopleList.getValue().isEmpty()) {
            addSampleData();
        }
    }

    public void addSampleData() {
        List<User> sampleUsers = new ArrayList<>();
        
        User user1 = new User();
        user1.setId(getNewId());
        user1.setName("John Doe");
        user1.setAge(30);
        user1.setGender("Male");
        
        User user2 = new User();
        user2.setId(getNewId());
        user2.setName("Jane Smith");
        user2.setAge(25);
        user2.setGender("Female");
        
        User user3 = new User();
        user3.setId(getNewId());
        user3.setName("Alex Johnson");
        user3.setAge(40);
        user3.setGender("Male");
        
        // Add users one by one to ensure they're properly saved
        List<User> currentList = peopleList.getValue();
        List<User> newList = currentList != null ? new ArrayList<>(currentList) : new ArrayList<>();
        newList.add(user1);
        newList.add(user2);
        newList.add(user3);
        
        peopleList.setValue(newList);
        savePeopleToPrefs();
    }
    
    /**
     * Delete all users from the repository
     */
    public void deleteAllUsers() {
        // Remove images if any
        List<User> currentList = peopleList.getValue();
        if (currentList != null) {
            for (User user : currentList) {
                String imageUrl = user.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageUtils.deleteImageFromStorage(context, imageUrl);
                }
            }
        }
        
        // Clear the list
        peopleList.setValue(new ArrayList<>());
        savePeopleToPrefs();
    }

    public LiveData<List<User>> getPeopleList() {
        return peopleList;
    }

    public LiveData<User> getPersonById(int personId) {
        MutableLiveData<User> personData = new MutableLiveData<>();
        List<User> currentList = peopleList.getValue();
        if (currentList != null) {
            for (User user : currentList) {
                if (user.getId() == personId) {
                    personData.setValue(user);
                    break;
                }
            }
        }
        return personData;
    }

    public void addPerson(User user) {
        int newId = getNewId();
        user.setId(newId);
        
        List<User> currentList = peopleList.getValue();
        List<User> newList = currentList != null ? new ArrayList<>(currentList) : new ArrayList<>();
        newList.add(user);
        peopleList.setValue(newList);
        savePeopleToPrefs();
    }

    public void updatePerson(User updatedUser) {
        List<User> currentList = peopleList.getValue();
        if (currentList != null) {
            List<User> newList = new ArrayList<>(currentList);
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getId() == updatedUser.getId()) {
                    newList.set(i, updatedUser);
                    break;
                }
            }
            peopleList.setValue(newList);
            savePeopleToPrefs();
        }
    }

    public void deletePerson(int personId) {
        List<User> currentList = peopleList.getValue();
        if (currentList != null) {
            List<User> newList = new ArrayList<>(currentList);
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getId() == personId) {
                    String imageUrl = newList.get(i).getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Delete the image file if needed
                        ImageUtils.deleteImageFromStorage(context, imageUrl);
                    }
                    newList.remove(i);
                    break;
                }
            }
            peopleList.setValue(newList);
            savePeopleToPrefs();
        }
    }

    private int getNewId() {
        int lastId = sharedPreferences.getInt(KEY_LAST_ID, 0);
        int newId = lastId + 1;
        sharedPreferences.edit().putInt(KEY_LAST_ID, newId).apply();
        return newId;
    }

    private void loadPeopleFromPrefs() {
        String peopleJson = sharedPreferences.getString(KEY_PEOPLE, "");
        List<User> loadedList = new ArrayList<>();

        if (!peopleJson.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(peopleJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject userJson = jsonArray.getJSONObject(i);
                    User user = new User();
                    user.setId(userJson.getInt("id"));
                    user.setName(userJson.getString("name"));
                    user.setAge(userJson.getInt("age"));
                    user.setImageUrl(userJson.getString("imageUrl"));
                    
                    // Handle gender (might not exist in older saved data)
                    if (userJson.has("gender")) {
                        user.setGender(userJson.getString("gender"));
                    }
                    
                    loadedList.add(user);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing people JSON", e);
            }
        }

        peopleList.setValue(loadedList);
    }

    private void savePeopleToPrefs() {
        List<User> currentList = peopleList.getValue();
        if (currentList == null) return;

        try {
            JSONArray jsonArray = new JSONArray();
            for (User user : currentList) {
                JSONObject userJson = new JSONObject();
                userJson.put("id", user.getId());
                userJson.put("name", user.getName());
                userJson.put("age", user.getAge());
                userJson.put("imageUrl", user.getImageUrl());
                
                // Save gender if it exists
                if (user.getGender() != null) {
                    userJson.put("gender", user.getGender());
                }
                
                jsonArray.put(userJson);
            }
            sharedPreferences.edit().putString(KEY_PEOPLE, jsonArray.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error saving people to JSON", e);
        }
    }
} 