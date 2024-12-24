package com.example.bettabeal.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    private int articleId;
    private String title;
    private String content;
    private String image;
    private String createdAt;

    public Article(int articleId, String title, String content, String image, String createdAt) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.createdAt = createdAt;
    }

    protected Article(Parcel in) {
        articleId = in.readInt();
        title = in.readString();
        content = in.readString();
        image = in.readString();
        createdAt = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public int getArticleId() { return articleId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImage() { return image; }
    public String getCreatedAt() { return createdAt; }

    public void setArticleId(int articleId) { this.articleId = articleId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setImage(String image) { this.image = image; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(articleId);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(image);
        dest.writeString(createdAt);
    }
} 