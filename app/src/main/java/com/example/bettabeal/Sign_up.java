package com.example.bettabeal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.bettabeal.model.SignUpRequest;
import com.example.bettabeal.model.SignUpResponse;
import com.example.bettabeal.api.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Sign_up extends AppCompatActivity {

    private EditText etFullname, etUsername, etBirth, etPhone, etEmail, etPassword, etPassConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViews();

        findViewById(R.id.btnget).setOnClickListener(view -> attemptSignUp());
        findViewById(R.id.already).setOnClickListener(view -> 
            startActivity(new Intent(Sign_up.this, Sign_in.class)));
    }

    private void initializeViews() {
        etFullname = findViewById(R.id.fullname);
        etUsername = findViewById(R.id.username);
        etBirth = findViewById(R.id.birth);
        etPhone = findViewById(R.id.phone);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etPassConfirm = findViewById(R.id.konfirm_pass);
    }

    private void attemptSignUp() {
        // Reset errors
        etFullname.setError(null);
        etUsername.setError(null);
        etBirth.setError(null);
        etPhone.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etPassConfirm.setError(null);

        String fullname = etFullname.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String birthDate = etBirth.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etPassConfirm.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Validasi Password Confirmation
        if (TextUtils.isEmpty(confirmPass)) {
            etPassConfirm.setError("Konfirmasi password diperlukan");
            focusView = etPassConfirm;
            cancel = true;
        } else if (!password.equals(confirmPass)) {
            etPassConfirm.setError("Password tidak cocok");
            focusView = etPassConfirm;
            cancel = true;
        }

        // Validasi Password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password diperlukan");
            focusView = etPassword;
            cancel = true;
        } else if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            focusView = etPassword;
            cancel = true;
        }

        // Validasi Email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email diperlukan");
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError("Email tidak valid");
            focusView = etEmail;
            cancel = true;
        }

        // Validasi Phone
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Nomor telepon diperlukan");
            focusView = etPhone;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            etPhone.setError("Nomor telepon tidak valid");
            focusView = etPhone;
            cancel = true;
        }

        // Validasi Birth Date
        if (TextUtils.isEmpty(birthDate)) {
            etBirth.setError("Tanggal lahir diperlukan");
            focusView = etBirth;
            cancel = true;
        } else if (!isBirthDateValid(birthDate)) {
            etBirth.setError("Format tanggal lahir: YYYY-MM-DD");
            focusView = etBirth;
            cancel = true;
        }

        // Validasi Username
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username diperlukan");
            focusView = etUsername;
            cancel = true;
        } else if (username.length() < 3) {
            etUsername.setError("Username minimal 3 karakter");
            focusView = etUsername;
            cancel = true;
        }

        // Validasi Fullname
        if (TextUtils.isEmpty(fullname)) {
            etFullname.setError("Nama lengkap diperlukan");
            focusView = etFullname;
            cancel = true;
        }

        if (cancel) {
            // Ada error, fokus ke field pertama yang error
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // Semua validasi berhasil, lanjut signup
            signUp(fullname, username, birthDate, phone, email, password);
        }
    }

    private boolean isEmailValid(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            return false;
        }
        return true;
    }

    private boolean isPhoneValid(String phone) {
        // Format: +62xxxxxxxxxx atau 08xxxxxxxxxx
        String phonePattern = "^(\\+62|62|0)[8][0-9]{8,11}$";
        if (!phone.matches(phonePattern)) {
            etPhone.setError("Format nomor telepon tidak valid\nContoh: 08xxxxxxxxxx");
            return false;
        }
        return true;
    }

    private boolean isUsernameValid(String username) {
        // Username hanya boleh mengandung huruf, angka, dan underscore
        String usernamePattern = "^[a-zA-Z0-9_]{3,20}$";
        if (!username.matches(usernamePattern)) {
            etUsername.setError("Username hanya boleh mengandung huruf, angka, dan underscore");
            return false;
        }
        return true;
    }

    private boolean isBirthDateValid(String birthDate) {
        // Format YYYY-MM-DD
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!birthDate.matches(datePattern)) {
            etBirth.setError("Format tanggal lahir: YYYY-MM-DD");
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            sdf.setLenient(false);
            sdf.parse(birthDate);
            return true;
        } catch (ParseException e) {
            etBirth.setError("Tanggal tidak valid");
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            return false;
        }
        
        // Tambahkan validasi password yang lebih kuat jika diperlukan
        // Contoh: harus mengandung huruf besar, kecil, dan angka
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        if (!password.matches(passwordPattern)) {
            etPassword.setError("Password harus mengandung minimal:\n" +
                              "- 1 huruf besar\n" +
                              "- 1 huruf kecil\n" +
                              "- 1 angka");
            return false;
        }
        return true;
    }

    private void signUp(String fullname, String username, String birthDate, 
                       String phone, String email, String password) {
        
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Membuat akun...");
        progressDialog.show();
        
        SignUpRequest request = new SignUpRequest(
            fullname, username, birthDate, phone, email, password
        );

        RetrofitClient.getInstance()
            .getApiService()
            .registerCustomer(request)
            .enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    progressDialog.dismiss();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        SignUpResponse signUpResponse = response.body();
                        if ("000".equals(signUpResponse.getCode())) {
                            showToast("Akun berhasil dibuat");
                            startActivity(new Intent(Sign_up.this, Sign_in.class));
                            finish();
                        } else {
                            handleSignUpError(signUpResponse.getMessage());
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            handleSignUpError(errorBody);
                        } catch (Exception e) {
                            showToast("Gagal membuat akun. Coba lagi.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Gagal terhubung ke server. Coba lagi.");
                }
            });
    }

    private void handleSignUpError(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            showToast("Terjadi kesalahan. Silakan coba lagi.");
            return;
        }

        // Convert error message to lowercase for easier checking
        String lowerCaseError = errorMessage.toLowerCase();

        // Handle username errors
        if (lowerCaseError.contains("username has already been taken") || 
            lowerCaseError.contains("username already exists") || 
            lowerCaseError.contains("username telah digunakan")) {
            etUsername.setError("Username telah digunakan");
            etUsername.requestFocus();
            return;
        }

        // Handle email errors
        if (lowerCaseError.contains("email has already been taken") || 
            lowerCaseError.contains("email already exists") || 
            lowerCaseError.contains("email telah terdaftar")) {
            etEmail.setError("Email telah terdaftar");
            etEmail.requestFocus();
            return;
        }

        // Handle phone errors
        if (lowerCaseError.contains("phone number has already been taken") || 
            lowerCaseError.contains("phone already exists") || 
            lowerCaseError.contains("nomor telepon telah digunakan")) {
            etPhone.setError("Nomor telepon telah digunakan");
            etPhone.requestFocus();
            return;
        }

        // Handle validation errors
        if (lowerCaseError.contains("validation")) {
            try {
                JSONObject errorBody = new JSONObject(errorMessage);
                JSONObject errors = errorBody.optJSONObject("errors");
                if (errors != null) {
                    if (errors.has("username")) {
                        etUsername.setError(errors.getJSONArray("username").getString(0));
                        etUsername.requestFocus();
                    }
                    if (errors.has("email")) {
                        etEmail.setError(errors.getJSONArray("email").getString(0));
                        etEmail.requestFocus();
                    }
                    if (errors.has("phone_number")) {
                        etPhone.setError(errors.getJSONArray("phone_number").getString(0));
                        etPhone.requestFocus();
                    }
                    if (errors.has("password")) {
                        etPassword.setError(errors.getJSONArray("password").getString(0));
                        etPassword.requestFocus();
                    }
                    if (errors.has("birth_date")) {
                        etBirth.setError(errors.getJSONArray("birth_date").getString(0));
                        etBirth.requestFocus();
                    }
                    if (errors.has("full_name")) {
                        etFullname.setError(errors.getJSONArray("full_name").getString(0));
                        etFullname.requestFocus();
                    }
                }
            } catch (Exception e) {
                showToast("Format data tidak valid. Silakan periksa kembali.");
            }
            return;
        }

        // If no specific error is caught, show the general error message
        showToast(errorMessage);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
