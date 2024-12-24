package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class ProductResponse {
    private String code;
    private String status;
    private DataWrapper data;

    public static class DataWrapper {
        private int current_page;
        private List<Product> data;

        public List<Product> getData() {
            return data;
        }
    }

    public DataWrapper getData() {
        return data;
    }
} 