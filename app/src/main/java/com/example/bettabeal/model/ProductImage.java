package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class ProductImage {
    @SerializedName("gallery_id")
    private int galleryId;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("is_main")
    private boolean isMain;

    // Getters
    public String getImageUrl() { return imageUrl; }
    public boolean isMain() { return isMain; }
} 