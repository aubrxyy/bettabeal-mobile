package com.example.bettabeal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class Sign_in extends AppCompatActivity {

    private EditText usernameet, passwordEditText;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Inisialisasi input form
        usernameet = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);

        // Event listener untuk tombol Sign in
        signInButton.setOnClickListener(view -> {
            String username = usernameet.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validasi input
            if (!validateInput(username, password)) return;

            // Lakukan login dengan memanggil API
            loginUser(username, password);
        });
    }

    // Validasi input
    private boolean validateInput(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            usernameet.setError("Email or Username is required");
            return false;
        }

        if (!isValidUsername(username)) {
            usernameet.setError("Invalid username. Must be an email, text only, or a combination of text and numbers.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }

        return true;
    }

    // Validasi format username (email, teks, atau kombinasi teks dan angka)
    private boolean isValidUsername(String username) {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches() || username.matches("^[a-zA-Z0-9]+$");
    }

    // Retrofit instance dan service
    private ApiService getApiService() {
        return new Retrofit.Builder()
                .baseUrl("https://api-bettabeal.dgeo.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    // Lakukan login dengan Retrofit
    private void loginUser(String username, String password) {
        getApiService().loginUser(new LoginRequest(username, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleLoginResponse(response.body());
                } else {
                    showToast("Error: Something went wrong, please try again later.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showToast("Error: Unable to connect");
            }
        });
    }

    // Tangani respons login
    private void handleLoginResponse(LoginResponse loginResponse) {
        switch (loginResponse.getCode()) {
            case "000":
                showToast("Login Berhasil");
                startActivity(new Intent(Sign_in.this, Home.class));
                finish(); // Selesaikan aktivitas saat ini
                break;
            case "001":
                showToast("Username or email incorrect");
                break;
            case "002":
                showToast("Password incorrect");
                break;
            default:
                showToast("Login Gagal: " + loginResponse.getMessage());
        }
    }

    // Helper untuk menampilkan pesan
    private void showToast(String message) {
        Toast.makeText(Sign_in.this, message, Toast.LENGTH_SHORT).show();
    }

    // Model request login
    private static class LoginRequest {
        private final String username;
        private final String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // Model response login
    private static class LoginResponse {
        private String code;    // e.g., "000"
        private String message; // e.g., "Login successful!"
        private String token;
        private User user;      // User object

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

    // Model User
    private static class User {
        private int user_id;
        private String username;
        private String role;
        private String status;

        public int getUserId() {
            return user_id;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }

        public String getStatus() {
            return status;
        }
    }

    // Interface API untuk Retrofit
    private interface ApiService {
        @POST("login")
        Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    }
}