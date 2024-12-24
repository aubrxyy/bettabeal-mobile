package com.example.bettabeal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArticleResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("articles")
    private List<ArticleItem> articles;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<ArticleItem> getArticles() { return articles; }

    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setArticles(List<ArticleItem> articles) { this.articles = articles; }
} 