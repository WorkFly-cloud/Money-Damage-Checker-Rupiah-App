package com.example.mdcapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView originalImageView = findViewById(R.id.originalImageView);
        //TextView contourAreaTextView = findViewById(R.id.contourAreaTextView);
        TextView percentageTextView = findViewById(R.id.percentageTextView);
        TextView classificationTextView = findViewById(R.id.classificationTextView);
        Button retakeImageButton = findViewById(R.id.retakeImageButton);
        TextView eligibilityTextView = findViewById(R.id.eligibilityTextView);



        Intent intent = getIntent();
        String originalImageUri = intent.getStringExtra("originalImageUri");
        String contourArea = intent.getStringExtra("contourArea");
        String percentage = intent.getStringExtra("percentage");
        String classification = intent.getStringExtra("classification");
        String eligibilityText = intent.getStringExtra("eligibilityText");



        //contourAreaTextView.setText("Luas kontur: " + contourArea);
        percentageTextView.setText("FISIK UANG : " + percentage + "%");
        classificationTextView.setText("NOMINAL : Rp" + classification);
        eligibilityTextView.setText(eligibilityText);



        if (originalImageUri != null) {
            Uri uri = Uri.parse(originalImageUri);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                // Resize the bitmap to a larger size, e.g., 2x the original size
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2, true);
                originalImageView.setImageBitmap(scaledBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        retakeImageButton.setOnClickListener(v -> {
            finish(); // Close the ResultActivity and return to ClassificationFragment
        });
    }
}



