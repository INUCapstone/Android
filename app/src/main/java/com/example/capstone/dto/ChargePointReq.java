package com.example.capstone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChargePointReq {

    private Long point;

    public ChargePointReq(Long point){
        this.point=point;
    }
}
