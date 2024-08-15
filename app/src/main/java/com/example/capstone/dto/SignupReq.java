package com.example.capstone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupReq {

    private String email;
    private String password;
    private String passwordRepeat;
    private String nickname;
    private String phoneNumber;

    @Builder
    public SignupReq(String email, String password, String passwordRepeat, String nickname, String phoneNumber){
        this.email=email;
        this.password=password;
        this.passwordRepeat=passwordRepeat;
        this.nickname=nickname;
        this.phoneNumber=phoneNumber;
    }
}
