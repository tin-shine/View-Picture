package com.example.helloandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helloandroid.R;
import com.example.helloandroid.service.UserService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        register();
    }

    public void initView() {
        usernameEditText = findViewById(R.id.new_username);
        passwordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register);
        userService = new UserService();
    }

    public void register () {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (illegalCheck()) {
                    new Thread(() -> userService.userRequest("register", usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), getApplicationContext())).start();
                }
            }
        });
    }

    public boolean illegalCheck () {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (username.equals("")) {
            Toast.makeText(getApplicationContext(), "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.equals("")) {
            Toast.makeText(getApplicationContext(), "密码不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(), "密码前后不一致！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

//    public void OkttpRequest() {
//        Gson gson = new Gson();
//        Map<String, String> o = new HashMap<>();
//
//        gson.toJson(o);
//    }
}
