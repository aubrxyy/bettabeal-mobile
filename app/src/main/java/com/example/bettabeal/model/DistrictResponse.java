package com.example.bettabeal.model;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class DistrictResponse {
    @SerializedName("code")
    private String code;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("data")
    private List<District> data;

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public List<District> getData() {
        return data != null ? data : new ArrayList<>();
    }

    public boolean isSuccess() {
        return "000".equals(code) && "success".equals(status);
    }
} 