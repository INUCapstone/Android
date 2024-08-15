package com.example.capstone.api;

public interface RepositoryCallback {
    void onSuccess(String message);
    void onFailure(String errorMessage);
}
