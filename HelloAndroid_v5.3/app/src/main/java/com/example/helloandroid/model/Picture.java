package com.example.helloandroid.model;

import android.graphics.Bitmap;

public class Picture {
    private String name;
    private Bitmap bitmap;
    private String path;
    private String md5;
    private String url;
    private Boolean isChecked;
    public Boolean islocal;
    public Boolean isnet;

    public Picture(String name, String path) {
        this.name = name;
        this.path = path;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
