package com.example.capstone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.capstone.R;

public class LoginActivity extends AppCompatActivity {

    private CardView signupForm;
    private CardView loginForm;
    private EditText loginEmailEdit, loginPasswordEdit;
    private EditText signupEmailEdit, signupPasswordEdit, signupPasswordRepeatEdit, signupPhoneNumEdit, signupNicknameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginForm = findViewById(R.id.login_form);
        signupForm = findViewById(R.id.signup_form);

        Button loginButton = findViewById(R.id.loginButton);
        Button goSignupPageButton = findViewById(R.id.goSignupPageButton);
        Button backToLoginButton = findViewById(R.id.backToLoginPageButton);
        Button signupButton = findViewById(R.id.signupButton);

        loginEmailEdit = findViewById(R.id.loginEmailEdit);
        loginPasswordEdit = findViewById(R.id.loginPasswordEdit);
        signupEmailEdit = findViewById(R.id.signupEmailEdit);
        signupPasswordEdit = findViewById(R.id.signupPasswordEdit);
        signupPasswordRepeatEdit = findViewById(R.id.signupPasswordRepeatEdit);
        signupPhoneNumEdit = findViewById(R.id.signupPhoneNumEdit);
        signupNicknameEdit = findViewById(R.id.signupNicknameEdit);

        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.green));
        }
        
        // 로그인 버튼
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
        
        // 로그인 페이지 -> 회원가입 페이지로 가는 버튼
        goSignupPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLoginForm();
                loginForm.setVisibility(View.GONE);
                signupForm.setVisibility(View.VISIBLE);
            }
        });
        
        // 회원가입 페이지 -> 로그인 페이지로 가는 버튼
        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSignupForm();
                signupForm.setVisibility(View.GONE);
                loginForm.setVisibility(View.VISIBLE);
            }
        });
        
        // 회원가입 버튼
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 로직 수행

                // 로그인 페이지로 이동
                clearSignupForm();
                signupForm.setVisibility(View.GONE);
                loginForm.setVisibility(View.VISIBLE);
            }
        });
    }

    // 로그인 입력 필드 초기화
    private void clearLoginForm() {
        loginEmailEdit.setText("");
        loginPasswordEdit.setText("");
    }

    // 회원가입 입력 필드 초기화
    private void clearSignupForm() {
        signupEmailEdit.setText("");
        signupPasswordEdit.setText("");
        signupPasswordRepeatEdit.setText("");
        signupPhoneNumEdit.setText("");
        signupNicknameEdit.setText("");
    }
}