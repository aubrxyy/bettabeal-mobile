package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

public class AddressRequest {
    @SerializedName("name")
    private String name;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("district_id")
    private int district_id;
    
    @SerializedName("poscode_id")
    private int poscode_id;
    
    @SerializedName("phone_number")
    private String phone_number;
    
    @SerializedName("is_main")
    private int is_main;

    public AddressRequest(String name, String address, int district_id, 
                         int poscode_id, String phone_number, int is_main) {
        this.name = name;
        this.address = address;
        this.district_id = district_id;
        this.poscode_id = poscode_id;
        this.phone_number = phone_number;
        this.is_main = is_main;
    }

    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getDistrictId() { return district_id; }
    public int getPoscodeId() { return poscode_id; }
    public String getPhoneNumber() { return phone_number; }
    public int getIsMain() { return is_main; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setDistrictId(int district_id) { this.district_id = district_id; }
    public void setPoscodeId(int poscode_id) { this.poscode_id = poscode_id; }
    public void setPhoneNumber(String phone_number) { this.phone_number = phone_number; }
    public void setIsMain(int is_main) { this.is_main = is_main; }
} 