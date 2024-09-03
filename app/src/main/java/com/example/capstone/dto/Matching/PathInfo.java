package com.example.capstone.dto.Matching;

import lombok.Getter;

@Getter
public class PathInfo {

    private Long x;
    private Long y;

    public PathInfo(Long x, Long y){
        this.x=x;
        this.y=y;
    }
}
