package com.example.capstone.ui.matching;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.activity.MapFragmentActivity;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.service.MemberService;
import com.example.capstone.api.service.NaverService;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.databinding.FragmentMatchingBinding;
import com.example.capstone.dto.Matching.DetailInfo;
import com.example.capstone.dto.Matching.MemberInfo;
import com.example.capstone.dto.Matching.PathInfo;
import com.example.capstone.dto.Matching.TaxiRoomRes;
import com.example.capstone.ui.driver.SharedViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class MatchingFragment extends Fragment {

    private FragmentMatchingBinding binding;
    private SocketService socketService;
    private List<TaxiRoomRes> roomList;
    private List<DetailInfo> locationInfoList;
    private RoomAdapter adapter;
    private LocationAdapter locationAdapter;
    private Button startMatchingButton, stopMatchingButton, findLocationButton,taxiOutButton;
    private EditText targetLocation;
    private TextView logoMatchingSuccess;
    private StompClient stompClient;
    private RecyclerView locateRecycler,roomRecycler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMatchingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        locateRecycler =   root.findViewById(R.id.recycler_view_locate);
        roomRecycler =   root.findViewById(R.id.recycler_view);

        targetLocation = root.findViewById(R.id.targetLocation);
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://3.37.76.51:80/ws");

        SharedMatchingModel viewModel = new ViewModelProvider(requireActivity()).get(SharedMatchingModel.class);

        locationInfoList = new ArrayList<>();
        locationAdapter = new LocationAdapter(locationInfoList);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        RecyclerView locationRecyclerView = root.findViewById(R.id.recycler_view_locate);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locationRecyclerView.setAdapter(locationAdapter);

        startMatchingButton = root.findViewById(R.id.startMatchingButton);
        stopMatchingButton = root.findViewById(R.id.stopMatchingButton);
        findLocationButton = root.findViewById(R.id.findLocationButton);
        taxiOutButton = root.findViewById(R.id.taxiOutButton);
        logoMatchingSuccess = root.findViewById(R.id.logoMatchingSuccess);

        roomList = new ArrayList<>();
        adapter = RoomAdapterSingleton.getInstance(roomList,stompClient, getContext());
        socketService = SocketSingleton.getInstance(roomList,adapter,getContext(),stompClient);
        locationAdapter.setSocketService(socketService);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        // 방 매칭 성공인 경우
        viewModel.getRoomInfo().observe(getViewLifecycleOwner(),  roomData-> {
            if (roomData != null) {
                startMatchingButton = root.findViewById(R.id.startMatchingButton);
                stopMatchingButton = root.findViewById(R.id.stopMatchingButton);
                findLocationButton = root.findViewById(R.id.findLocationButton);
                taxiOutButton = root.findViewById(R.id.taxiOutButton);
                locateRecycler = root.findViewById(R.id.recycler_view_locate);
                roomRecycler = root.findViewById(R.id.recycler_view);
                TextView logoMatchingSuccess = root.findViewById(R.id.logoMatchingSuccess);

                // 매칭페이지 전부 안보이게하고 선택된 방정보 보여주기, 택시하차버튼 활성화
                roomList.clear();
                startMatchingButton.setVisibility(View.GONE);
                stopMatchingButton.setVisibility(View.GONE);
                findLocationButton.setVisibility(View.GONE);
                roomRecycler.setVisibility(View.GONE);
                locateRecycler.setVisibility(View.GONE);
                targetLocation.setEnabled(false);

                LinearLayout linearLayout = root.findViewById(R.id.matchedRoomInfo);
                linearLayout.setVisibility(View.VISIBLE);
                TextView explainLogo = root.findViewById(R.id.explaineLogo);
                explainLogo.setVisibility(View.VISIBLE);
                taxiOutButton.setVisibility(View.VISIBLE);
                logoMatchingSuccess.setVisibility(View.VISIBLE);

                // 방정보 갱신
                TextView matchedTime = root.findViewById(R.id.matchedTime);
                TextView matchedCharge = root.findViewById(R.id.matchedCharge);
                TextView matchedCurrentMemberCnt = root.findViewById(R.id.matchedCurrentMemberCnt);
                TextView matchedMemberList = root.findViewById(R.id.matchedMemberList);
                ImageButton pathButton = root.findViewById(R.id.showMatchedPathButton);
                matchedTime.setText("예상시간 : " + roomData.getTime() + "분");
                matchedCharge.setText("예상금액 : " + roomData.getCharge() + "원");
                matchedCurrentMemberCnt.setText("정원 : " + roomData.getCurrentMemberCnt() + "/4");

                StringBuilder memberNames = new StringBuilder();
                for (MemberInfo member : roomData.getMemberList()) {
                    if (memberNames.length() > 0) {
                        memberNames.append(member.getNickname()).append(", ");
                    }
                }
                matchedMemberList.setText(memberNames.toString());
                pathButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), MapFragmentActivity.class);
                        List<PathInfo> pathInfos = roomData.getPathInfoList();
                        Gson gson = new Gson();
                        intent.putExtra("pathInfo", gson.toJson(pathInfos));
                        intent.putExtra("isMatching","false");
                        intent.putExtra("isEndMatching","true");
                        intent.putExtra("roomInfo", gson.toJson(roomData));
                        Log.d("버튼 리스너 클릭됨", "12312312312342@@@");
                        getContext().startActivity(intent);
                    }
                });
            }
        });

        // 방애 매칭 성공인 경우 (초기화)
        Intent comeIntent = getActivity().getIntent();
        boolean isEnded = Boolean.parseBoolean(comeIntent.getStringExtra("isEnded"));
        if(isEnded){
            Gson gson = new Gson();
            getActivity().getIntent().removeExtra("isEnded");

            Log.d("방정보", comeIntent.getStringExtra("roomInfoData"));
            TaxiRoomRes roomInfo = gson.fromJson(comeIntent.getStringExtra("roomInfoData"), TaxiRoomRes.class);
            viewModel.setRoomInfo(roomInfo);
            startMatchingButton = root.findViewById(R.id.startMatchingButton);
            stopMatchingButton = root.findViewById(R.id.stopMatchingButton);
            findLocationButton = root.findViewById(R.id.findLocationButton);
            taxiOutButton = root.findViewById(R.id.taxiOutButton);
            locateRecycler = root.findViewById(R.id.recycler_view_locate);
            roomRecycler = root.findViewById(R.id.recycler_view);
            TextView logoMatchingSuccess = root.findViewById(R.id.logoMatchingSuccess);

            // 매칭페이지 전부 안보이게하고 선택된 방정보 보여주기, 택시하차버튼 활성화
            roomList.clear();
            startMatchingButton.setVisibility(View.GONE);
            stopMatchingButton.setVisibility(View.GONE);
            findLocationButton.setVisibility(View.GONE);
            roomRecycler.setVisibility(View.GONE);
            locateRecycler.setVisibility(View.GONE);
            targetLocation.setEnabled(false);

            LinearLayout linearLayout = root.findViewById(R.id.matchedRoomInfo);
            linearLayout.setVisibility(View.VISIBLE);
            TextView explainLogo = root.findViewById(R.id.explaineLogo);
            explainLogo.setVisibility(View.VISIBLE);
            taxiOutButton.setVisibility(View.VISIBLE);
            logoMatchingSuccess.setVisibility(View.VISIBLE);

            // 방정보 갱신
            TextView matchedTime = root.findViewById(R.id.matchedTime);
            TextView matchedCharge = root.findViewById(R.id.matchedCharge);
            TextView matchedCurrentMemberCnt = root.findViewById(R.id.matchedCurrentMemberCnt);
            TextView matchedMemberList = root.findViewById(R.id.matchedMemberList);
            ImageButton pathButton = root.findViewById(R.id.showMatchedPathButton);
            matchedTime.setText("예상시간 : " + roomInfo.getTime() + "분");
            matchedCharge.setText("예상금액 : " + roomInfo.getCharge() + "원");
            matchedCurrentMemberCnt.setText("정원 : " + roomInfo.getCurrentMemberCnt() + "/4");

            StringBuilder memberNames = new StringBuilder();
            for (MemberInfo member : roomInfo.getMemberList()) {
                if (memberNames.length() > 0) {
                    memberNames.append(member.getNickname()).append(", ");
                }
            }
            matchedMemberList.setText(memberNames.toString());
            pathButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), MapFragmentActivity.class);
                    List<PathInfo> pathInfos = roomInfo.getPathInfoList();
                    Gson gson = new Gson();
                    intent.putExtra("pathInfo", gson.toJson(pathInfos));
                    intent.putExtra("isMatching","false");

                    startActivity(intent);
                }
            });
        }

        // 방 매칭중인 경우
        viewModel.getIsMatching().observe(getViewLifecycleOwner(), isMatching -> {

            if(isMatching != null && isMatching){
                startMatchingButton.setVisibility(View.GONE);
                stopMatchingButton.setVisibility(View.VISIBLE);
                findLocationButton.setVisibility(View.GONE);
                locateRecycler.setVisibility(View.GONE);
                roomRecycler.setVisibility(View.VISIBLE);
                targetLocation.setEnabled(false);
            }
        });

        // 방이 매칭중인경우 (초기화)
        if (comeIntent != null) {
            Boolean isMatchingsd = Boolean.parseBoolean(comeIntent.getStringExtra("isMatchingCome"));
            if(isMatchingsd != null && isMatchingsd){
                getActivity().getIntent().removeExtra("isMatchingCome");
                Gson gson = new Gson();

                Type type = new TypeToken<List<TaxiRoomRes>>() {}.getType();
                List<TaxiRoomRes> roomDataList = gson.fromJson(comeIntent.getStringExtra("currentRoomInfos"), type);
                getActivity().getIntent().removeExtra("currentRoomInfos");
                socketService.updateRoom(roomDataList);

                Log.d("버그해결","1@@");
                Log.d("버그해결", String.valueOf(roomDataList.size()));

                viewModel.setMatching();
                //Log.d("버그해결","4");
                startMatchingButton.setVisibility(View.GONE);
                stopMatchingButton.setVisibility(View.VISIBLE);
                findLocationButton.setVisibility(View.GONE);
                locateRecycler.setVisibility(View.GONE);
                roomRecycler.setVisibility(View.VISIBLE);
                targetLocation.setEnabled(false);
            }
        }

        // 택시하차버튼 클릭 시
        taxiOutButton.setOnClickListener(v -> {
            LinearLayout linearLayout = root.findViewById(R.id.matchedRoomInfo);
            linearLayout.setVisibility(View.GONE);
            TextView explainLogo = root.findViewById(R.id.explaineLogo);
            explainLogo.setVisibility(View.GONE);

            startMatchingButton.setVisibility(View.VISIBLE);
            stopMatchingButton.setVisibility(View.GONE);
            findLocationButton.setVisibility(View.VISIBLE);
            locateRecycler.setVisibility(View.VISIBLE);
            roomRecycler.setVisibility(View.GONE);
            targetLocation.setEnabled(true);
            taxiOutButton.setVisibility(View.GONE);
            logoMatchingSuccess.setVisibility(View.GONE);
            SharedViewModel driverModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            driverModel.getDriverInfo().observe(getViewLifecycleOwner(),  driverInfo-> {
              if(driverInfo != null){
                  sendDriverArrive(driverInfo.getDriverId());
              }
            });

            driverModel.reSet();
            viewModel.setNoMatching();
            viewModel.reSetRoomInfo();
            viewModel.reSetRoomList();

        });

        // 매칭 시작 버튼 클릭 시
        startMatchingButton.setOnClickListener(v -> {
            socketService.startSocketConnection();
            startMatchingButton.setVisibility(View.GONE);
            stopMatchingButton.setVisibility(View.VISIBLE);
            findLocationButton.setVisibility(View.GONE);
            locateRecycler.setVisibility(View.GONE);
            roomRecycler.setVisibility(View.VISIBLE);
            targetLocation.setEnabled(false);

        });

        // 매칭 종료 버튼 클릭 시
        stopMatchingButton.setOnClickListener(v -> {

            socketService.stopSocketConnection();
            startMatchingButton.setVisibility(View.VISIBLE);
            stopMatchingButton.setVisibility(View.GONE);
            findLocationButton.setVisibility(View.VISIBLE);
            locateRecycler.setVisibility(View.VISIBLE);
            roomRecycler.setVisibility(View.GONE);
            targetLocation.setEnabled(true);
            viewModel.setNoMatching();
            viewModel.reSetRoomList();
            viewModel.reSetRoomInfo();
        });

        // 도착지 검색 버튼 클릭 시
        findLocationButton.setOnClickListener(v -> {
            NaverService naverService = new NaverService(getActivity(), locationInfoList,locationAdapter);
            naverService.getLocation(String.valueOf(targetLocation.getText()), new RepositoryCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {
                }
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void sendDriverArrive(Long driverId){
        MemberService memberService = new MemberService(getActivity());
        memberService.driverArrive(driverId,new RepositoryCallback(){

            @Override
            public void onSuccess(String message) {
                Log.d("택시기사 정보변경","요청완료");
            }

            @Override
            public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {

            }
        });
    }
}
