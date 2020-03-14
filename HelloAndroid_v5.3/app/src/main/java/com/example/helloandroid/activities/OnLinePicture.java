package com.example.helloandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.helloandroid.R;
import com.example.helloandroid.adapter.LocalAdapter;
import com.example.helloandroid.adapter.OnLineAdapter;
import com.example.helloandroid.listener.DeleteCallback;
import com.example.helloandroid.listener.DownloadFeedback;
import com.example.helloandroid.model.AllPicture;
import com.example.helloandroid.model.Picture;
import com.example.helloandroid.service.PictureService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
* 本文件为在线图片展示活动的类,其适配器为OnLineAdapter.class
* */
public class OnLinePicture extends AppCompatActivity {

    private AllPicture allPicture;  // 一个整合了所有图片的类
    private ArrayList<Picture> pictures;    // 放置所有从服务器上下载的图片
    private PictureService pictureService;
    private GridView gridView;  // gridView
    private SparseBooleanArray isSelected = new SparseBooleanArray();   // 记录item的checkBox状态
    private OnLineAdapter adapter;  // 适配器
    private boolean isMultiSelectMode = false;  // 设置gridView显示模式,即有无checkBox视图
    private String token;
    private boolean x = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_line_picture);

//        Intent intent = getIntent();
//        token = intent.getStringExtra("token");

        String defaultValue = null;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        token = sharedPref.getString(getString(R.string.preference_file_key), defaultValue);

        pictures = new ArrayList<>();
        initView();
    }

    public void initView() {
        final String filePath = getApplicationContext().getFilesDir().getAbsolutePath();
        gridView = (GridView) findViewById(R.id.grid_View); // 获取gridView控件
        for (int i = 0; i < pictures.size(); i++) {
            isSelected.put(i, false);
        }   // 初始化checkBox状态记录变量
        x = true;//重置状态，在删除下载时有用，
        getPictureFromServer();
        while(x){
            try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        adapter = new OnLineAdapter(pictures, isMultiSelectMode, getApplicationContext(), isSelected);
        gridView.setAdapter(adapter);   // 绑定适配器
        adapter.notifyDataSetChanged();
        // 单击全屏显示
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), OnlineShow.class);
//                intent.putExtra("filePath", filePath);
                intent.putExtra("bitmap", pictures.get(position).getBitmap());
                startActivity(intent);
            }
        });
        //长按
        final TableLayout tableLayout = findViewById(R.id.tableLayout);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                updateGridView( true);
                tableLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });
        //删除
        ArrayList path = new ArrayList ();
        final Button deleteButton = findViewById(R.id.del_pic);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.setVisibility(View.GONE);
                isSelected = adapter.getIsSelected();    // 读取当前checkBox状态
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        String ss = pictures.get(i).getPath();
                        path.add(ss);// CheckBox被选时,删除对应图片
                    }
                }
                pictureService = new PictureService();
                pictureService.deleteImg(token, path, new DeleteCallback() {
                    @Override

                    public void onSuccess(Map<String, Boolean> result) {
                        Looper.prepare();
                        Object s[] = result.keySet().toArray();
                        Boolean pan = true;
                        for(int i = 0; i < result.size(); i++) {
                            if(result.get(s[i]) == false){
                                pan = false;
                            }
                        }
                        if(pan) {
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                        }
                        else{

                            Toast.makeText(getApplicationContext(),"失败",Toast.LENGTH_LONG).show();

                        }
                        isMultiSelectMode = false;
                        Looper.loop();
                    }

                    @Override
                    public void onError(String exception) {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),exception,Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }

                });
                while (isMultiSelectMode){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                initView();
            }
        });
        //取消
        final Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.setVisibility(View.GONE);
                updateGridView(false);
            }
        });
        //下载到本地
        final Button uploadButton = findViewById(R.id.upload_pic);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> path = new ArrayList<String>();
                ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
                isSelected = adapter.getIsSelected();    // 读取当前checkBox状态
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        String ss = pictures.get(i).getPath();
                        Bitmap bb = pictures.get(i).getBitmap();
                        path.add(ss);
                        bitmaps.add(bb);
                    }
                }
                allPicture = new AllPicture(filePath);
                int count = 0;
                for(String ss: path) {
                    int i = 0;
                    String[] array = ss.split("/");
                    String local_pic = array[array.length-1];
                    for(String pic: allPicture.picName){
                        if(local_pic.equals(pic)){
                            Toast.makeText(getApplicationContext(),"文件"+pic+"已存在",Toast.LENGTH_LONG).show();
                            i = 1;
                        }
                    }
                    if (i==1){
                        count++;
                        continue;
                    }
                    int idx = ss.lastIndexOf("/");
                    ss = ss.substring(idx + 1, ss.length());
                    File imageFile = new File(getFilesDir(), ss);
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(imageFile);
                        Bitmap image = bitmaps.get(count);
                        count++;
                        image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                        Toast.makeText(getApplicationContext(),"下载成功",Toast.LENGTH_LONG).show();
//                        Toast.makeText(UIUtils.getContext(), UIUtils.getString(R.string.save_picture_success), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"失败",Toast.LENGTH_LONG).show();
//
                    }
                }
                tableLayout.setVisibility(View.GONE);
                isMultiSelectMode = false;
                initView();
            }
        });
    }

    private void getPictureFromServer() {
        pictures.clear();
        pictureService = new PictureService();
        pictureService.downloadImg(token, new DownloadFeedback() {
            @Override
            public void onSuccess(ArrayList<Picture> PictureList) {
                for (Picture image: PictureList) {
                    Bitmap bitmap = null;
                    InputStream is = null;
                    String url = "http://www.huanghecs.win:5000"+image.getUrl();
                    try {
                        is = new java.net.URL(url).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String path = image.getPath();
                    File file = new File(path);
                    if(file.exists()){
                        image.islocal = true;
                    }
                    else image.islocal = false;

                    bitmap = BitmapFactory.decodeStream(is);
                    image.setBitmap(bitmap);
                    Bitmap bitmap11 = image.getBitmap();
                   if(!image.islocal) {
                       pictures.add(image);
                   }
                }
                x =false;

            }

            @Override
            public void onError(String exception) {

            }
        });
    }

    public void updateGridView(boolean MultiSelectMode) {
        isMultiSelectMode = MultiSelectMode;
        for (int i = 0; i < pictures.size(); i++) {
            isSelected.put(i, false);
        }
        adapter = new OnLineAdapter(pictures, isMultiSelectMode, getApplicationContext(), isSelected);
        gridView.setAdapter(adapter);
    }

}