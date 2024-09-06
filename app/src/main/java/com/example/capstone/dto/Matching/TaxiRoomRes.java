package com.example.capstone.dto.Matching;

import java.util.List;

import lombok.Getter;

@Getter
public class TaxiRoomRes {

    private Long roomId;

    // 현재 방에 잇는 맴버 수
    private Long currentMemberCnt;

    // 택시 경로 정보. 꺾이는 지점의 좌표값리스트
    private List<PathInfo> pathInfoList;

    // 소요시간
    private Long time;

    // 소요비용
    private Long charge;

    // 참여자 닉네임 리스트
    private List<MemberInfo> memberList;

    public TaxiRoomRes(Long roomId, Long currentMemberCnt, List<PathInfo> pathInfoList, Long time, Long charge, List<MemberInfo> memberList){
        this.charge=charge;
        this.roomId =roomId;
        this.memberList=memberList;
        this.pathInfoList=pathInfoList;
        this.currentMemberCnt=currentMemberCnt;
        this.time=time;
    }
}
