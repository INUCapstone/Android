package com.example.capstone.common;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // 토큰을 userId를 키로 사용하여 저장하는 메서드
    public void saveToken(String token) {

        editor.putString("accessToken", "Bearer "+token);
        editor.apply();
    }

    // 토큰을 userId를 키로 사용하여 가져오는 메서드
    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", "notExist");
    }

    // 모든 토큰을 삭제하는 메서드
    public void clearTokens() {
        editor.clear();
        editor.apply();
    }

}
