package com.example.bettabeal.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Ganti dengan base URL API Anda
    private static final String BASE_URL = "https://api.bettabeal.my.id/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Membuat HTTP Logger
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Konfigurasi OkHttpClient
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging);

            // Konfigurasi Gson
            Gson gson = new GsonBuilder()
                .setLenient()
                .create();

            // Buat Retrofit instance
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        }
        return retrofit;
    }

    // Method untuk reset instance (berguna untuk testing atau mengubah base URL)
    public static void resetInstance() {
        retrofit = null;
    }
} 