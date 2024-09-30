package com.example.capstone.dto.Matching;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DetailInfo{
    private String title;
    private String roadAddress;
    private String mapx;
    private String mapy;

    public DetailInfo(String title, String roadAddress, String mapx, String mapy){
        this.mapx=mapx;
        this.mapy=mapy;
        this.title=title;
        this.roadAddress=roadAddress;
    }

    public DetailInfo(){

    }
}