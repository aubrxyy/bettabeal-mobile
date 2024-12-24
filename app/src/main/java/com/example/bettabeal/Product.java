package com.example.bettabeal;

public class Product {
    private int productId;
    private String productName;
    private int price;
    private String imageUrl;

    public Product(int productId, String productName, int price, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
} 