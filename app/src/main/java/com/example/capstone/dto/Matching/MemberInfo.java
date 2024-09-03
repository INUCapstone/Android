package com.example.capstone.dto.Matching;

import lombok.Getter;

@Getter
public class MemberInfo {

    private String nickname;

    private boolean isReady;

    public MemberInfo(String nickname, boolean isReady){
        this.isReady=isReady;
        this.nickname=nickname;
    }
}
