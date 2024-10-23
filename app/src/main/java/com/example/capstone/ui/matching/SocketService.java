package com.example.capstone.ui.matching;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

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

import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.ProjCoordinate;

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
    private Button startMatchingButton, stopMatchingButton, findLocationButton, taxiOutButton;

    public SocketService(List<TaxiRoomRes> roomList, RoomAdapter adapter, Context context, StompClient stompClient) {
        this.roomList = roomList;
        this.adapter = adapter;
        this.tokenManager = new TokenManager(context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.context=context;
        targetLocation = ((Activity) context).findViewById(R.id.targetLocation);
        this.stompClient = stompClient;
    }

    // 소켓 연결을 시작하는 메소드
    public void startSocketConnection() {
        getLocation(); // 위치 정보를 가져옴

        // 위도 y
        String latitudes = "37.377";
        // 경도 x
        String longitudes = "126.633";
        WaitingMemberReqDto data = WaitingMemberReqDto.builder()
                .startX(longitudes)
                .startY(latitudes)
                .endX(endX)
                .endY(endY)
                .build();

        Log.d("위치", "출발지"+longitudes+" "+latitudes);
        Log.d("위치", "도착지"+endX+" "+endY);

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

                        roomList.clear();
                        Log.d("매칭", Integer.toString(roomDataList.size()));
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
        if(roomData.isStart()){
            startMatchingButton = ((Activity) context).findViewById(R.id.startMatchingButton);
            stopMatchingButton = ((Activity) context).findViewById(R.id.stopMatchingButton);
            findLocationButton = ((Activity) context).findViewById(R.id.findLocationButton);
            taxiOutButton = ((Activity) context).findViewById(R.id.taxiOutButton);
            RecyclerView locateRecycler = ((Activity) context).findViewById(R.id.recycler_view_locate);
            RecyclerView roomRecycler = ((Activity) context).findViewById(R.id.recycler_view);
            TextView logoMatchingSuccess = ((Activity) context).findViewById(R.id.logoMatchingSuccess);
            // 매칭구독 끊기
            topicDisposable.dispose();

            // 매칭페이지 전부 안보이게하고 선택된 방정보 보여주기, 택시하차버튼 활성화
            roomList.clear();
            roomList.add(roomData);
            startMatchingButton.setVisibility(View.GONE);
            stopMatchingButton.setVisibility(View.GONE);
            findLocationButton.setVisibility(View.GONE);
            locateRecycler.setVisibility(View.GONE);
            roomRecycler.setVisibility(View.VISIBLE);
            targetLocation.setEnabled(false);
            taxiOutButton.setVisibility(View.VISIBLE);
            logoMatchingSuccess.setVisibility(View.VISIBLE);

            // 택시기사페이지로 이동

        }
        else{
            roomList.add(roomData);
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

        // 변환된 GPS 좌표를 endX, endY에 설정
        this.endX = "126.6556559997"; // 경도 (longitude)
        this.endY = "37.38118527"; // 위도 (latitude)

        String title = clickedDetailInfo.getTitle();
        targetLocation = ((Activity) context).findViewById(R.id.targetLocation);
        targetLocation.setText(title.replaceAll("<[^>]*>", ""));
    }

    public double[] convertTMtoWGS84(double tmX, double tmY) {
        // Proj4j 설정
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem tmCrs = crsFactory.createFromName("EPSG:5178");  // 네이버 TM 좌표계 (EPSG:5179)
        CoordinateReferenceSystem wgs84Crs = crsFactory.createFromName("EPSG:4326");  // WGS84 좌표계 (EPSG:4326)

        // 좌표 변환 설정
        CoordinateTransform transform = new BasicCoordinateTransform(tmCrs, wgs84Crs);

        // 변환할 좌표 생성
        ProjCoordinate inputCoord = new ProjCoordinate(tmX, tmY);
        ProjCoordinate outputCoord = new ProjCoordinate();

        // 좌표 변환 실행
        transform.transform(inputCoord, outputCoord);

        // 변환된 좌표 반환 (위도, 경도)
        return new double[]{outputCoord.y, outputCoord.x};  // WGS84 좌표는 (위도, 경도) 순서
    }
}

