package com.example.capstone.dto.Matching;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitingMemberReqDto {

    private String startX;
    private String startY;
    private String endX;
    private String endY;

    @Builder
    public WaitingMemberReqDto(String startX, String startY, String endX, String endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

}