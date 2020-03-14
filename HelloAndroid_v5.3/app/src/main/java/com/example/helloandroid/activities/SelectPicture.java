package com.example.helloandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.helloandroid.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectPicture extends AppCompatActivity {

    private Button camera, album;
    final int REQUEST_CAMERA = 1;
    final int REQUEST_ALBUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        initView();
    }

    public void initView () {
        camera = (Button) findViewById(R.id.camera);
        album = (Button) findViewById(R.id.album);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_ALBUM);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    dealWithCamera(data);
                    break;
                case REQUEST_ALBUM:
                    dealWithAlbum(data);
                    break;
            }
        }
    }

    public void dealWithCamera(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        savePictureInternal(imageBitmap);
//        showPictureInImageView(imageBitmap);
    }

    public void dealWithAlbum(Intent data) {

        //打开相册并选择照片，这个方式选择单张
        // 获取返回的数据，这里是android自定义的Uri地址
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        // 获取选择照片的数据视图
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        // 从数据视图中获取已选择图片的路径
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
        savePictureInternal(imageBitmap);
        // 将图片显示到界面上
//        showPictureInImageView(imageBitmap);
    }

    public void savePictureInternal(Bitmap imageBitmap) {
        @SuppressLint("SimpleDateFormat") String filename = new SimpleDateFormat("MMdd_HH_mm_ss").format(new Date());
        filename += ".jpg";
//        String pathName = getFilesDir().toString();
        File file = new File(getFilesDir(), filename);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush(); bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPictureInImageView(Bitmap imageBitmap) {
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(imageBitmap);
    }
}