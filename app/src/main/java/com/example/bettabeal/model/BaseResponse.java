package com.example.bettabeal.model;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;
    private String code;

    private Object data; // bisa disesuaikan dengan response API

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
    public boolean isSuccess() {
        return success;
    }
} 