package com.example.bettabeal.model;

import java.util.List;

public class ProductDetailResponse {
    private String code;
    private String status;
    private ProductDetail data;

    public ProductDetail getData() {
        return data;
    }

    public static class ProductDetail {
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
        private Category category;
        private MainImage main_image;
        private List<ProductImage> additional_images;

        // Getters
        public long getProduct_id() { return product_id; }
        public String getProduct_name() { return product_name; }
        public String getDescription() { return description; }
        public float getPrice() { return price; }
        public int getStock_quantity() { return stock_quantity; }
        public Category getCategory() { return category; }
        public MainImage getMain_image() { return main_image; }
        public List<ProductImage> getAdditional_images() { return additional_images; }
    }

    public static class Category {
        private int category_id;
        private String category_name;
        private String description;
        private String icon;
        private int order;
        private boolean is_active;

        public String getCategory_name() { return category_name; }
    }

    public static class MainImage {
        private int gallery_id;
        private long product_id;
        private long seller_id;
        private String image_url;
        private String uploaded_at;
        private boolean is_main;

        public String getImage_url() { return image_url; }
    }

    public static class ProductImage {
        private int gallery_id;
        private long product_id;
        private long seller_id;
        private String image_url;
        private String uploaded_at;
        private boolean is_main;

        public String getImage_url() { return image_url; }
    }
} 