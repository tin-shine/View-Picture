package com.example.helloandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.helloandroid.listener.DownloadFeedback;
import com.example.helloandroid.model.AllPicture;
import com.example.helloandroid.R;
import com.example.helloandroid.model.Picture;
import com.example.helloandroid.service.PictureService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LocalAdapter extends BaseAdapter {
    private static SparseBooleanArray isSelected;   // 记录每个item选中与否
    private List<Picture> list;
    public String token;
    private PictureService pictureService;
    private LayoutInflater inflater;
    public HashSet<String> set ;
    private boolean multiSelectMode;    // 设置视图显示模式:false=普通模式,true=多选模式
    private ArrayList<com.example.helloandroid.model.Picture> pictures;
    public LocalAdapter(AllPicture allPicture, boolean isMultiSelectMode, Context context, SparseBooleanArray isSelectedBox, HashSet<String> set) {
        super();
        this.set = set;
        System.out.println(3333);
        System.out.println(set);
        this.token = token;
        list = new ArrayList<>();   // 存放所有文件的列表
        isSelected = isSelectedBox;   // 更新item选中状态
        inflater = LayoutInflater.from(context);
        multiSelectMode = isMultiSelectMode;    // 设置视图显示模式

        for (int i = 0; i < allPicture.picture.size(); i++)
        {
            Picture pic = new Picture(allPicture.picName.get(i), allPicture.picture.get(i));
            list.add(pic);      // 读取文件到文件列表中

        }

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
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.pic_thumbnail, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.txt);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.img);
            ///获取整个relativeLayout
            viewHolder.item = convertView.findViewById(R.id.picture_item);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.box);
            viewHolder.checkBox.setOnClickListener(null); // 有博客说这里不置null会导致系统判断错乱
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String s = list.get(position).getTitle();
        viewHolder.title.setText(list.get(position).getTitle() );
        System.out.println(111);
        System.out.println(s);
        System.out.println(set);
        System.out.println(set.contains(s));
        if(set.contains(s)){
            viewHolder.title.setText(list.get(position).getTitle() + "服务器中存在" );
        }
        else {
            viewHolder.title.setText(list.get(position).getTitle() + "服务器中不存在" );
        }
        viewHolder.image.setImageBitmap(list.get(position).getImage());
        viewHolder.checkBox.bringToFront();     // 将checkbox显示在图片之上
        if (multiSelectMode) {      // 多选模式下
            viewHolder.checkBox.setVisibility(View.VISIBLE);    // 显示checkBox
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.checkBox.toggle();
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

    class Picture {
        private String name;
        private File pic;
        private boolean isnet;
        public Picture(String picName, File picFile) {
            name = picName;
            pic = picFile;
        }
        public Picture(String picName, File picFile ,Boolean isnet) {
            name = picName;
            pic = picFile;
            this.isnet = isnet;
        }

        public String getTitle() {
//            return title;
            return name;
        }
        public String getIsnet() {
//            return title;
            if(isnet){
                return "服务器中已存在";
            }
           else return "服务器中不存在";
        }

        public Bitmap getImage() {
            String path = pic.getAbsolutePath();
            return BitmapFactory.decodeFile(path);
        }
    }
}