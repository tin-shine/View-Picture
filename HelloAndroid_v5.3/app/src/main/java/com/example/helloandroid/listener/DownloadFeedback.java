package com.example.helloandroid.listener;

import com.example.helloandroid.model.Picture;

import java.util.ArrayList;

public interface DownloadFeedback {

    void onSuccess(ArrayList<Picture> PictureList);

    void onError(String exception);
}
