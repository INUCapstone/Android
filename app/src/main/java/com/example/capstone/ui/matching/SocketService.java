package com.example.capstone.ui.matching;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.capstone.R;
import com.example.capstone.common.TokenManager;
import com.example.capstone.dto.Matching.DetailInfo;
import com.example.capstone.dto.Matching.MemberInfo;
import com.example.capstone.dto.Matching.PathInfo;
import com.example.capstone.dto.Matching.TaxiRoomRes;
import com.example.capstone.dto.Matching.WaitingMemberReqDto;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class SocketService {

    private double latitude = 0.0, longitude = 0.0;
    private StompClient stompClient;
    private boolean isRunning = false;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final List<TaxiRoomRes> roomList;
    private final RoomAdapter adapter;
    private TokenManager tokenManager;
    private Disposable topicDisposable;
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private EditText targetLocation;
    private String endX;
    private String endY;

    public SocketService(List<TaxiRoomRes> roomList, RoomAdapter adapter, Context context) {
        this.roomList = roomList;
        this.adapter = adapter;
        this.tokenManager = new TokenManager(context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.context=context;
        targetLocation = ((Activity) context).findViewById(R.id.targetLocation);
    }

    // 소켓 연결을 시작하는 메소드
    public void startSocketConnection() {
        getLocation(); // 위치 정보를 가져옴

        // 위도 y
        String latitudes = Double.toString(latitude);
        // 경도 x
        String longitudes = Double.toString(longitude);
        WaitingMemberReqDto data = WaitingMemberReqDto.builder()
                .startX(longitudes)
                .startY(latitudes)
                .endX(endX)
                .endY(endY)
                .build();


        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://3.37.76.51:80/ws");

        // 헤더에 토큰 추가
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", tokenManager.getAccessToken()));

        // STOMP 연결 시도
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    isRunning = true;
                    subscribeToRoomUpdates(); // 방 정보 업데이트 구독
                    break;
                case ERROR:
                    Log.d("에러",lifecycleEvent.getException().getMessage());
                    lifecycleEvent.getException().printStackTrace();
                    break;
                case CLOSED:
                    isRunning = false;
                    break;
            }
        });

        // STOMP 클라이언트 연결
        stompClient.connect(headers);

        Gson gson = new Gson();

        stompClient.send("/pub/match/" + tokenManager.getMemberId(), gson.toJson(data))
                .subscribe(() -> Log.d("매칭 요청", "매칭 시작 요청을 보냈습니다."),
                        throwable -> Log.d("매칭 요청 실패", throwable.getMessage()));

    }

    // 메시지 수신을 대기하고 UI를 업데이트하는 메소드
    private void subscribeToRoomUpdates() {

        topicDisposable = stompClient.topic("/sub/member/"+ tokenManager.getMemberId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {

                    String finalMessage = topicMessage.getPayload();
                    uiHandler.post(() -> {
                        // JSON 문자열을 TaxiRoomRes 객체로 파싱
                        Gson gson = new Gson();

                        Type type = new TypeToken<List<TaxiRoomRes>>() {}.getType();
                        List<TaxiRoomRes> roomDataList = gson.fromJson(finalMessage, type);

                        // RecyclerView 업데이트
                        for(TaxiRoomRes res : roomDataList){
                            changeRoomList(res);
                        }
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

    private void changeRoomList(TaxiRoomRes roomData) {
        boolean isNew = true;
        int index = 0;

        // 방 리스트에 존재하는 방인지 확인
        for (TaxiRoomRes room : roomList) {
            if (room.getRoomId() == roomData.getRoomId()) {
                isNew = false;
                break;
            }
            index++;
        }

        // 방이 새롭게 추가된 방이면
        if (isNew) {
            roomList.add(roomData);
        }
        // 방이 없어지면, roomList에서 해당 방을 삭제한다.
        else if (roomData.isDelete()) {
            roomList.remove(index);
        }
        // 방의 모든 인원이 레디하여 출발한 경우, 매칭구독 끊고 기사구독을 연결
        else if (roomData.isStart()) {
            // 매칭 구독 끊기

            // 매칭페이지에 매칭된 방 정보를 보여주기

            // 기사정보 페이지로 이동

            // 기사구독 연결(기사정보를 받아오면 기사정보 보여주고 소켓연결 끊기)

        }
        // 방이 변경됐으면(ex.누가 레디를 하거나 푼 경우), 해당 방의 정보를 변경해준다.
        else {
            roomList.set(index, roomData);
        }
    }

    private void getLocation() {

        LocationRequest locationRequest = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 10000) // 10초마다 요청
                .setWaitForAccurateLocation(true) // 보다 정확한 위치를 기다리도록 설정
                .setMinUpdateIntervalMillis(5000) // 5초
                .build();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            longitude = locationResult.getLastLocation().getLongitude();
            latitude = locationResult.getLastLocation().getLatitude();
            // 지속적으로 위치를 가져올 필요가 없어서 더이상 가져오는것을 막음
            fusedLocationClient.removeLocationUpdates(locationCallback);

        }
    };

    public void setEndLocationInfo(DetailInfo clickedDetailInfo){

        this.endX = clickedDetailInfo.getMapx();
        this.endY = clickedDetailInfo.getMapy();
        String title = clickedDetailInfo.getTitle();
        targetLocation = ((Activity) context).findViewById(R.id.targetLocation);
        targetLocation.setText(title.replaceAll("<[^>]*>", ""));
    }
}

