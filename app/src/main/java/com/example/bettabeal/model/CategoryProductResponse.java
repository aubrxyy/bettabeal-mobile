package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class CategoryProductResponse {
    private String code;
    private String status;
    private DataWrapper data;

    public static class DataWrapper {
        private Category category;
        private ProductsWrapper products;

        public Category getCategory() {
            return category;
        }

        public ProductsWrapper getProducts() {
            return products;
        }
    }

    public static class ProductsWrapper {
        private int current_page;
        private List<Product> data;

        public List<Product> getData() {
            return data;
        }
    }

    public DataWrapper getData() {
        return data;
    }
} 