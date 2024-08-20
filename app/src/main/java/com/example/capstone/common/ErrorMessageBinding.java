package com.example.capstone.common;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessageBinding {

    public static Map<String, String> getErrorMessages(JSONObject jsonObject) throws JSONException {

        JSONArray validationErrors = jsonObject.optJSONArray("validationErrors");
        Map<String, String> errorMessages = new HashMap<>();

        for (int i = 0; i < validationErrors.length(); i++) {
            JSONObject error = validationErrors.getJSONObject(i);
            String field = error.optString("field");
            String reason = error.optString("reason");

            errorMessages.put(field, reason);
        }

        return  errorMessages;
    }
}
