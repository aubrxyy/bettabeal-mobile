package com.example.bettabeal.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OrderData data;

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public OrderData getData() {
        return data;
    }

    public static class OrderData {
        @SerializedName("order_id")
        private Long orderId;

        @SerializedName("total_amount")
        private Double totalAmount;

        @SerializedName("snap_token")
        private String snapToken;

        @SerializedName("payment_url")
        private String paymentUrl;

        @SerializedName("status")
        private String status;

        public Long getOrderId() {
            return orderId;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public String getSnapToken() {
            return snapToken;
        }

        public String getPaymentUrl() {
            return paymentUrl;
        }

        public String getStatus() {
            return status;
        }
    }
} 