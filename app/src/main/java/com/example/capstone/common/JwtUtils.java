package com.example.capstone.common;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class JwtUtils {

    // JWT에서 userId 추출하는 메서드
    public static String getUserIdFromToken(String token) {
        try {
            // JWT는 헤더, 페이로드, 서명으로 구성됨
            String[] parts = token.split("\\.");

            // 페이로드 부분을 Base64Url로 디코딩
            String payload = parts[1];
            String decodedPayload = new String(Base64.decode(payload, Base64.URL_SAFE));

            // JSON 문자열을 JSONObject로 변환
            JSONObject jsonObject = new JSONObject(decodedPayload);

            // userId 값 추출
            return jsonObject.optString("userId", null); // "userId"는 페이로드에 포함된 클레임 이름
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
