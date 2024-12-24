package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {
    private long product_id;
    private long seller_id;
    private int category_id;
    private String product_name;
    private String description;
    private float price;
    private int stock_quantity;
    private String created_at;
    private String updated_at;
    private boolean is_active;
    private String deleted_at;
    private MainImage main_image;
    private List<ProductImage> images;
    private Category category;
    private double average_rating;
    private int total_sales;

    // Inner classes for nested objects
    public static class MainImage {
        private int gallery_id;
        private String image_url;
        
        public String getImage_url() {
            return image_url;
        }
    }

    public static class ProductImage {
        private int gallery_id;
        private String image_url;
        private boolean is_main;
        
        public String getImage_url() {
            return image_url;
        }
    }

    // Getters and setters
    public long getProduct_id() { return product_id; }
    public long getSeller_id() { return seller_id; }
    public int getCategory_id() { return category_id; }
    public String getProduct_name() { return product_name; }
    public String getDescription() { return description; }
    public float getPrice() { return price; }
    public int getStock_quantity() { return stock_quantity; }
    public boolean isIs_active() { return is_active; }
    public MainImage getMain_image() { return main_image; }
    public List<ProductImage> getImages() { return images; }
    public Category getCategory() { return category; }
    public double getAverage_rating() { return average_rating; }
    public int getTotal_sales() { return total_sales; }
} 