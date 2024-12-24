package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class Poscode {
    @SerializedName("poscode_id")
    private int poscode_id;
    
    @SerializedName("code")
    private String code;

    public int getPoscodeId() {
        return poscode_id;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
} 