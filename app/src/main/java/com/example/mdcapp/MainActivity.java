package com.example.mdcapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.action_info_money:
                    selectedFragment = new InfoMoneyFragment();
                    break;
                case R.id.action_classification:
                    // Buka ClassificationActivity alih-alih menggunakan fragment
                    Intent ClassificationIntent = new Intent(MainActivity.this, ClassificationActivity.class);
                    startActivity(ClassificationIntent);
                    return true; // Mengembalikan true tanpa mengganti fragment
                case R.id.action_exchange_info:
                    selectedFragment = new ExchangeInfoFragment();
                    break;
            }

            if (selectedFragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();
            }
            return true;
        });

        // Set default selection saat pertama kali dibuka
        bottomNavigationView.setSelectedItemId(R.id.action_info_money);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set kembali ke fragment default saat kembali ke MainActivity
        bottomNavigationView.setSelectedItemId(R.id.action_info_money);
    }
}
