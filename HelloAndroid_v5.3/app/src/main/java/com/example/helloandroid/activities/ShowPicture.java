package com.example.helloandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.helloandroid.R;
import com.example.helloandroid.adapter.*;
import com.example.helloandroid.listener.DownloadFeedback;
import com.example.helloandroid.listener.ResultCallbackListener;
import com.example.helloandroid.model.AllPicture;
import com.example.helloandroid.model.MyAPPContext;
import com.example.helloandroid.model.Picture;
import com.example.helloandroid.service.PictureService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ShowPicture extends AppCompatActivity {

//    private MyAPPContext myAPPContext;
    private AllPicture allPicture;
    private GridView gridView;
    private SparseBooleanArray isSelected = new SparseBooleanArray();
    private LocalAdapter adapter;
    private boolean isMultiSelectMode = false;
    private PictureService pictureService = new PictureService();
    private ArrayList<Picture> pictureUploadSet = new ArrayList<>();
    private String token;
    public int num;
    public HashSet<String> set = new HashSet<>();
    private ArrayList<com.example.helloandroid.model.Picture> pictures;
    private static final int COMPLETED = 1;
    private static final int ERROR = 2;
    private boolean x = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
//        Intent intent = getIntent();
//        token = intent.getStringExtra("token");

        String defaultValue = null;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        token = sharedPref.getString(getString(R.string.preference_file_key), defaultValue);

        getPictureFromServer();
        initView();

    }
    private void getPictureFromServer() {

        pictureService = new PictureService();
        pictureService.downloadImg(token, new DownloadFeedback() {
            @Override
            public void onSuccess(ArrayList<com.example.helloandroid.model.Picture> PictureList) {
                for (com.example.helloandroid.model.Picture image: PictureList) {
                    Bitmap bitmap = null;
                    InputStream is = null;
                    String url = "http://www.huanghecs.win:5000"+image.getUrl();
                    try {
                        is = new java.net.URL(url).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String path = image.getPath();
                    System.out.println(222);
                    set.add(path);
                    System.out.println(set);



                }
                num = set.size();

            }

            @Override
            public void onError(String exception) {

            }
        });
    }

    public void initView() {
        final String filePath = getApplicationContext().getFilesDir().getAbsolutePath();
//        System.out.println(filePath);
        allPicture = new AllPicture(filePath);  // 读取所有文件
        for (int i = 0; i < allPicture.picture.size(); i++) {
            isSelected.put(i, false);
        }
        gridView = (GridView) findViewById(R.id.grid_View);
        try
        {
            Thread.currentThread().sleep(300);//毫秒
        }
        catch(Exception e){}
        adapter = new LocalAdapter(allPicture, isMultiSelectMode, getApplicationContext(), isSelected, this.set);
        gridView.setAdapter(adapter);
        // 单击全屏显示
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FullScreenPicture.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("picName", allPicture.picName.get(position));
                startActivity(intent);
                finish();
            }
        });







        final TableLayout tableLayout = findViewById(R.id.tableLayout);
        // 长按进入多选删除上传模式
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                updateGridView(filePath, true);
                tableLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });

        final Button deleteButton = findViewById(R.id.del_pic);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.setVisibility(View.GONE);
                isSelected = LocalAdapter.getIsSelected();    // 读取当前checkBox状态
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        allPicture.picture.get(i).delete(); // CheckBox被选时,删除对应图片
                    }
                }
                updateGridView(filePath, false);
            }
        });
        final Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.setVisibility(View.GONE);
                updateGridView(filePath, false);
            }
        });
        final Button uploadButton = findViewById(R.id.upload_pic);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.setVisibility(View.GONE);
                isSelected = LocalAdapter.getIsSelected();    // 读取当前checkBox状态
                for (int i = 0; i < isSelected.size(); i++) {
                    if ((isSelected.get(i))) {  // 若第i个图片被选中
                        // 上传此图片
                        String name = allPicture.picName.get(i);
                        String path = allPicture.picture.get(i).getAbsolutePath();
//                        allPicture.picNet.set(i,true);
                        pictureUploadSet.add(new Picture(name, path));
                    }
                }
                pictureService.uploadPicture(token, pictureUploadSet, new ResultCallbackListener() {
                    @Override
                    public void onSuccess(Map<String, Boolean> result) {
                        System.out.println(result);
                        boolean flag = true;
                        Iterator<Map.Entry<String, Boolean>> iteratorMap = result.entrySet().iterator();
                        while (iteratorMap.hasNext()) {
                            Map.Entry<String, Boolean> next = iteratorMap.next();
                            if (!next.getValue()) {
                                Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                                flag = false;
                            }
                        }
                        if (flag) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }

                    @Override
                    public void onError(String exception) {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });
                updateGridView(filePath, false);
            }
        });
    }

    public void updateGridView(String filePath, boolean MultiSelectMode) {
        isMultiSelectMode = MultiSelectMode;
        allPicture = new AllPicture(filePath);
        for (int i = 0; i < allPicture.picture.size(); i++) {
            isSelected.put(i, false);
        }
        adapter = new LocalAdapter(allPicture, isMultiSelectMode, getApplicationContext(), isSelected, set);
        gridView.setAdapter(adapter);
    }

}