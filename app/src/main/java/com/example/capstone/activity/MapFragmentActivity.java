package com.example.capstone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.capstone.R;
import com.example.capstone.dto.Matching.PathInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.PathOverlay;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapFragmentActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        Gson gson = new Gson();
        Intent intents = getIntent();
        String valueData = intents.getStringExtra("pathInfo"); // 문자열 데이터
        Type type = new TypeToken<List<PathInfo>>() {}.getType();
        List<PathInfo> pathInfos = gson.fromJson(valueData, type);

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.pathMap);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.pathMap, mapFragment).commit();
        }

        mapFragment.getMapAsync(naverMap -> {
            UiSettings uiSettings = naverMap.getUiSettings();
            uiSettings.setZoomControlEnabled(true);
            uiSettings.setScrollGesturesEnabled(true);
            LocationOverlay locationOverlay = naverMap.getLocationOverlay();
            locationOverlay.setVisible(true);
            naverMap.setMapType(NaverMap.MapType.Basic);
            LatLng initialPosition = new LatLng(pathInfos.get(0).getY(), pathInfos.get(0).getX());
            locationOverlay.setPosition(initialPosition);

            // 지도 초기 중심을 LocationOverlay 위치로 설정
            naverMap.moveCamera(CameraUpdate.scrollTo(initialPosition));

            PathOverlay path = new PathOverlay();

            List<LatLng> list = new ArrayList<>();
            for(PathInfo pathInfo : pathInfos){
                list.add(new LatLng(pathInfo.getY(),pathInfo.getX()));
            }
            path.setCoords(list);
            path.setColor(Color.BLUE);
            path.setMap(naverMap);
            path.setWidth(40);

        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapFragmentActivity.this, MainActivity.class);

            intent.putExtra("isMatchingCome",intents.getStringExtra("isMatching"));
            boolean isEndMatching = Boolean.parseBoolean(intents.getStringExtra("isEndMatching"));
            if(isEndMatching){
                intent.putExtra("roomInfoData", intents.getStringExtra("roomInfo"));
                intent.putExtra("isEnded", "true");
            }
            intent.putExtra("currentRoomInfos",intents.getStringExtra("currentRoomInfo"));

            startActivity(intent);
            finish();
        });
    }
}