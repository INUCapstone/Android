package com.example.capstone.api;

import com.example.capstone.dto.LoginReq;
import com.example.capstone.dto.SignupReq;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiController {

    @POST("/members")
    Call<ResponseBody> signup(@Body SignupReq signupReq);

    @POST("/members/login")
    Call<ResponseBody> login(@Body LoginReq loginReq);
}
