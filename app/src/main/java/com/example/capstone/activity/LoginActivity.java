package com.example.capstone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.green_gradient_status_bar));
        }

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 로그인 로직을 수행

                // MainActivity로 이동
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                // LoginActivity를 종료하여 뒤로 가기 시 돌아오지 않도록 함
                finish();
            }
        });
    }
}