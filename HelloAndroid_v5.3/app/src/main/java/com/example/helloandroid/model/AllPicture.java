package com.example.helloandroid.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;

import com.example.helloandroid.R;

import java.io.File;
import java.util.ArrayList;

/*
* 这个类整合了所有待显示的图片,方便其他活动对文件进行操作*/

public class AllPicture {
    public ArrayList<String> picName;   // 文件名集合
    public ArrayList<File> picture;     // 文件集合
    public ArrayList<CheckBox> picBoxes;    // 每个文件在item中的checkBox

    // 说明: 以上三个变量之间产生联系的唯一方式是通过下标来标识
    public ArrayList<Boolean> picNet;
    private String token;
    /* 构造函数
    * 只有一个传入参数,它告诉构造函数应该从哪个文件夹中获取所有文件
    * */
    public AllPicture(String fileAbsolutePath) {

        picName = new ArrayList<>();
        picture = new ArrayList<>();
        picBoxes = new ArrayList<>();
        picNet = new ArrayList<>();
        File file = new File(fileAbsolutePath);

        File[] subFile = file.listFiles();

        assert subFile != null;
        for (File value : subFile) {
            // 判断是否为文件夹
            if (!value.isDirectory()) {
                String filename = value.getName();
                // 按扩展名判断是否为图片
//                if (filename.trim().toLowerCase().endsWith(".jpg") || filename.trim().toLowerCase().endsWith(".png") || filename.trim().toLowerCase().endsWith(".bmp")) {
                picName.add(fileAbsolutePath + "/" + filename);
                picture.add(value);
                picNet.add(false);
//                }
            }
        }
    }
}