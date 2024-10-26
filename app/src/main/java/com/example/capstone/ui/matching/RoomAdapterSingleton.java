package com.example.capstone.ui.matching;

import android.content.Context;

import com.example.capstone.dto.Matching.TaxiRoomRes;

import java.util.List;

import ua.naiksoftware.stomp.StompClient;

public class RoomAdapterSingleton {

    private static RoomAdapter roomAdapter = null;


    public static RoomAdapter getInstance(List<TaxiRoomRes> roomList, StompClient stompClient, Context context) {
        if (roomAdapter == null) {
            roomAdapter = new RoomAdapter(roomList,stompClient, context);
        }
        return roomAdapter;
    }
}
