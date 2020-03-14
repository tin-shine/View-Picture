package com.example.helloandroid.listener;

import com.example.helloandroid.model.Picture;

import java.util.ArrayList;
import java.util.Map;

public interface DeleteCallback {
    void onSuccess(Map<String, Boolean> result);

    void onError(String exception);
}
