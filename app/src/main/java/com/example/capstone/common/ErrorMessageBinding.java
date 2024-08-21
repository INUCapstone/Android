package com.example.capstone.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessageBinding {

    public static Map<String, String> getErrorMessages(JSONObject jsonObject) throws JSONException {

        Map<String, String> errorMessages = new HashMap<>();
        if(jsonObject.optJSONArray("validationErrors") == null){
            errorMessages.put("message",jsonObject.optString("message"));
        }
        else{
            JSONArray validationErrors = jsonObject.optJSONArray("validationErrors");

            for (int i = 0; i < validationErrors.length(); i++) {

                JSONObject error = validationErrors.getJSONObject(i);
                String field = error.optString("field");
                String reason = error.optString("reason");
                errorMessages.put(field, reason);
            }
        }
        return  errorMessages;
    }
}
