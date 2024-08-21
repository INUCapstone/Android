package com.example.capstone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginReq {

    private String email;
    private String password;

    public LoginReq(String email, String password){
        this.email=email;
        this.password=password;
    }
}
