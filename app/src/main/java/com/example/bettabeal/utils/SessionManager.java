package com.example.bettabeal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "BettaBealSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public void createLoginSession(String token, int userId) {
        saveToken(token);
        saveUserId(userId);
        setLoggedIn(true);
        editor.apply();
    }

    public void saveUserDetails(String fullName, String birthDate, String phoneNumber, String email, String profileImage) {
        editor.putString(KEY_USERNAME, fullName);
        editor.putString(KEY_DATE_OF_BIRTH, birthDate);
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_IMAGE, profileImage);
        editor.apply();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    public String getDateOfBirth() {
        return pref.getString(KEY_DATE_OF_BIRTH, "");
    }

    public String getPhoneNumber() {
        return pref.getString(KEY_PHONE_NUMBER, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getProfileImage() {
        return pref.getString(KEY_PROFILE_IMAGE, "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            // Jika tidak login, bisa redirect ke halaman login
            // Contoh implementasi redirect bisa disesuaikan dengan kebutuhan
        }
    }

    public boolean checkLoginStatus() {
        boolean isLoggedIn = pref.getBoolean(KEY_IS_LOGGED_IN, false);
        String token = getToken();
        int userId = getUserId();
        
        // Check if we have valid session data
        return isLoggedIn && !token.isEmpty() && userId != -1;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
} 