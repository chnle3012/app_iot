package com.example.btl_iot.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final String IMAGE_DIR = "person_images";

    public static String saveImageToInternalStorage(Context context, Uri imageUri) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(context.getFilesDir(), IMAGE_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "PERSON_" + timeStamp + ".jpg";
            File outputFile = new File(directory, fileName);

            // Copy and compress the image
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                FileOutputStream fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();
                inputStream.close();
                return outputFile.getAbsolutePath();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
        }
        return null;
    }

    public static String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(context.getFilesDir(), IMAGE_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "PERSON_" + timeStamp + ".jpg";
            File outputFile = new File(directory, fileName);

            // Compress and save the image
            FileOutputStream fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
        }
        return null;
    }

    public static void deleteImageFromStorage(Context context, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        Log.e(TAG, "Failed to delete image: " + imagePath);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting image: " + e.getMessage());
            }
        }
    }

    public static Bitmap loadImageFromStorage(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
        }
        return null;
    }
} 