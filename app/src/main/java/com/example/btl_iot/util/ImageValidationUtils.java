package com.example.btl_iot.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector;
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult;
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector.FaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;

public class ImageValidationUtils {

    private static final String TAG = "ImageValidationUtil";
    private static final String FACE_DETECTOR_MODEL = "assets/blaze_face_short_range.tflite";

    public static class FaceDetectionResult {
        public boolean isValid;
        public String message;

        public FaceDetectionResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
    }

    /**
     * Kiểm tra xem ảnh từ Uri có chứa đúng một khuôn mặt người hay không.
     *
     * @param context Context của ứng dụng.
     * @param imageUri Uri của ảnh cần kiểm tra.
     * @return Đối tượng FaceDetectionResult chứa kết quả (isValid) và thông báo (message).
     */
    public static FaceDetectionResult checkSingleFaceInImage(Context context, Uri imageUri) {
        FaceDetector faceDetector = null;
        MPImage mpImage = null;

        try {
            FaceDetectorOptions.Builder optionsBuilder =
                    FaceDetectorOptions.builder()
                            .setBaseOptions(BaseOptions.builder().setModelAssetPath(FACE_DETECTOR_MODEL).build())
                            .setRunningMode(RunningMode.IMAGE)
                            .setMinDetectionConfidence(0.5f);


            FaceDetectorOptions options = optionsBuilder.build();

            faceDetector = FaceDetector.createFromOptions(context, options);

            Bitmap bitmap;
            try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
                if (inputStream == null) {
                    Log.w(TAG, "Không thể mở InputStream từ Uri: " + imageUri);
                    return new FaceDetectionResult(false, "Không thể mở ảnh từ đường dẫn được cung cấp.");
                }
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e(TAG, "Lỗi khi đọc ảnh từ Uri: " + imageUri, e);
                return new FaceDetectionResult(false, "Lỗi khi đọc file ảnh.");
            }

            if (bitmap == null) {
                Log.w(TAG, "Không thể giải mã Bitmap từ Uri: " + imageUri);
                return new FaceDetectionResult(false, "Không thể giải mã hình ảnh. Định dạng có thể không được hỗ trợ.");
            }

            mpImage = new BitmapImageBuilder(bitmap).build();

            FaceDetectorResult detectionResult = faceDetector.detect(mpImage);

            int faceCount = detectionResult.detections().size();

            if (faceCount == 1) {
                return new FaceDetectionResult(true, "Ảnh hợp lệ, chứa một khuôn mặt.");
            } else if (faceCount == 0) {
                return new FaceDetectionResult(false, "Ảnh không chứa khuôn mặt nào.");
            } else {
                return new FaceDetectionResult(false, "Ảnh chứa nhiều hơn một khuôn mặt (" + faceCount + " khuôn mặt được phát hiện).");
            }

        } catch (Exception e) {
            Log.e(TAG, "Lỗi trong quá trình phát hiện khuôn mặt: " + e.getMessage(), e);
            return new FaceDetectionResult(false, "Đã xảy ra lỗi trong quá trình xử lý ảnh: " + e.getMessage());
        } finally {
            if (mpImage != null) {
                mpImage.close();
            }
            if (faceDetector != null) {
                faceDetector.close();
            }
        }
    }
}