package com.example.capstone.ui.matching;

import android.os.Handler;
import android.os.Looper;

import com.example.capstone.dto.Matching.MemberInfo;
import com.example.capstone.dto.Matching.PathInfo;
import com.example.capstone.dto.Matching.TaxiRoomRes;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketService {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean isRunning = false;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final List<TaxiRoomRes> roomList;
    private final RoomAdapter adapter;

    public SocketService(List<TaxiRoomRes> roomList, RoomAdapter adapter) {
        this.roomList = roomList;
        this.adapter = adapter;
    }

    // 소켓 연결을 시작하는 메소드
    public void startSocketConnection() {

        List<PathInfo> pathInfoList = new ArrayList<>();
        List<MemberInfo> memberInfoList = new ArrayList<>();
        pathInfoList.add(new PathInfo(10L,20L));
        memberInfoList.add(new MemberInfo("박세용",true));
        memberInfoList.add(new MemberInfo("김강욱",false));
        TaxiRoomRes roomData = new TaxiRoomRes(1L,3l,pathInfoList,30L,20L,memberInfoList);

        roomList.add(roomData);
        adapter.notifyDataSetChanged();

        /*
        new Thread(() -> {
            try {
                socket = new Socket("3.37.76.51",80); // 서버 주소와 포트번호를 넣으세요
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isRunning = true;

                listenForMessages(); // 메시지 수신 대기

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        */
    }

    // 메시지 수신을 대기하고 UI를 업데이트하는 메소드
    private void listenForMessages() {
        try {
            String message;
            while (isRunning && (message = reader.readLine()) != null) {
                String finalMessage = message;

                uiHandler.post(() -> {
                    // JSON 문자열을 TaxiRoomRes 객체로 파싱
                    Gson gson = new Gson();
                    TaxiRoomRes roomData = gson.fromJson(finalMessage, TaxiRoomRes.class);

                    // RecyclerView를 업데이트하기 위해 기존 데이터 리스트에 새로 받은 데이터 추가
                    roomList.add(roomData);
                    adapter.notifyDataSetChanged();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 소켓 연결을 종료하는 메소드
    public void stopSocketConnection() {
        isRunning = false;
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

