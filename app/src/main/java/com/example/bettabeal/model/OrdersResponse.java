package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrdersResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("orders")
        private List<Order> orders;

        @SerializedName("pagination")
        private Pagination pagination;

        public List<Order> getOrders() {
            return orders;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class Pagination {
        @SerializedName("current_page")
        private int currentPage;

        @SerializedName("last_page")
        private int lastPage;

        @SerializedName("per_page")
        private int perPage;

        @SerializedName("total")
        private int total;

        public int getCurrentPage() { return currentPage; }
        public int getLastPage() { return lastPage; }
        public int getPerPage() { return perPage; }
        public int getTotal() { return total; }
    }

    public static class Order {
        @SerializedName("order_id")
        private long orderId;

        @SerializedName("seller")
        private Seller seller;

        @SerializedName("customer")
        private Customer customer;

        @SerializedName("items")
        private List<OrderItem> items;

        @SerializedName("shipping_address")
        private ShippingAddress shippingAddress;

        @SerializedName("total_amount")
        private String totalAmount;

        @SerializedName("status")
        private String status;

        @SerializedName("shipping_status")
        private String shippingStatus;

        @SerializedName("payment_type")
        private String paymentType;

        @SerializedName("transaction_id")
        private String transactionId;

        @SerializedName("snap_token")
        private String snapToken;

        @SerializedName("payment_url")
        private String paymentUrl;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        // Getters
        public long getOrderId() { return orderId; }
        public Seller getSeller() { return seller; }
        public Customer getCustomer() { return customer; }
        public List<OrderItem> getItems() { return items; }
        public ShippingAddress getShippingAddress() { return shippingAddress; }
        public String getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public String getShippingStatus() { return shippingStatus != null ? shippingStatus : "Pending"; }
        public String getPaymentType() { return paymentType; }
        public String getTransactionId() { return transactionId; }
        public String getSnapToken() { return snapToken; }
        public String getPaymentUrl() { return paymentUrl; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }

    public static class Seller {
        @SerializedName("seller_id")
        private long sellerId;

        @SerializedName("store_name")
        private String storeName;

        @SerializedName("store_rating")
        private String storeRating;

        // Getters
        public long getSellerId() { return sellerId; }
        public String getStoreName() { return storeName; }
        public String getStoreRating() { return storeRating; }
    }

    public static class Customer {
        @SerializedName("user_id")
        private long userId;

        @SerializedName("username")
        private String username;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("email")
        private String email;

        @SerializedName("phone_number")
        private String phoneNumber;

        // Getters
        public long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
    }

    public static class OrderItem {
        @SerializedName("product_id")
        private long productId;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("price")
        private String price;

        @SerializedName("subtotal")
        private String subtotal;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("product_status")
        private String productStatus;

        @SerializedName("is_active")
        private boolean isActive;

        // Getters
        public long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public String getPrice() { return price; }
        public String getSubtotal() { return subtotal; }
        public String getImageUrl() { return imageUrl; }
        public String getProductStatus() { return productStatus; }
        public boolean isActive() { return isActive; }
    }

    public static class ShippingAddress {
        @SerializedName("address")
        private String address;

        @SerializedName("district_id")
        private long districtId;

        @SerializedName("poscode_id")
        private long poscodeId;

        // Getters
        public String getAddress() { return address; }
        public long getDistrictId() { return districtId; }
        public long getPoscodeId() { return poscodeId; }
    }
} 