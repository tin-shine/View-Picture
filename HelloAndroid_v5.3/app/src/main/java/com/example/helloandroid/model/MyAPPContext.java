package com.example.helloandroid.model;

import android.app.Application;
import android.content.Context;

public class MyAPPContext extends Application {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
