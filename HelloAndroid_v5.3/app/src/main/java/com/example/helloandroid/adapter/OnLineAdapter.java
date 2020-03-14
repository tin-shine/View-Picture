package com.example.helloandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.helloandroid.model.AllPicture;
import com.example.helloandroid.R;
import com.example.helloandroid.model.Picture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
* 此文件为在线图片展示活动中gridView对应的适配器
* */

public class OnLineAdapter extends BaseAdapter {
    private static SparseBooleanArray isSelected;   // 记录每个item选中与否
    private ArrayList<Picture> list;
    private LayoutInflater inflater;
    private boolean multiSelectMode;    // 设置视图显示模式:false=普通模式,true=多选模式

    public OnLineAdapter(ArrayList<Picture> pictures, boolean isMultiSelectMode, Context context, SparseBooleanArray isSelectedBox) {
        super();
        list = pictures;
        isSelected = isSelectedBox;   // 更新item选中状态
        inflater = LayoutInflater.from(context);
        multiSelectMode = isMultiSelectMode;    // 设置视图显示模式
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OnLineAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.pic_thumbnail, null);
            viewHolder = new OnLineAdapter.ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.txt);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.img);
            ///获取整个relativeLayout
            viewHolder.item = convertView.findViewById(R.id.picture_item);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.box);
            viewHolder.checkBox.setOnClickListener(null); // 有博客说这里不置null会导致系统判断错乱
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OnLineAdapter.ViewHolder) convertView.getTag();
        }
        Bitmap bitmap = list.get(position).getBitmap();


            viewHolder.title.setText(list.get(position).getUrl());
            viewHolder.image.setImageBitmap(bitmap); // 设置图片
            viewHolder.checkBox.bringToFront();     // 将checkbox显示在图片之上

        if (multiSelectMode) {      // 多选模式下
            viewHolder.checkBox.setVisibility(View.VISIBLE);    // 显示checkBox
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.checkBox.toggle();   // 实现点击图片也能勾选/取消勾选checkBox
                }
            });
            // 注册checkBox监听事件
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isSelected.put(position, isChecked);    // 将变化后的checkBox状态存入isSelected中
                }
            });
        }

        return convertView;
    }

    public static SparseBooleanArray getIsSelected() {
        return isSelected;
    }

    class ViewHolder
    {
        public RelativeLayout item;
        public TextView title;
        public ImageView image;
        public CheckBox checkBox;

    }



    }

//    class Pic {
//        private String name;
//        private File pic;
//
//        public Pic(String picName, File picFile) {
//            name = picName;
//            pic = picFile;
//        }
//
//        public String getTitle() {
////            return title;
//            return name;
//        }
//
//        // 79行使用了这个函数来读取图片,在网络操作中这样可能带来问题:
//        // 因为访问网络文件不像访问本地文件只需要一个path,还涉及到其他的操作
//        public Bitmap getImage() {
//            String path = pic.getAbsolutePath();
//            return BitmapFactory.decodeFile(path);
//        }
//    }
