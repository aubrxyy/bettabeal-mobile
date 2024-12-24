package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Category> data;

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public List<Category> getData() {
        return data;
    }
} 
