package com.example.helloandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.helloandroid.fragment.InfoPanelFragment;
import com.example.helloandroid.fragment.MainPanelFragment;
import com.example.helloandroid.R;
import com.example.helloandroid.model.MyAPPContext;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
/*
* 此文件为导航栏对应的类文件,主要完成对底部导航栏按钮的监听和Fragment的加载以及活动跳转功能
 */
public class NavigationActivity extends AppCompatActivity {
    MainPanelFragment mainPanelFragment;
    InfoPanelFragment infoPanelFragment;
    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_MULTI_PROCESS);
        String oldToken = sharedPref.getString(getString(R.string.preference_file_key), token);

        if (oldToken != null) {
            token = oldToken;
        }

        if (token != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            String str = getString(R.string.preference_file_key);
            editor.putString(str, token).apply();
        }

        initView();
    }

    public void initView () {
        mainPanelFragment = new MainPanelFragment();
        infoPanelFragment = new InfoPanelFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, mainPanelFragment).commit();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        if (!mainPanelFragment.isAdded()) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainPanelFragment).commit();
                        }
                        return true;
                    case R.id.member_info:
                        if (!infoPanelFragment.isAdded()) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, infoPanelFragment).commit();
                        }
                        return true;
                    case R.id.import_picture:
                        startActivity(new Intent(getApplicationContext(), SelectPicture.class));
                        return true;
                    case R.id.local_picture:
                        Intent intent = new Intent(getApplicationContext(), ShowPicture.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        return true;
                    case R.id.online_picture:
                        Intent online_picture = new Intent(getApplicationContext(), OnLinePicture.class);
                        online_picture.putExtra("token", token);
                        startActivity(online_picture);
                }
                return false;
            }
        };
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
