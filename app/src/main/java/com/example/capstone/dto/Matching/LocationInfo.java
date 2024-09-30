package com.example.capstone.dto.Matching;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LocationInfo {

    private Long total;
    private List<DetailInfo> items;

    public LocationInfo(Long total, List<DetailInfo> items){
        this.total = total;
        this.items = items;
    }

    public LocationInfo(){

    }

}