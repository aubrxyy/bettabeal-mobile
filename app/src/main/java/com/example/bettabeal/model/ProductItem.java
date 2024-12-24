package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class ProductItem {
    @SerializedName("product_id")
    private int productId;
    
    @SerializedName("product_name")
    private String productName;
    
    @SerializedName("price")
    private int price;
    
    @SerializedName("main_image")
    private MainImage mainImage;

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getPrice() { return price; }
    public MainImage getMainImage() { return mainImage; }
} 