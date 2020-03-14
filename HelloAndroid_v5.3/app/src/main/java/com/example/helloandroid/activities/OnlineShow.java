package com.example.helloandroid.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloandroid.R;

public class OnlineShow extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_picture);
        ImageView img = findViewById(R.id.fullScreen);
        img.setImageBitmap(getIntent().getParcelableExtra("bitmap"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
