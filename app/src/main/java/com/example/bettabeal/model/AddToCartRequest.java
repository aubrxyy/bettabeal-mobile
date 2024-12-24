package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class AddToCartRequest {
    @SerializedName("product_id")
    private long productId;
    
    @SerializedName("quantity")
    private int quantity;

    public AddToCartRequest(long productId) {
        this.productId = productId;
        this.quantity = 1; // Default quantity adalah 1
    }

    public long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
} 