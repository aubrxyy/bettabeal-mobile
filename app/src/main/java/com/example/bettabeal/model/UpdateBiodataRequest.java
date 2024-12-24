package com.example.bettabeal.model;

public class UpdateBiodataRequest {
    private String full_name;
    private String birth_date;
    private String phone_number;
    private String email;

    public UpdateBiodataRequest(String full_name, String birth_date, 
                              String phone_number, String email) {
        this.full_name = full_name;
        this.birth_date = birth_date;
        this.phone_number = phone_number;
        this.email = email;
    }
} 