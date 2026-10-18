package com.example.mdcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class CintaRupiahActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinta);  // Mengatur layout activity

        // Inisialisasi ImageButton
        ImageButton btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCintaActivity();
            }
        });
    }

    // Fungsi untuk membuka activity lain
    private void openCintaActivity() {
        Intent intent = new Intent(CintaRupiahActivity.this, BanggaRupiahActivity.class);
        startActivity(intent);
    }
}
