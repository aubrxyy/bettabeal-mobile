package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class AddressResponse {
    @SerializedName("code")
    private String code;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Address data;

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Address getData() {
        return data;
    }

    public boolean isSuccess() {
        return "000".equals(code) && "success".equals(status);
    }
} 