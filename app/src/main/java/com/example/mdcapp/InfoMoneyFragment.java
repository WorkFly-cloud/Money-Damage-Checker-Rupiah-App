package com.example.mdcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InfoMoneyFragment extends Fragment {

    public InfoMoneyFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_money, container, false);

        // tombol 100000
        ImageButton button100000 = view.findViewById(R.id.button_100000);
        button100000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openMoneySeratusActivity();
            }
        });

        // tombol 50000
        ImageButton button50000 = view.findViewById(R.id.button_50000);
        button50000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoneyLimpuActivity();
            }
        });

        // tombol 20000
        ImageButton button20000 = view.findViewById(R.id.button_20000);
        button20000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoneyDupuActivity();
            }
        });

        // tombol 10000
        ImageButton button10000 = view.findViewById(R.id.button_10000);
        button10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoneySepuActivity();
            }
        });

        // tombol 5000
        ImageButton button5000 = view.findViewById(R.id.button_5000);
        button5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoneyLimaActivity();
            }
        });

        // tombol 2000
        ImageButton button2000 = view.findViewById(R.id.button_2000);
        button2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoneyDuaActivity();
            }
        });

        // tombol 1000
        ImageButton button1000 = view.findViewById(R.id.button_1000);
        button1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoneySeribuActivity();
            }
        });


        // Find the "Uji Kemampuanmu" button and set click listener
        ImageButton button_quiz = view.findViewById(R.id.button_quiz);
        button_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuizActivity();
            }
        });

        // CBP
        ImageButton button_kenali_cbp = view.findViewById(R.id.button_kenali_cbp);
        button_kenali_cbp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCintaActivity();
            }
        });

        return view;
    }

    private void openMoneySeratusActivity() {
        // Create an Intent to start MoneyProfileActivity
        Intent intent = new Intent(getActivity(), SeratusActivity.class);
        startActivity(intent);
    }

    private void openMoneyLimpuActivity() {
        // Create an Intent to start MoneyProfileActivity
        Intent intent = new Intent(getActivity(), LimpuActivity.class);
        startActivity(intent);
    }

    private void openMoneyDupuActivity() {
        // Create an Intent to start MoneyProfileActivity
        Intent intent = new Intent(getActivity(), DupuActivity.class);
        startActivity(intent);
    }

    private void openMoneySepuActivity() {
        // Create an Intent to start MoneyProfileActivity
        Intent intent = new Intent(getActivity(), SepuActivity.class);
        startActivity(intent);
    }

    private void openMoneyLimaActivity() {
        // Create an Intent to start MoneyProfileActivity
        Intent intent = new Intent(getActivity(), LimaActivity.class);
        startActivity(intent);
    }

    private void openMoneyDuaActivity() {
        // Create an Intent to start MoneyProfileActivity
        Intent intent = new Intent(getActivity(), DuaActivity.class);
        startActivity(intent);
    }

    private void openMoneySeribuActivity() {
        // Create an Intent to start MoneyProfileActivity
        Intent intent = new Intent(getActivity(), SeribuActivity.class);
        startActivity(intent);
    }


    // Function to open QuizActivity
    private void openQuizActivity() {
        Intent intent = new Intent(getActivity(), QuizActivity.class);
        startActivity(intent);
    }

    private void openCintaActivity() {
        Intent intent = new Intent(getActivity(), CintaRupiahActivity.class);
        startActivity(intent);
    }
}
