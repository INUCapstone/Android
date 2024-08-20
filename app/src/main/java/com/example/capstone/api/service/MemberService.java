package com.example.capstone.api.service;

import com.example.capstone.api.ApiController;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.RetrofitClient;
import com.example.capstone.common.ErrorMessageBinding;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.dto.LoginReq;
import com.example.capstone.dto.SignupReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
                    // 성공 시 콜백 호출
                    callback.onSuccess("로그인 성공");
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

