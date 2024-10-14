package com.devsquad.mind_melody.Model;

public class Audio {
    private String name;
    private String filePath;
    private int imageResId;  // 新增图片资源ID --

    public Audio(String name, String filePath, int imageResId) {
        this.name = name;
        this.filePath = filePath;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
