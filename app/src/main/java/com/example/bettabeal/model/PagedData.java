package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PagedData {
    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("data")
    private List<Product> data;

    public List<Product> getData() { return data; }
} 