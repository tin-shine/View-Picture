package com.example.helloandroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.example.helloandroid.model.Picture;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

    private static volatile HttpUtils mInstance;
    private OkHttpClient mClient;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static Type mapType = new TypeToken<Map<String, String>>() {
    }.getType();
//    public static Type ObjType = new TypeToken<Map<String, Map<String,Boolean>>>() {
//    }.getType();
    public static Type ResultType = new TypeToken<Map<String, Boolean>>() {
    }.getType();

    public static Type ImageArrayType = new TypeToken<Map<String,ArrayList<Picture>>>() {
    }.getType();

    public static Type ResultArrayType = new TypeToken<Map<String, Map<String, Boolean>>>() {
    }.getType();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String BASE_URL = "http://www.huanghecs.win:5000";

    public static HttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (null == mInstance) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    private HttpUtils() {
        /**
         * 使用构造者模式
         * 设置连接超时
         * 读取超时
         * 写超时
         */
        mClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void getWithOkhttp(String url, okhttp3.Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(callback);
    }

    public void postWithOkhttp(String url, String params, okhttp3.Callback callback) {
        RequestBody body = RequestBody.create(params, JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        mClient.newCall(request).enqueue(callback);
    }

    public String post(String url, String params) {
        RequestBody body = RequestBody.create(JSON, params);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = mClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String post(String url, HttpEntity entity) throws IOException {
        HttpClient client = HttpClients.custom().build();
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        return EntityUtils.toString(response.getEntity());
    }
}