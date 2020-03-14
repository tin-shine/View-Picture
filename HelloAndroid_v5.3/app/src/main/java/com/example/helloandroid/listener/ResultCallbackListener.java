package com.example.helloandroid.listener;

import java.util.Map;

public interface ResultCallbackListener {

    void onSuccess(Map<String, Boolean> result);

    void onError(String exception);
}
