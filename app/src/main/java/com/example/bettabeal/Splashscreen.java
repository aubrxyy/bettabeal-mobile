package com.example.bettabeal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splashscreen extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // Durasi splashscreen dalam milidetik (2500ms = 2.5 detik)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splashscreen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Menampilkan splashscreen selama SPLASH_DURATION
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splashscreen.this, Sign_in.class); // Pindah ke Sign_in activity
            startActivity(intent);
            finish(); // Tutup Splashscreen activity
        }, SPLASH_DURATION);
    }
}
