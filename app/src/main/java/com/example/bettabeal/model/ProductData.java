package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductData {
    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("data")
    private List<Product> data;

    // Getter
    public List<Product> getData() { return data; }
} 