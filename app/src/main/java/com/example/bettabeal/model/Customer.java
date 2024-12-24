package com.example.bettabeal.model;

public class Customer {
    private int customer_id;
    private String full_name;
    private String email;
    private String phone_number;
    private String birth_date;
    private String profile_image;
    private String address;
    private String gender;

    public int getId() {
        return customer_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }
} 