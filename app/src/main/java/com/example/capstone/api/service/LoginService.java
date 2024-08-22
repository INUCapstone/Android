package com.example.capstone.api.service;

import android.content.Context;
import android.util.Log;

import com.example.capstone.api.ApiController;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.RetrofitClient;
import com.example.capstone.common.ErrorMessageBinding;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.common.TokenManager;
import com.example.capstone.dto.LoginReq;
import com.example.capstone.dto.SignupReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginService {

    private ApiController apiController;
    private TokenManager tokenManager;

    public LoginService(Context context) {
        this.apiController = RetrofitClient.getInstance();
        this.tokenManager = new TokenManager(context);
    }

    public void signup(SignupReq signupReq, final RepositoryCallback callback) {

        apiController.signup(signupReq).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 성공 시 콜백 호출
                    callback.onSuccess("회원가입이 완료되었습니다.");
                }
                else if(response.code() == 400 ){

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
                else if(response.code() == 500){
                    callback.onFailure(ExceptionCode.SERVER_INNER_ERROR, null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 네트워크 오류 시 콜백 호출
                callback.onFailure(ExceptionCode.NETWORK_ERROR, null);
            }
        });
    }

    public void login(LoginReq loginReq, final RepositoryCallback callback){

        apiController.login(loginReq).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try{
                        String responseBodyString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBodyString);
                        String accessToken = jsonObject.getJSONObject("response").getString("accessToken");
                        Log.d("login",accessToken);
                        // 토큰 저장
                        tokenManager.saveToken(accessToken);

                        // 성공 시 콜백 호출
                        callback.onSuccess("로그인 성공");
                    }catch (Exception e){
                        Log.d("token","토큰 저장실패"+e.toString());
                        callback.onFailure(ExceptionCode.SAVE_FAILE_TOKEN, null);
                    }
                }
                else if(response.code() == 400 ){
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
                else if(response.code() == 500){
                    callback.onFailure(ExceptionCode.SERVER_INNER_ERROR, null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 네트워크 오류 시 콜백 호출
                callback.onFailure(ExceptionCode.NETWORK_ERROR, null);
            }
        });
    }
}

