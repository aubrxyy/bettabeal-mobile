package com.example.bettabeal.model;
import com.google.gson.annotations.SerializedName;

public class MainAddressResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Address data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Address getData() {
        return data;
    }
} 