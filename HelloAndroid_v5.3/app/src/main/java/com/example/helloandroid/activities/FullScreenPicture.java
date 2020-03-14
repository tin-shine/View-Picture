package com.example.helloandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.helloandroid.R;
import com.example.helloandroid.model.AllPicture;

import java.io.File;
/*
* 该类用于全屏显示一张图片
* */
public class FullScreenPicture extends AppCompatActivity {
    AllPicture allPicture;  // 获取全部待显示文件
    String filePath;    // 从Intent传过来的路径,它指示了要访问哪一个文件夹下文件,用于初始化allPicture类
    int position;   // 它指示了要全屏展示第几个文件,它的值通过传入的文件名来确定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_picture);

        Intent intent = getIntent();    // 从单击图片启动而来
        filePath = intent.getStringExtra("filePath");
        allPicture = new AllPicture(filePath);  // 根据传入路径获得文件集合
        String picName = intent.getStringExtra("picName");
        position = allPicture.picName.indexOf(picName); // 确定全屏展示第几个文件

        ImageView imageView = (ImageView) findViewById(R.id.fullScreen);
        imageView.setImageBitmap(BitmapFactory.decodeFile(allPicture.picture.get(position).getAbsolutePath())); // 全屏展示

        // 实现长按弹出对话框,可选择删除图片
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(FullScreenPicture.this) {
                @Override
                public AlertDialog create() {
//                        d("对话框create，创建时调用");
                    return super.create();
                }

                @Override
                public AlertDialog show() {
//                        d("对话框show，显示时调用");
                    return super.show();
                }
            };

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
//                        d("对话框取消");
                    }
                });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
//                        d("对话框销毁");
                    }
                });

            dialog.setIcon(R.mipmap.ic_launcher)
                .setTitle("删除")
                .setMessage("确认删除？")
                .setCancelable(true)
                .setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onEnter();
                        }
                    })
                    .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onCancel();
                            }
                        });
            dialog.show();
            return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onEnter() {
        if(allPicture.picture.get(position).exists()) {
            File file = allPicture.picture.get(position);
            file.delete();
            finish();
        }
    }

    public void onCancel() {
        // do nothing
    }
}