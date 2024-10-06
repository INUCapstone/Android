package com.example.capstone.dto.Matching;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathInfo {

    private Double x;
    private Double y;

    public PathInfo(Double x, Double y){
        this.x=x;
        this.y=y;
    }

    public PathInfo(){

    }
}
