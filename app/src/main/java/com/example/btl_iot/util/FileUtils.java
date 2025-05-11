package com.example.btl_iot.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * Chuyển đổi Uri thành File
     * Luôn copy file vào bộ nhớ cache của ứng dụng để tránh lỗi permission
     */
    public static File getFileFromUri(Context context, Uri uri) {
        if (uri == null) return null;
        
        // Luôn copy file vào bộ nhớ cache của ứng dụng để tránh lỗi permission
        try {
            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            File outputFile = new File(context.getCacheDir(), fileName);
            
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(outputFile)) {
                
                if (inputStream == null) {
                    Log.e(TAG, "Failed to open input stream from uri: " + uri);
                    return null;
                }
                
                byte[] buffer = new byte[4 * 1024]; // 4k buffer
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                Log.d(TAG, "Successfully copied file to cache: " + outputFile.getAbsolutePath());
                return outputFile;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error copying file from uri: " + uri, e);
            return null;
        }
    }
} 