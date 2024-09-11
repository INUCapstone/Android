package com.example.capstone.dto.Matching;

import lombok.Getter;

@Getter
public class MemberInfo {

    private String nickname;

    private String memberId;

    private boolean isReady;

    public MemberInfo(String nickname, String memberId,boolean isReady){
        this.isReady=isReady;
        this.memberId=memberId;
        this.nickname=nickname;
    }
}
