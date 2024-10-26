package com.example.capstone.ui.matching;

import android.content.Context;

import com.example.capstone.dto.Matching.TaxiRoomRes;

import java.util.List;

import ua.naiksoftware.stomp.StompClient;

public class SocketSingleton {

    private static SocketService socketService = null;


    public static SocketService getInstance(List<TaxiRoomRes> roomList, RoomAdapter adapter, Context context, StompClient stompClient) {
        if (socketService == null) {
            socketService = new SocketService(roomList,adapter,context,stompClient);
        }
        return socketService;
    }
}
