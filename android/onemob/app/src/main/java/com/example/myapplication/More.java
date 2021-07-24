package com.example.myapplication;

public class More {

    private String moreName;
    private int imageId;

    public More(String moreName, int imageId) {
        this.moreName = moreName;
        this.imageId = imageId;
    }

    public String getMoreName() {
        return moreName;
    }

    public void setMoreName(String moreName) {
        this.moreName = moreName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
