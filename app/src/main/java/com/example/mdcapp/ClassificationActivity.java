package com.example.mdcapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClassificationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

    private PreviewView previewView;
    private ImageView processedImageView;
    private TextView contourAreaTextView;
    private TextView percentageTextView;
    private TextView classificationTextView;
    private ImageButton selectImageButton;
    private ImageButton captureImageButton;
    private ImageCapture imageCapture;
    private ImageView overlayImageView;
    private TextView eligibilityTextView;

    private Interpreter tflite;
    private static final String MODEL_NAME = "hasil.tflite";
    private String[] classNames = {"1000", "10000", "100000", "2000", "20000", "5000", "50000"};
    private static final int IMG_SIZE_H = 300;
    private static final int IMG_SIZE_W = 150;

    // Define fullScalePixels for each class
    private static final double FULL_SCALE_PIXELS_1000 = 104491.0;
    private static final double FULL_SCALE_PIXELS_2000 = 104491.0;
    private static final double FULL_SCALE_PIXELS_5000 = 106259.0;
    private static final double FULL_SCALE_PIXELS_10000 = 107585.0;
    private static final double FULL_SCALE_PIXELS_20000 = 108911.0;
    private static final double FULL_SCALE_PIXELS_50000 = 110679.0;
    private static final double FULL_SCALE_PIXELS_100000 = 112005.0;

    private double fullScalePixels; // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        previewView = findViewById(R.id.previewView);
        //processedImageView = findViewById(R.id.processedImageView);
        //contourAreaTextView = findViewById(R.id.contourAreaTextView);
        //percentageTextView = findViewById(R.id.percentageTextView);
        //classificationTextView = findViewById(R.id.classificationTextView);
        selectImageButton = findViewById(R.id.selectImageButton);
        captureImageButton = findViewById(R.id.captureImageButton);
        overlayImageView = findViewById(R.id.overlayImageView);
        eligibilityTextView = findViewById(R.id.eligibilityTextView);
        ImageButton flashToggleButton = findViewById(R.id.flashToggleButton);

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV");
            return;
        }

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        captureImageButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        });

        startCamera();

        flashToggleButton.setOnClickListener(v -> {
            boolean isFlashOn = flashToggleButton.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.l_on).getConstantState());

            if (isFlashOn) {
                // Nonaktifkan mode flash dan ganti ikon
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                flashToggleButton.setImageResource(R.drawable.l_off);
            } else {
                // Aktifkan mode flash dan ganti ikon
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                flashToggleButton.setImageResource(R.drawable.l_on);
            }
        });


        Button btnSetThreshold = findViewById(R.id.btn_set_threshold);
        btnSetThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showThresholdDialog(); // Memunculkan popup saat tombol ditekan
            }
        });

        previewView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = previewView.getWidth();
                previewView.getLayoutParams().height = width; // Set height to match width for 1:1 ratio
                previewView.requestLayout();

                overlayImageView.getLayoutParams().width = width;
                overlayImageView.getLayoutParams().height = width;
                overlayImageView.requestLayout();
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder().build();

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

        previewView.post(() -> {
            int width = previewView.getWidth();
            int height = previewView.getHeight();
            int size = Math.min(width, height);
        });
    }

    private void captureImage() {
        File photoFile = new File(getFilesDir(), "captured_image.jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                processImage(bitmap);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Photo capture failed: " + exception.getMessage());
            }
        });
    }

    // Fungsi untuk menerapkan gamma correction
    private Mat gammaCorrection(Mat src, double gamma) {
        Mat lutMatrix = new Mat(1, 256, CvType.CV_8UC1);
        for (int i = 0; i < 256; i++) {
            lutMatrix.put(0, i, Math.pow(i / 255.0, gamma) * 255.0);
        }
        Mat dst = new Mat();
        Core.LUT(src, lutMatrix, dst);
        return dst;
    }

    private void processImage(Bitmap originalBitmap) {
        // Pastikan gambar memiliki rasio 1:1 dengan memotong dari tengah
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int newDimen = Math.min(width, height);

        Bitmap squareBitmap = Bitmap.createBitmap(originalBitmap,
                (width - newDimen) / 2,
                (height - newDimen) / 2,
                newDimen,
                newDimen);

        // Ubah ukuran gambar menjadi 500x500
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(squareBitmap, 1080, 1080, true);

        // Crop gambar setelah resize
        int cropWidth = 700; // Lebar crop
        int cropHeight = 350; // Tinggi crop
        int centerX = resizedBitmap.getWidth() / 2;
        int centerY = resizedBitmap.getHeight() / 2;

        // Menghitung batas crop
        int cropX = Math.max(centerX - cropWidth / 2, 0);
        int cropY = Math.max(centerY - cropHeight / 2, 0);
        int cropRight = Math.min(cropX + cropWidth, resizedBitmap.getWidth());
        int cropBottom = Math.min(cropY + cropHeight, resizedBitmap.getHeight());

        Bitmap croppedBitmap = Bitmap.createBitmap(resizedBitmap, cropX, cropY, cropRight - cropX, cropBottom - cropY);
        Mat croppedImage = new Mat();
        Utils.bitmapToMat(croppedBitmap, croppedImage);

        // Apply thresholding
        Mat binaryMat = new Mat();
        Imgproc.threshold(croppedImage, binaryMat, thresholdValue, 255, Imgproc.THRESH_BINARY);

        // Konversi croppedImage ke grayscale
        Mat croppedGray = new Mat();
        Imgproc.cvtColor(binaryMat, croppedGray, Imgproc.COLOR_BGR2GRAY);

        // Equalize histogram pada cropped image
        Mat croppedEqualized = new Mat();
        Imgproc.equalizeHist(croppedGray, croppedEqualized);

        // Terapkan Gaussian blur pada cropped image
        Mat croppedBlurred = new Mat();
        Imgproc.GaussianBlur(croppedEqualized, croppedBlurred, new Size(5, 5), 0);

        // Deteksi tepi menggunakan Sobel pada cropped image
        Mat croppedSobelX = new Mat();
        Mat croppedSobelY = new Mat();
        Imgproc.Sobel(croppedBlurred, croppedSobelX, CvType.CV_64F, 1, 0, 5);
        Imgproc.Sobel(croppedBlurred, croppedSobelY, CvType.CV_64F, 0, 1, 5);
        Mat croppedSobelEdges = new Mat();
        Core.magnitude(croppedSobelX, croppedSobelY, croppedSobelEdges);

        Core.normalize(croppedSobelEdges, croppedSobelEdges, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);

        // Threshold untuk gambar biner dari tepi Sobel pada cropped image
        Mat croppedSobelBinary = new Mat();
        Imgproc.threshold(croppedSobelEdges, croppedSobelBinary, 50, 255, Imgproc.THRESH_BINARY);

        // Temukan kontur pada cropped image
        List<MatOfPoint> croppedContours = new ArrayList<>();
        Mat croppedHierarchy = new Mat();
        Imgproc.findContours(croppedSobelBinary, croppedContours, croppedHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double croppedMaxArea = 0;
        MatOfPoint croppedMaxContour = null;
        for (MatOfPoint contour : croppedContours) {
            double area = Imgproc.contourArea(contour);
            if (area > croppedMaxArea) {
                croppedMaxArea = area;
                croppedMaxContour = contour;
            }
        }

        // Jika kontur berada di luar rentang, tampilkan gambar default
        if (croppedMaxArea < 0 || croppedMaxArea > 1000000.0) {
            // Setel gambar default
            processedImageView.setImageResource(R.drawable.tdkdikenali);
            contourAreaTextView.setText("Luas kontur: Tidak dikenal");
            percentageTextView.setText("Persentase kontur: Tidak dikenal");
            classificationTextView.setText("Kelas Terprediksi: Tidak dikenal");
            eligibilityTextView.setText("Keterangan Kelayakan: Tidak dikenal");
        } else {
            double percentageArea = (croppedMaxArea / fullScalePixels) * 100;
            if (percentageArea > 100) {
                percentageArea = 100; // Batasi persentase hingga 100%
            }
            String formattedPercentage = String.format("%.0f", percentageArea);

            if (croppedMaxContour != null) {
                // Gunakan croppedImage sebagai latar belakang untuk gambar kontur
                Mat contourImage = new Mat(croppedImage.size(), CvType.CV_8UC3);
                croppedImage.copyTo(contourImage); // Copy cropped image as background
                List<MatOfPoint> croppedMaxContourList = new ArrayList<>();
                croppedMaxContourList.add(croppedMaxContour);
                Imgproc.drawContours(contourImage, croppedMaxContourList, -1, new Scalar(0, 255, 0), 2);

                Moments moments = Imgproc.moments(croppedMaxContour);
                int centroidX = (int) (moments.m10 / moments.m00);
                int centroidY = (int) (moments.m01 / moments.m00);


                Bitmap resultBitmap = Bitmap.createBitmap(contourImage.cols(), contourImage.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(contourImage, resultBitmap);

                //processedImageView.setImageBitmap(resultBitmap);
                //contourAreaTextView.setText("Luas kontur: " + croppedMaxArea);
                //percentageTextView.setText("Persentase kontur: " + formattedPercentage + "%");
                Bitmap resizedForTflite = Bitmap.createScaledBitmap(croppedBitmap, IMG_SIZE_H, IMG_SIZE_W, true);
                classifyImage(resizedForTflite, resultBitmap, croppedMaxArea);
            }
        }
    }




    private Uri saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getApplicationContext().getCacheDir(), "temp_image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void classifyImage(Bitmap bitmap, Bitmap resultBitmap, double croppedMaxArea) {
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);

        float[][] result = new float[1][classNames.length];
        tflite.run(byteBuffer, result);

        float maxProb = 0;
        int maxIndex = 0;
        for (int i = 0; i < classNames.length; i++) {
            if (result[0][i] > maxProb) {
                maxProb = result[0][i];
                maxIndex = i;
            }
        }

        // Update fullScalePixels based on the detected class
        switch (classNames[maxIndex]) {
            case "1000":
                fullScalePixels = FULL_SCALE_PIXELS_1000;
                break;
            case "10000":
                fullScalePixels = FULL_SCALE_PIXELS_10000;
                break;
            case "100000":
                fullScalePixels = FULL_SCALE_PIXELS_100000;
                break;
            case "2000":
                fullScalePixels = FULL_SCALE_PIXELS_2000;
                break;
            case "20000":
                fullScalePixels = FULL_SCALE_PIXELS_20000;
                break;
            case "5000":
                fullScalePixels = FULL_SCALE_PIXELS_5000;
                break;
            case "50000":
                fullScalePixels = FULL_SCALE_PIXELS_50000;
                break;
        }

        double percentageArea = (croppedMaxArea / fullScalePixels) * 100;
        if (percentageArea > 100) {
            percentageArea = 100; // Batasi persentase hingga 100%
        }
        String formattedPercentage = String.format("%.0f", percentageArea);

        //classificationTextView.setText("Kelas Terprediksi: " + classNames[maxIndex]);

        String eligibilityText = percentageArea > 67 ? "LAYAK TUKAR" : "TIDAK LAYAK TUKAR";
        //eligibilityTextView.setText("Keterangan Kelayakan: " + eligibilityText);

        Uri originalImageUri = saveBitmapToFile(resultBitmap);
        if (originalImageUri != null) {
            Intent resultIntent = new Intent(this, ResultActivity.class);
            resultIntent.putExtra("originalImageUri", originalImageUri.toString());
            resultIntent.putExtra("contourArea", String.valueOf(croppedMaxArea));
            resultIntent.putExtra("percentage", formattedPercentage);
            resultIntent.putExtra("classification", classNames[maxIndex]);
            resultIntent.putExtra("eligibilityText", eligibilityText);
            startActivity(resultIntent);
        }

    }


    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * IMG_SIZE_H * IMG_SIZE_W * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[IMG_SIZE_H * IMG_SIZE_W];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < intValues.length; i++) {
            int val = intValues[i];
            byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
            byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
            byteBuffer.putFloat((val & 0xFF) / 255.0f);
        }
        return byteBuffer;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd(MODEL_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == this.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                processImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
    }

    // Variabel untuk menyimpan nilai threshold yang bisa diubah
    private int thresholdValue = 120; // Nilai default

    private void showThresholdDialog() {
        // Buat AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Threshold");

        // Buat EditText untuk input nilai threshold
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Input hanya angka
        input.setText(String.valueOf(thresholdValue)); // Set nilai default
        builder.setView(input);

        // Set tombol "OK" untuk menyimpan nilai
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputValue = input.getText().toString();
                if (!inputValue.isEmpty()) {
                    thresholdValue = Integer.parseInt(inputValue);
                }
            }
        });

        // Set tombol "Cancel" jika pengguna tidak ingin mengubah nilai
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}