package com.example.backgroundimage.models.favorite;

public class Favorite {
    int id;
    String title;
    String imageUrl;
    String postCategory;

    boolean isNeedPoint;

    public Favorite(int id, String title, String imageUrl, String postCategory) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.postCategory = postCategory;
        this.isNeedPoint = isNeedPoint;
    }
    public Favorite(int id, String title, String imageUrl, String postCategory, boolean isNeedPoint) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.postCategory = postCategory;
        this.isNeedPoint = isNeedPoint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostCategory() {
        return postCategory;
    }


    public void setNeedPont(boolean needPoint) {
        isNeedPoint = needPoint;
    }

    public boolean isNeedPont() {
        return isNeedPoint;
    }
}