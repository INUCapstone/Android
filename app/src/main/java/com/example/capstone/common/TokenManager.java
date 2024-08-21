package com.example.capstone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // 토큰을 userId를 키로 사용하여 저장하는 메서드
    public void saveToken(String token) {
        String userId = JwtUtils.getUserIdFromToken(token); // JWT에서 userId 추출
        if (userId != null) {
            editor.putString(userId, token);
            editor.apply();
            Log.d("TokenManager", "Token saved for userId: " + userId);
        }
        else {
            Log.d("TokenManager", "Failed to extract userId from token");
        }
    }

    // 토큰을 userId를 키로 사용하여 가져오는 메서드
    public String getToken(String userId) {
        return sharedPreferences.getString(userId, null);
    }

    // 모든 토큰을 삭제하는 메서드
    public void clearTokens() {
        editor.clear();
        editor.apply();
    }
}
