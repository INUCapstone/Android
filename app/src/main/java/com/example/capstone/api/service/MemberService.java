package com.example.capstone.api.service;

import com.example.capstone.api.ApiController;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.RetrofitClient;
import com.example.capstone.dto.LoginReq;
import com.example.capstone.dto.SignupReq;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberService {

    private ApiController apiController;

    public MemberService() {
        this.apiController = RetrofitClient.getInstance();
    }

    public void signup(SignupReq signupReq, final RepositoryCallback callback) {

        apiController.signup(signupReq).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 성공 시 콜백 호출
                    callback.onSuccess("회원가입이 완료되었습니다.");
                } else {
                    // 실패 시 콜백 호출
                    callback.onFailure("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 네트워크 오류 시 콜백 호출
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public void login(LoginReq loginReq, final RepositoryCallback callback){

        apiController.login(loginReq).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 성공 시 콜백 호출
                    callback.onSuccess("로그인 성공");
                } else {
                    // 실패 시 콜백 호출
                    callback.onFailure("login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 네트워크 오류 시 콜백 호출
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
}

