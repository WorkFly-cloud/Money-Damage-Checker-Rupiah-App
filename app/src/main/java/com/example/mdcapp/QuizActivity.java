package com.example.mdcapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private RadioGroup optionsGroup;
    private Button nextButton;
    private TextView questionText;
    private RadioButton optionA, optionB, optionC, optionD;

    private int score = 0;
    private int currentQuestionIndex = 0;
    private final int totalQuestions = 10;

    // Pertanyaan
    private final String[] questions = {
            "Mengapa penting untuk menyimpan uang kertas di tempat yang kering dan bersih?",
            "Apa yang harus dilakukan jika menemukan uang kertas yang sobek?",
            "Mengapa uang kertas tidak boleh dilipat atau dirusak?",
            "Apa yang harus dihindari saat menyimpan uang kertas di dompet?",
            "Apa yang bisa terjadi jika uang kertas terpapar cairan?",
            "Bagaimana cara yang benar untuk menyimpan uang kertas di dalam lemari?",
            "Apa yang sebaiknya dilakukan jika uang kertas mengalami kerusakan parah?",
            "Mengapa penting untuk tidak menulis atau mencoret uang kertas?",
            "Apa yang harus dilakukan jika uang kertas terkena noda yang sulit dihilangkan?",
            "Bagaimana cara yang tepat untuk menghindari kerusakan pada uang kertas saat menyimpannya di dompet?"
    };

    // Pilihan jawaban untuk setiap pertanyaan
    private final String[][] options = {
            {"A. Agar tidak lembab dan menghindari kerusakan", "B. Agar warna tetap cerah", "C. Agar lebih awet", "D. Semua benar"},
            {"A. Disimpan dan digunakan seperti biasa", "B. Dilipat agar tidak terlihat sobek", "C. Ditukarkan di bank", "D. Direkatkan dengan selotip"},
            {"A. Untuk menjaga nilai estetika dan keutuhan uang", "B. Agar lebih mudah disimpan", "C. Agar tidak tercecer", "D. Semua benar"},
            {"A. Uang kertas yang terlipat dapat mudah sobek", "B. Dimasukkan ke dalam saku yang sempit", "C. Dicampur dengan barang tajam", "D. Ditaruh di tempat yang terendam air"},
            {"A. Uang akan menjadi lebih kuat", "B. Uang akan rusak dan sulit digunakan", "C. Uang menjadi lebih halus", "D. Tidak ada pengaruhnya"},
            {"A. Dilipat dalam buku", "B. Disimpan rata di tempat yang kering", "C. Digulung kecil", "D. Diminyaki agar awet"},
            {"A. Tukarkan dengan uang baru", "B. Diperbaiki sendiri dengan lem", "C. Disimpan sebagai kenang-kenangan", "D. Dijual sebagai uang antik"},
            {"A. Karena menulis di uang kertas bisa merusak nilai asli uang", "B. Agar uang tetap rapi", "C. Agar uang lebih bersih", "D. Agar lebih menarik"},
            {"A. Cuci dengan sabun dan air", "B. Biarkan saja dan tetap digunakan", "C. Tukarkan di bank", "D. Dibersihkan dengan penghapus"},
            {"A. Hindari menyimpan uang di tempat yang basah", "B. Menjaga uang tetap rata", "C. Melipat uang dengan hati-hati", "D. Semua benar"}
    };

    // Jawaban yang benar untuk setiap pertanyaan
    private final int[] correctAnswers = {
            R.id.option_a, // Jawaban benar untuk pertanyaan 1
            R.id.option_c, // Jawaban benar untuk pertanyaan 2
            R.id.option_a, // Jawaban benar untuk pertanyaan 3
            R.id.option_a, // Jawaban benar untuk pertanyaan 4
            R.id.option_b, // Jawaban benar untuk pertanyaan 5
            R.id.option_b, // Jawaban benar untuk pertanyaan 6
            R.id.option_a, // Jawaban benar untuk pertanyaan 7
            R.id.option_a, // Jawaban benar untuk pertanyaan 8
            R.id.option_c, // Jawaban benar untuk pertanyaan 9
            R.id.option_b  // Jawaban benar untuk pertanyaan 10
    };

    // List untuk menyimpan urutan acak pertanyaan
    private List<Integer> questionOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        optionsGroup = findViewById(R.id.options_group);
        nextButton = findViewById(R.id.next_button);
        questionText = findViewById(R.id.question_text);
        optionA = findViewById(R.id.option_a);
        optionB = findViewById(R.id.option_b);
        optionC = findViewById(R.id.option_c);
        optionD = findViewById(R.id.option_d);

        // Membuat urutan pertanyaan acak
        questionOrder = new ArrayList<>();
        for (int i = 0; i < totalQuestions; i++) {
            questionOrder.add(i);
        }
        Collections.shuffle(questionOrder);  // Mengacak urutan pertanyaan

        loadQuestion();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                currentQuestionIndex++;
                if (currentQuestionIndex < totalQuestions) {
                    loadQuestion();
                } else {
                    showResults();
                }
            }
        });

        ImageButton backButton = findViewById(R.id.btn_back);
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isOptionSelected = checkedId != -1;
            nextButton.setEnabled(isOptionSelected);
        });

        backButton.setOnClickListener(view -> showExitConfirmationDialog());
    }

    private void loadQuestion() {
        // Menggunakan urutan acak untuk menampilkan pertanyaan
        int questionIndex = questionOrder.get(currentQuestionIndex);
        questionText.setText(questions[questionIndex]);
        optionA.setText(options[questionIndex][0]);
        optionB.setText(options[questionIndex][1]);
        optionC.setText(options[questionIndex][2]);
        optionD.setText(options[questionIndex][3]);
        optionsGroup.clearCheck();
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        int questionIndex = questionOrder.get(currentQuestionIndex);
        if (selectedId == correctAnswers[questionIndex]) {
            score++;
        }
    }

    private void showResults() {
        double percentage = (score / (double) totalQuestions) * 100;
        new AlertDialog.Builder(this)
                .setTitle("Quiz Results")
                .setMessage("Your score: " + score + "/" + totalQuestions + "\nPercentage: " + percentage + "%")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar dari kuis?")
                .setPositiveButton("Ya", (dialog, which) -> finish())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
