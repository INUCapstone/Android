package com.example.capstone.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DriverInfo {

    private Long driverId;
    private String phoneNumber;
    private String carNumber;
    private String name;
    private Integer pickupTime;

    public DriverInfo(){

    }

    public DriverInfo(Long driverId, String phoneNumber, String carNumber, String name, Integer pickupTime){
        this.carNumber = carNumber;
        this.driverId=driverId;
        this.name= name;
        this.phoneNumber = phoneNumber;
        this.pickupTime=pickupTime;
    }

}
