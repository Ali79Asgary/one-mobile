package com.example.myapplication;

public class Video {
    private String videoLink;
    private String videoName;
    private int imageId;

    public Video(String videoLink, String videoName, int imageId) {
        this.videoLink = videoLink;
        this.videoName = videoName;
        this.imageId = imageId;
    }

    public Video(String videoLink, String videoName) {
        this.videoLink = videoLink;
        this.videoName = videoName;
    }

    public String getVideoLink() { return videoLink; }

    public void setVideoLink(String videoLink) { this.videoLink = videoLink; }

    public String getVideoName() { return videoName; }

    public void setVideoName(String videoName) { this.videoName = videoName; }

    public int getImageId() { return imageId; }

    public void setImageId(int imageId) { this.imageId = imageId; }
}
