package com.example.capstone.api;

import com.example.capstone.dto.ChargePointReq;
import com.example.capstone.dto.LoginReq;
import com.example.capstone.dto.ModifyUser;
import com.example.capstone.dto.SignupReq;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface ApiController {

    @POST("/members")
    Call<ResponseBody> signup(@Body SignupReq signupReq);

    @POST("/members/login")
    Call<ResponseBody> login(@Body LoginReq loginReq);

    @GET("/members")
    Call<ResponseBody> getUserInfo(@Header("Authorization") String token);

    @PATCH("/members")
    Call<ResponseBody> modifyUserInfo(@Header("Authorization") String token, @Body ModifyUser modifyUser);

    @PATCH("/members/point")
    Call<ResponseBody> chargePoint(@Header("Authorization") String token, @Body ChargePointReq chargePointReq);
}
