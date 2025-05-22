package com.example.btl_iot.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.btl_iot.R;
import com.example.btl_iot.data.api.ApiClient;
import com.example.btl_iot.data.api.ApiService;
import com.example.btl_iot.data.model.FCMTokenRequest;
import com.example.btl_iot.data.model.FCMTokenResponse;
import com.example.btl_iot.ui.dashboard.MainDashboardActivity;
import com.example.btl_iot.util.SharedPrefsUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        
        Log.d(TAG, "Message received from: " + message.getFrom());
        
        try {
            // Kiểm tra data payload
            if (message.getData() != null && !message.getData().isEmpty()) {
                Map<String, String> data = message.getData();
                Log.d(TAG, "Message data payload: " + data);
                
                // Extract values from data payload
                String subject = data.get("subject");
                String content = data.get("content");
                String info = data.get("info");
                String timestamp = data.get("timestamp");
                String imageUrl = data.get("image");
                String warningId = data.get("warningId");
                
                String title = subject != null ? subject : "Cảnh báo hệ thống";
                String body = content != null ? content : (info != null ? "Phát hiện cảnh báo: " + info : "Có cảnh báo mới");
                
                // Hiển thị thông báo
                showNotification(title, body, data);
            } else {
                Log.d(TAG, "No data payload");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing FCM message", e);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken called with token: " + token);
        
        // Save token locally
        saveTokenLocally(token);
        
        // Check if user is logged in before sending token to server
        boolean isLoggedIn = SharedPrefsUtils.isLoggedIn(getApplication());
        if (isLoggedIn) {
            sendStoredFCMTokenToServer(getApplication());
        } else {
            Log.d(TAG, "Chưa có JWT, sẽ gửi token sau khi đăng nhập");
        }
    }
    
    private void saveTokenLocally(String token) {
        Log.d(TAG, "Saving FCM token locally: " + token);
        SharedPrefsUtils.saveFCMToken(getApplication(), token);
        
        // Verify token was saved
        String savedToken = SharedPrefsUtils.getFCMToken(getApplication());
        Log.d(TAG, "Verified saved token: " + savedToken);
    }
    /**
     * Hiển thị thông báo
     */
    private void showNotification(String title, String body, Map<String, String> data) {
        try {
            Log.d(TAG, "Showing notification - Title: " + title + ", Body: " + body);
            
            // Tạo intent để mở MainDashboardActivity
            Intent intent = new Intent(this, MainDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            // Mở tab Warnings
            intent.putExtra("OPEN_WARNINGS", true);
            
            // Truyền warningId để mở chi tiết cảnh báo
            if (data != null && data.containsKey("warningId")) {
                String warningId = data.get("warningId");
                intent.putExtra("warningId", warningId);
                Log.d(TAG, "Added warningId to intent: " + warningId);
            }
            
            // Tạo PendingIntent với requestCode ngẫu nhiên để tránh ghi đè
            int requestCode = (int) System.currentTimeMillis();
            PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
    
            // Lấy thông tin channel
            String channelId = getString(R.string.default_notification_channel_id);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            
            // Xây dựng thông báo với BigTextStyle cho nội dung dài
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_stat_ic_notification)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{0, 500, 200, 500}); // Vibration pattern
            
            // Sử dụng BigTextStyle để hiển thị nội dung dài
            if (body != null && body.length() > 40) {
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.bigText(body);
                bigTextStyle.setBigContentTitle(title);
                builder.setStyle(bigTextStyle);
            }
    
            // Hiển thị thông báo
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    
            // Tạo channel cho Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    channelId,
                    getString(R.string.default_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                );
                // Cấu hình channel
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 500, 200, 500});
                channel.setDescription("Thông báo cảnh báo từ hệ thống IoT");
                channel.setShowBadge(true);
                
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Created notification channel: " + channelId);
            }
            
            // Hiển thị thông báo với ID ngẫu nhiên để tránh ghi đè
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification displayed with ID: " + notificationId);
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
        }
    }
    
    public static void sendStoredFCMTokenToServer(Context context) {
        // Get the token from SharedPreferences
        String fcmToken = SharedPrefsUtils.getFCMToken(context);
        Log.d(TAG, "Sending stored FCM token to server. Token: " + fcmToken);
        
        // Check if token exists and user is logged in
        if (fcmToken == null || fcmToken.isEmpty()) {
            Log.e(TAG, "FCM token is null or empty, cannot send to server");
            
            // Try to get a new token
            Log.d(TAG, "Attempting to get a new FCM token");
            com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Getting new FCM token failed", task.getException());
                            return;
                        }

                        // Got new FCM token
                        String newToken = task.getResult();
                        Log.d(TAG, "Got new FCM token: " + newToken);
                        
                        // Save it and try again
                        SharedPrefsUtils.saveFCMToken(context, newToken);
                        sendTokenWithAuth(context, newToken);
                    });
            return;
        }
        
        if (!SharedPrefsUtils.isLoggedIn(context)) {
            Log.e(TAG, "User is not logged in, cannot send FCM token to server");
            return;
        }
        
        // Send the token with authentication
        sendTokenWithAuth(context, fcmToken);
    }
    
    private static void sendTokenWithAuth(Context context, String fcmToken) {
        // Get JWT token
        String jwtToken = SharedPrefsUtils.getAuthToken(context);
        if (jwtToken == null || jwtToken.isEmpty()) {
            Log.e(TAG, "JWT token is null or empty, cannot send FCM token to server");
            return;
        }
        
        Log.d(TAG, "Sending FCM token to server with JWT authentication");
        
        // Prepare authorization header
        String authHeader = "Bearer " + jwtToken;
        
        // Prepare request body
        FCMTokenRequest tokenRequest = new FCMTokenRequest(fcmToken);
        
        // Get API service
        ApiService apiService = ApiClient.getApiService();
        
        try {
            // Make API call
            Call<FCMTokenResponse> call = apiService.updateFCMToken(authHeader, tokenRequest);
            
            call.enqueue(new Callback<FCMTokenResponse>() {
                @Override
                public void onResponse(Call<FCMTokenResponse> call, Response<FCMTokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        FCMTokenResponse tokenResponse = response.body();
                        if (tokenResponse.isSuccess()) {
                            Log.d(TAG, "FCM token successfully sent to server");
                        } else {
                            Log.e(TAG, "Failed to send FCM token: " + tokenResponse.getMessage());
                        }
                    } else {
                        Log.e(TAG, "Failed to send FCM token, response code: " + response.code());
                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<FCMTokenResponse> call, Throwable t) {
                    Log.e(TAG, "Error sending FCM token to server", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception when sending FCM token to server", e);
        }
    }
}
