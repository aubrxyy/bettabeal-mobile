package com.example.bettabeal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class Sign_up extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etPassConfirm, etPhone, etBirthDate;
    private Button btnSignUp, btnAlready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Inisialisasi view
        etUsername = findViewById(R.id.username);   // Field Username
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etPassConfirm = findViewById(R.id.konfirm_pass);
        etPhone = findViewById(R.id.phone);
        etBirthDate = findViewById(R.id.birth);
        btnSignUp = findViewById(R.id.btnget);
        btnAlready = findViewById(R.id.already);

        // Listener tombol sign up
        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPass = etPassConfirm.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();

            if (validateInputs(username, email, password, confirmPass, phone, birthDate)) {
                signUp(username, email, password, phone, birthDate);
            }
        });

        // Listener tombol "Already have an account?"
        btnAlready.setOnClickListener(v -> startActivity(new Intent(Sign_up.this, Sign_in.class)));
    }

    // Validasi input
    private boolean validateInputs(String username, String email, String password, String confirmPass, String phone, String birthDate) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(birthDate)) {
            showToast("Mohon isi semua field");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email tidak valid");
            return false;
        }

        if (!password.equals(confirmPass)) {
            showToast("Konfirmasi password harus sama dengan password");
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            showToast("Nomor telepon tidak valid");
            return false;
        }

        return true;
    }

    // Fungsi mendaftar dengan Retrofit
    private void signUp(String username, String email, String password, String phone, String birthDate) {
        // Gunakan username juga sebagai full_name
        getApiService().signUp(new SignUp(username, birthDate, phone, email, password, username))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            showToast("Akun berhasil dibuat");
                        } else if (response.code() == 409) {
                            showToast("Email telah terdaftar");
                        } else {
                            showToast("Gagal membuat akun. Coba lagi.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showToast("Error: " + t.getMessage());
                    }
                });
    }

    // Tampilkan toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Retrofit API Service
    private ApiService getApiService() {
        return new Retrofit.Builder()
                .baseUrl("https://api-bettabeal.dgeo.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    // Model SignUp request
    private static class SignUp {
        final String full_name;
        final String birth_date;
        final String phone_number;
        final String email;
        final String password;
        final String username;  // Ditambahkan untuk menyimpan username

        SignUp(String full_name, String birth_date, String phone_number, String email, String password, String username) {
            this.full_name = full_name;
            this.birth_date = birth_date;
            this.phone_number = phone_number;
            this.email = email;
            this.password = password;
            this.username = username;  // Gunakan username juga sebagai username
        }
    }

    // API Interface
    interface ApiService {
        @POST("register/customer")
        Call<Void> signUp(@Body SignUp request);
    }
}
