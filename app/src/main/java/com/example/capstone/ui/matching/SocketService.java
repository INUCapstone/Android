package com.example.capstone.ui.matching;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.capstone.common.TokenManager;
import com.example.capstone.dto.Matching.TaxiRoomRes;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class SocketService {

    private StompClient stompClient;
    private boolean isRunning = false;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final List<TaxiRoomRes> roomList;
    private final RoomAdapter adapter;
    private TokenManager tokenManager;
    private Disposable topicDisposable;

    public SocketService(List<TaxiRoomRes> roomList, RoomAdapter adapter, Context context) {
        this.roomList = roomList;
        this.adapter = adapter;
        this.tokenManager = new TokenManager(context);
    }

    // 소켓 연결을 시작하는 메소드
    public void startSocketConnection() {
        double latitude = 37.5665;  // 예시: 위도
        double longitude = 126.9780;  // 예시: 경도

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://3.37.76.51/ws");
        // 헤더에 토큰 추가
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + tokenManager.getAccessToken()));
        headers.add(new StompHeader("Latitude ", Double.toString(latitude)));   // 위도 추가
        headers.add(new StompHeader("Longitude ", Double.toString(longitude))); // 경도 추가

        // STOMP 클라이언트 연결
        stompClient.connect(headers);

        // STOMP 연결 시도
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    isRunning = true;
                    subscribeToRoomUpdates(); // 방 정보 업데이트 구독
                    break;
                case ERROR:
                    lifecycleEvent.getException().printStackTrace();
                    break;
                case CLOSED:
                    isRunning = false;
                    break;
            }
        });
    }

    // 메시지 수신을 대기하고 UI를 업데이트하는 메소드
    private void subscribeToRoomUpdates() {
        topicDisposable = stompClient.topic("/sub/rooms")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String finalMessage = topicMessage.getPayload();

                    uiHandler.post(() -> {
                        // JSON 문자열을 TaxiRoomRes 객체로 파싱
                        Gson gson = new Gson();
                        TaxiRoomRes roomData = gson.fromJson(finalMessage, TaxiRoomRes.class);

                        // RecyclerView 업데이트
                        changeRoomList(roomData);
                        adapter.notifyDataSetChanged();
                    });
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    // 소켓 연결을 종료하는 메소드
    public void stopSocketConnection() {
        if (isRunning) {
            stompClient.disconnect();
            if (topicDisposable != null) {
                topicDisposable.dispose();
            }
            isRunning = false;
        }
    }

    private void changeRoomList(TaxiRoomRes roomData){

        // 방이 없어지면
        if(roomData.isDelete()){

        }
        // 방이 출발했으면. 내가 있는 방이 출발한 경우다.
        else if(roomData.isStart()){

        }
        // 방이 변경됐으면
        else if(false){

        }
        // 방이 새롭게 추가된 방이면
        else{
            roomList.add(roomData);
        }

    }
}

