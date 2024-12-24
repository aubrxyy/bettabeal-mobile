package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("address_id")
    private Long addressId;
    
    @SerializedName("user_id")
    private Long userId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("district_id")
    private Integer districtId;
    
    @SerializedName("poscode_id")
    private Integer poscodeId;
    
    @SerializedName("phone_number")
    private String phoneNumber;
    
    @SerializedName("is_main")
    private Boolean isMain;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("biteship_id")
    private String biteshipId;
    
    @SerializedName("district_name")
    private String districtName;
    
    @SerializedName("poscode")
    private String posCode;

    // Getters
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getUserId() {
        return userId != null ? userId : 0L;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public Integer getDistrictId() {
        return districtId != null ? districtId : 0;
    }

    public Integer getPoscodeId() {
        return poscodeId != null ? poscodeId : 0;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : "";
    }

    public boolean isMain() {
        return isMain != null && isMain;
    }

    public void setMain(boolean main) {
        this.isMain = main;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt != null ? updatedAt : "";
    }

    public String getBiteshipId() {
        return biteshipId != null ? biteshipId : "";
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }
} 