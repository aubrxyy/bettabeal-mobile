package com.example.bettabeal.model;

public class SignUpRequest {
    private String full_name;
    private String username;
    private String birth_date;
    private String phone_number;
    private String email;
    private String password;

    public SignUpRequest(String full_name, String username, String birth_date, 
                        String phone_number, String email, String password) {
        this.full_name = full_name;
        this.username = username;
        this.birth_date = birth_date;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
    }
} 