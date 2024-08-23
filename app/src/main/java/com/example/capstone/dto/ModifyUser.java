package com.example.capstone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ModifyUser {

    private String nickname;
    private String password;
    private String confirmPassword;

    public ModifyUser(String nickname, String password, String confirmPassword){
        this.nickname=nickname;
        this.password=password;
        this.confirmPassword=confirmPassword;
    }
}
