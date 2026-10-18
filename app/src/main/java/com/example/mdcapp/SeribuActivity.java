package com.example.mdcapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SeribuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seribu_layout);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menutup Activity saat tombol kembali ditekan
                onBackPressed();
            }
        });

        // Reference UI elements
        TextView txtName = findViewById(R.id.spec_name);
        TextView txtCurrency = findViewById(R.id.spec_currency);
        TextView txtSeries = findViewById(R.id.spec_series);
        TextView txtDenomination = findViewById(R.id.spec_denomination);
        TextView txtIssueDate = findViewById(R.id.spec_issue_date);
        TextView txtSignatory = findViewById(R.id.spec_signatory);
        TextView txtWatermark = findViewById(R.id.spec_watermark);
        TextView txtMaterial = findViewById(R.id.spec_material);
        TextView txtSize = findViewById(R.id.spec_size);

        // Set images and texts directly
        txtName.setText(": Uang Rp 1.000");
        txtCurrency.setText(": Rupiah");
        txtSeries.setText(": 2016");
        txtDenomination.setText(": Rp1.000");
        txtIssueDate.setText(": 19 Desember 2016");
        txtSignatory.setText(": Perry Warjiyo,Sri Mulyani Indrawati");
        txtWatermark.setText(": Tjut Meutia");
        txtMaterial.setText(": Serat Kapas");
        txtSize.setText(": 141 x 65 mm");
    }
}