package com.example.bettabeal.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private ApiService apiService;
    private static final String BASE_URL = "https://api-bettabeal.dgeo.id/";

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();

        Gson gson = new GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Boolean.class, new JsonDeserializer<Boolean>() {
                @Override
                public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                    if (json.isJsonNull()) return false;
                    try {
                        int value = json.getAsInt();
                        return value == 1;
                    } catch (Exception e) {
                        return json.getAsBoolean();
                    }
                }
            })
            .create();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
} 