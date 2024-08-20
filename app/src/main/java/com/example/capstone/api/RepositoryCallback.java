package com.example.capstone.api;

import com.example.capstone.common.ExceptionCode;

import java.util.Map;

public interface RepositoryCallback {
    void onSuccess(String message);
    void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages);
}
