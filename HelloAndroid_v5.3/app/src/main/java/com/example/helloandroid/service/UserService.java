package com.example.helloandroid.service;

import android.content.Context;
import android.content.Intent;

import com.example.helloandroid.activities.NavigationActivity;
import com.example.helloandroid.utils.HttpUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserService {

    private String params;
    private Gson gson;
    private Map<String, String> map;
    private String url;
    private String p_token;

    // 需要使用shareReference保存获取到的token

    public UserService() {
        params = "";
        url = "http://www.huanghecs.win:5000/api/user";
        map = new HashMap<>();
        gson = new Gson();
        p_token = null;
    }

    public void userRequest(String mode, String username, String password, Context context) {
        map.put("action", mode);
        map.put("username", username);
        map.put("password", password);
        params = gson.toJson(map);
//        String returnJson = HttpUtils.getInstance().post(url, params);
//        Map<String, String> map_res = gson.fromJson(returnJson, HttpUtils.mapType);
//        if (map_res.containsKey("errorcode")) {
//            System.out.println("Error: " + map_res.get("msg"));
//            return null;
//        } else {
//            System.out.println(map_res);
//            String token = map_res.get("token");
//            System.out.println("token: " + token);
//            return token;
//        }
        HttpUtils.getInstance().postWithOkhttp(url, params, new Callback() {
            private String token;

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                System.out.println(res);
                Map<String, String> map_res = gson.fromJson(res, HttpUtils.mapType);
                if (map_res.containsKey("errorcode")) {
                    System.out.println("Error: " + map_res.get("msg"));
                } else {
                    System.out.println(map_res);
                    token = map_res.get("token");
                    Intent intent = new Intent(context, NavigationActivity.class);
                    intent.putExtra("token", token);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            }
        });
    }
}
