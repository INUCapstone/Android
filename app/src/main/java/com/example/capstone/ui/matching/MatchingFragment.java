package com.example.capstone.ui.matching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.databinding.FragmentMatchingBinding;
import com.example.capstone.dto.Matching.TaxiRoomRes;

import java.util.ArrayList;
import java.util.List;


public class MatchingFragment extends Fragment {

    private FragmentMatchingBinding binding;
    private SocketService socketService;
    private List<TaxiRoomRes> roomList;
    private RoomAdapter adapter;
    private Button startMatchingButton, stopMatchingButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMatchingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        roomList = new ArrayList<>();
        adapter = new RoomAdapter(roomList);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        startMatchingButton = root.findViewById(R.id.startMatchingButton);
        stopMatchingButton = root.findViewById(R.id.stopMatchingButton);

        socketService = new SocketService(roomList, adapter);

        // 매칭 시작 버튼 클릭 시
        startMatchingButton.setOnClickListener(v -> {
            socketService.startSocketConnection();
            startMatchingButton.setVisibility(View.GONE);
            stopMatchingButton.setVisibility(View.VISIBLE);
        });

        // 매칭 종료 버튼 클릭 시
        stopMatchingButton.setOnClickListener(v -> {
            socketService.stopSocketConnection();
            startMatchingButton.setVisibility(View.VISIBLE);
            stopMatchingButton.setVisibility(View.GONE);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
