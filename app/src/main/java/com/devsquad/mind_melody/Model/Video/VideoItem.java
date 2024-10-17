package com.devsquad.mind_melody.Model.Video;

public class VideoItem {
    private String title;
    private int thumbnailResId;
    private int videoResId;

    public VideoItem(String title, int thumbnailResId, int videoResId) {
        this.title = title;
        this.thumbnailResId = thumbnailResId;
        this.videoResId = videoResId;
    }

    public String getTitle() {
        return title;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public int getVideoResId() {
        return videoResId;
    }
}

