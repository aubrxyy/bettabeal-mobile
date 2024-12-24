package com.example.bettabeal.model;

public class SignUpResponse {
    private String code;
    private String message;
    private Data data;

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        // Sesuaikan dengan response API
        private String token;
        // tambahkan field lain sesuai response
    }
} 