package com.example.capstone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupReq {

    private String email;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String phoneNumber;

    @Builder
    public SignupReq(String email, String password, String confirmPassword, String nickname, String phoneNumber){
        this.email=email;
        this.password=password;
        this.confirmPassword = confirmPassword;
        this.nickname=nickname;
        this.phoneNumber=phoneNumber;
    }
}
