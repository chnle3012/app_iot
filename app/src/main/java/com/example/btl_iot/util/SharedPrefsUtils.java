package com.example.btl_iot.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtils {
    
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static void saveAuthToken(Context context, String token, long expiration) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(Constants.KEY_AUTH_TOKEN, token);
        editor.putLong(Constants.KEY_TOKEN_EXPIRATION, expiration);
        editor.apply();
    }
    
    public static String getAuthToken(Context context) {
        return getSharedPreferences(context).getString(Constants.KEY_AUTH_TOKEN, null);
    }
    
    public static long getTokenExpiration(Context context) {
        return getSharedPreferences(context).getLong(Constants.KEY_TOKEN_EXPIRATION, 0);
    }
    
    public static void clearAuthToken(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(Constants.KEY_AUTH_TOKEN);
        editor.remove(Constants.KEY_TOKEN_EXPIRATION);
        editor.apply();
    }
    
    public static boolean isLoggedIn(Context context) {
        String token = getAuthToken(context);
        long expiration = getTokenExpiration(context);
        long currentTime = System.currentTimeMillis();
        
        return token != null && expiration > currentTime;
    }
} 