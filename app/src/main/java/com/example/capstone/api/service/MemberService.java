package com.example.capstone.api.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.capstone.R;
import com.example.capstone.activity.LoginActivity;
import com.example.capstone.api.ApiController;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.RetrofitClient;
import com.example.capstone.common.ErrorMessageBinding;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.common.TokenManager;
import com.example.capstone.dto.ModifyUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberService {

    private ApiController apiController;
    private TokenManager tokenManager;
    private Context context;

    public MemberService(Context context) {
        this.context=context;
        this.apiController = RetrofitClient.getInstance();
        this.tokenManager = new TokenManager(context);
    }

    public void getUserInfo(final RepositoryCallback callback){
        String token = tokenManager.getAccessToken();

        apiController.getUserInfo(token).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    String responseData = null;
                    try {
                        responseData = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("myInfo", responseData);

                    // 프래그먼트에 데이터 전달
                    NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_activity_main);
                    navController.navigate(navController.getCurrentDestination().getId(), bundle);
                    // 성공 시 콜백 호출
                    callback.onSuccess("유저정보 조회 성공");
                }
                else if(response.code() == 500 ){
                    callback.onFailure(ExceptionCode.SERVER_INNER_ERROR,null);
                }
                else if(response.code() == 400){
                    callback.onFailure(ExceptionCode.INPUT_VAILDATION_ERORR,null);
                }
                else{
                    callback.onFailure(ExceptionCode.AUTH_FAIL,null);
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(ExceptionCode.NETWORK_ERROR, null);
            }
        });
    }

    public void modifyUserInfo(ModifyUser modifyUser, final RepositoryCallback callback){
        String token = tokenManager.getAccessToken();

        apiController.modifyUserInfo(token,modifyUser).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    // 성공 시 콜백 호출
                    callback.onSuccess("유저정보 수정 성공");
                }
                else if(response.code() == 500 ){
                    callback.onFailure(ExceptionCode.SERVER_INNER_ERROR,null);
                }
                else if(response.code() == 400){
                    try {
                        // 실패 시 콜백 호출
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = null;
                        jsonObject = new JSONObject(errorBody);
                        Map<String, String> errorMessages = ErrorMessageBinding.getErrorMessages(jsonObject);
                        callback.onFailure(ExceptionCode.INPUT_VAILDATION_ERORR, errorMessages);

                    } catch (JSONException e) {
                        callback.onFailure(ExceptionCode.SERVER_INNER_ERROR,null);
                    } catch (IOException e) {
                        callback.onFailure(ExceptionCode.SERVER_INNER_ERROR,null);
                    }
                }
                else{
                    callback.onFailure(ExceptionCode.AUTH_FAIL,null);
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(ExceptionCode.NETWORK_ERROR, null);
            }
        });
    }

    public void driverArrive(Long driverId, final RepositoryCallback callback) {
        String token = tokenManager.getAccessToken();

        apiController.sendDriverArrive(token,driverId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 성공 시 콜백 호출
                    callback.onSuccess("기사정보 수정 성공");
                }
                else{
                    callback.onFailure(ExceptionCode.AUTH_FAIL,null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(ExceptionCode.NETWORK_ERROR, null);
            }
        });
    }

    private void redirectToLogin() {
        // 현재 액티비티의 context를 사용하여 로그인 페이지로 이동
        Intent intent = new Intent(context, LoginActivity.class);

        // 기존 액티비티 스택을 모두 비우고 로그인 페이지로 이동
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
