package com.example.btl_iot;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.service.MyFirebaseMessagingService;
import com.example.btl_iot.util.SharedPrefsUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApplication extends Application {
    private static Context appContext;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        
        try {
            // Initialize the ApiClient with the application context
            ApiClient.init(appContext);
            
            // Initialize Firebase
            FirebaseApp.initializeApp(this);

//            // Xóa token cũ và lấy token mới
//            refreshFCMToken();
            
            // Get and log the FCM token for debugging purposes
            getAndSaveFCMToken();

        } catch (Exception e) {
            // Ghi log lỗi nhưng không crash
            Log.e(TAG, "Error initializing application", e);
        }
    }
//    private void refreshFCMToken() {
//        try {
//            Log.d(TAG, "Refreshing FCM token...");
//
//            // Xóa token cũ
//            FirebaseMessaging.getInstance().deleteToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Log.d(TAG, "Old FCM token deleted successfully");
//
//                        // Lấy token mới
//                        FirebaseMessaging.getInstance().getToken()
//                            .addOnCompleteListener(tokenTask -> {
//                                if (tokenTask.isSuccessful()) {
//                                    String newToken = tokenTask.getResult();
//                                    Log.d(TAG, "New FCM token obtained: " + newToken);
//
//                                    // Lưu token mới
//                                    SharedPrefsUtils.saveFCMToken(this, newToken);
//
//                                    // Gửi token mới đến server nếu đã đăng nhập
//                                    if (SharedPrefsUtils.isLoggedIn(this)) {
//                                        MyFirebaseMessagingService.sendStoredFCMTokenToServer(this);
//                                    }
//                                } else {
//                                    Log.e(TAG, "Failed to get new FCM token", tokenTask.getException());
//                                }
//                            });
//                    } else {
//                        Log.e(TAG, "Failed to delete old FCM token", task.getException());
//                    }
//                });
//        } catch (Exception e) {
//            Log.e(TAG, "Error refreshing FCM token", e);
//        }
//    }
    
    private void getAndSaveFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Save token locally
                    SharedPrefsUtils.saveFCMToken(this, token);
                    
                    // Log the token for debugging
                    Log.d(TAG, "FCM Token: " + token);
                    
                    // Check if user is logged in, if yes send token to server
                    if (SharedPrefsUtils.isLoggedIn(this)) {
                        MyFirebaseMessagingService.sendStoredFCMTokenToServer(this);
                    } else {
                        Log.d(TAG, "User not logged in, token will be sent after login");
                    }
                });
    }

    public static Context getAppContext() {
        return appContext;
    }
} 