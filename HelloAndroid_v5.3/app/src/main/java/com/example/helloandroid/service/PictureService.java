package com.example.helloandroid.service;

import com.example.helloandroid.listener.DeleteCallback;
import com.example.helloandroid.listener.DownloadFeedback;
import com.example.helloandroid.listener.ResultCallbackListener;
import com.example.helloandroid.model.Picture;
import com.example.helloandroid.utils.HttpUtils;
import com.google.gson.Gson;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PictureService {

    private Gson gson;

    public PictureService() {
        gson = new Gson();
    }

    public void uploadPicture(final String token, final ArrayList<Picture> pictures, final ResultCallbackListener resultCallbackListener) {
        new Thread(){
            @Override
            public void run() {
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.addPart("token", new StringBody(token, ContentType.TEXT_PLAIN));
                for (Picture picture : pictures) {
                    File file = new File(picture.getPath());
                    entityBuilder.addBinaryBody(picture.getPath(), file, ContentType.create("image/jpeg"), file.getName());
                }
                String responseJson = null;
                try {
                    responseJson = HttpUtils.getInstance().post(HttpUtils.BASE_URL + "/api/upload/", entityBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(responseJson);
                if (responseJson.contains("errorcode")) {
                    Map<String, String> responseMap = gson.fromJson(responseJson, HttpUtils.mapType);
                    System.out.println(responseMap);
                    resultCallbackListener.onError("错误码:" + responseMap.get("errorcode") + "错误信息:" + responseMap.get("msg"));
                } else {
                    Map<String, Boolean> responseMap = gson.fromJson(responseJson, HttpUtils.ResultType);
                    resultCallbackListener.onSuccess(responseMap);
                }
            }
        }.start();
    }

    public void downloadImg(final String token, final DownloadFeedback downloadFeedback) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        String params = gson.toJson(map);
        HttpUtils.getInstance().postWithOkhttp(HttpUtils.BASE_URL + "/api/geturl/", params, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseJson = response.body().string();
//                System.out.println(responseJson);
                if (responseJson.contains("errorcode")) {
                    Map<String, String> responseMap = gson.fromJson(responseJson, HttpUtils.mapType);
                    downloadFeedback.onError("错误码:" + responseMap.get("errorcode") + "错误信息:" + responseMap.get("msg"));
                } else {
                    Map<String, ArrayList<Picture>> responseMap = gson.fromJson(responseJson, HttpUtils.ImageArrayType);
//                    System.out.println("ResponseMap = " + "http://www.huanghecs.win:5000"+responseMap.get("photo_list").get(1).getUrl());
                    downloadFeedback.onSuccess(responseMap.get("photo_list"));
                }
            }
        });
    }

    public void deleteImg(final String token, ArrayList<String> path, final DeleteCallback deleteCallback){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token",token);
        map.put("path",path);
        String str = gson.toJson(map);
        HttpUtils.getInstance().postWithOkhttp(HttpUtils.BASE_URL + "/api/delete/", str, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseJson = response.body().string();
                System.out.println(responseJson);
                if(responseJson.contains("errorcode")){
                    Map<String, String> responseMap = gson.fromJson(responseJson, HttpUtils.mapType);
                    deleteCallback.onError("错误码:" + responseMap.get("errorcode") + "错误信息:" + responseMap.get("msg"));
                }else{
                    Map<String,Map<String,Boolean>> responseMap = gson.fromJson(responseJson, HttpUtils.ResultArrayType);
                    deleteCallback.onSuccess(responseMap.get("ret"));
                }
            }
        });
    }
}