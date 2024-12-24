package com.example.bettabeal.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class District {
    @SerializedName("district_id")
    private int district_id;
    
    @SerializedName("district_name")
    private String district_name;

    public int getDistrictId() {
        return district_id;
    }

    public String getDistrictName() {
        return district_name;
    }

    @Override
    public String toString() {
        return district_name != null ? district_name : "";
    }
} 