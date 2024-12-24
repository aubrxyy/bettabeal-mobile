package com.example.bettabeal.model;

import java.util.List;

public class CartResponse {
    private String status;
    private CartData data;
    private String message;

    public static class CartData {
        private int total_items;
        private double subtotal;
        private List<CartItem> items;
        private List<Object> removed_items;

        public int getTotal_items() {
            return total_items;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public List<CartItem> getItems() {
            return items;
        }
    }

    public static class CartItem {
        private int cart_item_id;
        private Product product;
        private int quantity;
        private double total_price;

        public int getCart_item_id() {
            return cart_item_id;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotal_price() {
            return total_price;
        }
    }

    public static class Product {
        private int product_id;
        private String name;
        private String price;
        private String image_url;

        public int getProduct_id() {
            return product_id;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public String getImage_url() {
            return image_url;
        }
    }

    public String getStatus() {
        return status;
    }

    public CartData getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
} 