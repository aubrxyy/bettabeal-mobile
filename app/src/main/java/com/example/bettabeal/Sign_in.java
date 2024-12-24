package com.example.bettabeal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bettabeal.api.RetrofitClient;
import com.example.bettabeal.model.LoginRequest;
import com.example.bettabeal.model.LoginResponse;
import com.example.bettabeal.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class Sign_in extends AppCompatActivity {

    private EditText usernameet, passwordEditText;
    private CheckBox checkBoxShowPassword;
    private SessionManager sessionManager;

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_TOKEN = "userToken";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.checkLoginStatus()) {
            navigateToHome();
            return;
        }

        setContentView(R.layout.activity_sign_in);
        initializeUI();
    }

    private void initializeUI() {
        usernameet = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        checkBoxShowPassword = findViewById(R.id.checkpass);
        Button signInButton = findViewById(R.id.sign_in_button);
        Button signUpButton = findViewById(R.id.btnsignup);

        checkBoxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        signInButton.setOnClickListener(view -> {
            String username = usernameet.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (validateInput(username, password)) loginUser(username, password);
        });

        signUpButton.setOnClickListener(view -> startActivity(new Intent(Sign_in.this, Sign_up.class)));
    }

    private boolean validateInput(String username, String password) {
        if (TextUtils.isEmpty(username) || !isValidUsername(username)) {
            usernameet.setError("Invalid username. Must be email, text only, or text and numbers.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }
        return true;
    }

    private boolean isValidUsername(String username) {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches() || username.matches("^[a-zA-Z0-9]+$");
    }

    private void loginUser(String username, String password) {
        // Show loading indicator
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        LoginRequest loginRequest = new LoginRequest(username, password);

        RetrofitClient.getInstance()
            .getApiService()
            .login(loginRequest)
            .enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    progressDialog.dismiss();
                    
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        if ("000".equals(loginResponse.getCode())) {
                            saveLoginData(
                                loginResponse.getToken(),
                                loginResponse.getUser().getUserId()
                            );
                            navigateToHome();
                        } else {
                            showToast(loginResponse.getMessage());
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            showToast("Login Failed: " + errorBody);
                        } catch (Exception e) {
                            showToast("Login Failed");
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    showToast("Error: Unable to connect to server");
                }
            });
    }

    private void saveLoginData(String token, int userId) {
        // Simpan token dengan format Bearer jika belum ada
        String tokenToSave = token.startsWith("Bearer ") ? token : "Bearer " + token;
        sessionManager.setLoggedIn(true);
        sessionManager.saveToken(tokenToSave);
        sessionManager.saveUserId(userId);
        // Use SessionManager methods instead of SharedPreferences
        sessionManager.createLoginSession(tokenToSave, userId);
    }

    private void navigateToHome() {
        Intent intent = new Intent(Sign_in.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleLoginResponse(Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            LoginResponse loginResponse = response.body();
            if ("000".equals(loginResponse.getCode())) {
                // Ambil token dan user langsung dari response
                String token = loginResponse.getToken();
                LoginResponse.User user = loginResponse.getUser();

                if (user != null) {
                    // Simpan ke session manager
                    sessionManager.saveToken(token);
                    sessionManager.saveUserId(user.getUserId());
                    sessionManager.setLoggedIn(true);

                    // Opsional: Simpan username jika diperlukan
                    sessionManager.saveUserDetails(
                        user.getUsername(),
                        "",  // birth_date tidak ada dalam response
                        "",  // phone_number tidak ada dalam response
                        "",  // email tidak ada dalam response
                        ""   // profile_image tidak ada dalam response
                    );

                    // Redirect ke halaman utama
                    Intent intent = new Intent(Sign_in.this, Home.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Sign_in.this, 
                        "Login failed: User data is missing", 
                        Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Sign_in.this, 
                    "Login failed: " + loginResponse.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                String errorBody = response.errorBody() != null ? 
                    response.errorBody().string() : "Unknown error";
                Toast.makeText(Sign_in.this, 
                    "Login failed: " + errorBody, 
                    Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Sign_in.this, 
                    "Login failed: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }

         }
    }
}
