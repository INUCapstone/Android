package com.example.capstone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.capstone.R;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.service.MemberService;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.dto.LoginReq;
import com.example.capstone.dto.SignupReq;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private CardView signupForm;
    private CardView loginForm;
    private EditText loginEmailEdit, loginPasswordEdit;
    private EditText signupEmailEdit, signupPasswordEdit, signupPasswordRepeatEdit, signupPhoneNumEdit, signupNicknameEdit;
    private Button loginButton, goSignupPageButton, backToLoginButton, signupButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setup();
        
        // 상태바 색상 변경
        if (getWindow() != null) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.green));
        }
        
        // 로그인 버튼
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 로직을 수행
                LoginReq loginReq = LoginReq.builder()
                        .email(loginEmailEdit.getText().toString())
                        .password(loginPasswordEdit.getText().toString())
                        .build();

                MemberService memberService = new MemberService(LoginActivity.this);
                memberService.login(loginReq, new RepositoryCallback() {
                    @Override
                    public void onSuccess(String message) {
                        // MainActivity로 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        // LoginActivity를 종료하여 뒤로 가기 시 돌아오지 않도록 함
                        finish();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {

                        if(errorMessages != null){
                            showErrorMessageLogin(errorMessages);
                        }
                        if(errorMessages.containsKey("message")){
                            Toast.makeText(LoginActivity.this, errorMessages.get("message"), Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(LoginActivity.this, exceptionCode.getExceptionMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                SignupReq signupReq = SignupReq.builder()
                        .email(signupEmailEdit.getText().toString())
                        .password(signupPasswordEdit.getText().toString())
                        .confirmPassword(signupPasswordRepeatEdit.getText().toString())
                        .phoneNumber(signupPhoneNumEdit.getText().toString())
                        .nickname(signupNicknameEdit.getText().toString())
                        .build();

                MemberService memberService = new MemberService(LoginActivity.this);
                memberService.signup(signupReq, new RepositoryCallback() {
                    @Override
                    public void onSuccess(String message) {
                        // 로그인 페이지로 이동
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        clearSignupForm();
                        signupForm.setVisibility(View.GONE);
                        loginForm.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {
                        if(errorMessages != null){
                            showErrorMessageSignup(errorMessages);
                        }
                        if(errorMessages.containsKey("message")){
                            Toast.makeText(LoginActivity.this, errorMessages.get("message"), Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(LoginActivity.this, exceptionCode.getExceptionMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


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

    private void setup(){
        loginForm = findViewById(R.id.login_form);
        signupForm = findViewById(R.id.signup_form);

        loginButton = findViewById(R.id.loginButton);
        goSignupPageButton = findViewById(R.id.goSignupPageButton);
        backToLoginButton = findViewById(R.id.backToLoginPageButton);
        signupButton = findViewById(R.id.signupButton);

        loginEmailEdit = findViewById(R.id.loginEmailEdit);
        loginPasswordEdit = findViewById(R.id.loginPasswordEdit);
        signupEmailEdit = findViewById(R.id.signupEmailEdit);
        signupPasswordEdit = findViewById(R.id.signupPasswordEdit);
        signupPasswordRepeatEdit = findViewById(R.id.signupPasswordRepeatEdit);
        signupPhoneNumEdit = findViewById(R.id.signupPhoneNumEdit);
        signupNicknameEdit = findViewById(R.id.signupNicknameEdit);
    }

    private void showErrorMessageSignup(Map<String, String> errorMessages){

        if(errorMessages.containsKey("email")){
            signupEmailEdit.setText("");
            signupEmailEdit.setHintTextColor(Color.RED);
            signupEmailEdit.setHint(errorMessages.get("email"));
        }
        if(errorMessages.containsKey("password")){
            signupPasswordEdit.setText("");
            signupPasswordEdit.setHintTextColor(Color.RED);
            signupPasswordEdit.setHint(errorMessages.get("password"));
        }
        if(errorMessages.containsKey("confirmPassword")){
            signupPasswordRepeatEdit.setText("");
            signupPasswordRepeatEdit.setHintTextColor(Color.RED);
            signupPasswordRepeatEdit.setHint(errorMessages.get("confirmPassword"));
        }
        if(errorMessages.containsKey("nickname")){
            signupNicknameEdit.setText("");
            signupNicknameEdit.setHintTextColor(Color.RED);
            signupNicknameEdit.setHint(errorMessages.get("nickname"));
        }
        if(errorMessages.containsKey("phoneNumber")){
            signupPhoneNumEdit.setText("");
            signupPhoneNumEdit.setHintTextColor(Color.RED);
            signupPhoneNumEdit.setHint(errorMessages.get("phoneNumber"));
        }
    }

    private void showErrorMessageLogin(Map<String, String> errorMessages){

        if(errorMessages.containsKey("email")){
            loginEmailEdit.setText("");
            loginEmailEdit.setHintTextColor(Color.RED);
            loginEmailEdit.setHint(errorMessages.get("email"));
        }
        if(errorMessages.containsKey("password")){
            loginPasswordEdit.setText("");
            loginPasswordEdit.setHintTextColor(Color.RED);
            loginPasswordEdit.setHint(errorMessages.get("password"));
        }

    }
}