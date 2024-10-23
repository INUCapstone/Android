package com.example.capstone.ui.matching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.service.NaverService;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.databinding.FragmentMatchingBinding;
import com.example.capstone.dto.Matching.DetailInfo;
import com.example.capstone.dto.Matching.TaxiRoomRes;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMatchingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        targetLocation = root.findViewById(R.id.targetLocation);

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://3.37.76.51:80/ws");

        roomList = new ArrayList<>();
        adapter = new RoomAdapter(roomList,stompClient, getContext());
        locationInfoList = new ArrayList<>();
        locationAdapter = new LocationAdapter(locationInfoList);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        RecyclerView locationRecyclerView = root.findViewById(R.id.recycler_view_locate);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locationRecyclerView.setAdapter(locationAdapter);

        startMatchingButton = root.findViewById(R.id.startMatchingButton);
        stopMatchingButton = root.findViewById(R.id.stopMatchingButton);
        findLocationButton = root.findViewById(R.id.findLocationButton);
        taxiOutButton = root.findViewById(R.id.taxiOutButton);
        logoMatchingSuccess = root.findViewById(R.id.logoMatchingSuccess);

        socketService = new SocketService(roomList,adapter,getContext(),stompClient);
        locationAdapter.setSocketService(socketService);

        RecyclerView locateRecycler =   root.findViewById(R.id.recycler_view_locate);
        RecyclerView roomRecycler =   root.findViewById(R.id.recycler_view);

        taxiOutButton.setOnClickListener(v -> {
            startMatchingButton.setVisibility(View.VISIBLE);
            stopMatchingButton.setVisibility(View.GONE);
            findLocationButton.setVisibility(View.VISIBLE);
            locateRecycler.setVisibility(View.VISIBLE);
            roomRecycler.setVisibility(View.GONE);
            targetLocation.setEnabled(true);
            taxiOutButton.setVisibility(View.GONE);
            logoMatchingSuccess.setVisibility(View.GONE);
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

    public void clickLocation(){

    }
}
