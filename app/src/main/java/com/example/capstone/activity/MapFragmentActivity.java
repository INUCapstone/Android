package com.example.capstone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.capstone.R;
import com.example.capstone.dto.Matching.PathInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.UiSettings;

import java.lang.reflect.Type;
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
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapFragmentActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        });
    }
}