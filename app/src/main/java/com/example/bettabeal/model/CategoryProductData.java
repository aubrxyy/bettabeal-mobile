package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class CategoryProductData {
    @SerializedName("category")
    private Category category;

    @SerializedName("products")
    private PagedData products;

    public PagedData getProducts() {
        return products;
    }

    public Category getCategory() {
        return category;
    }
} 