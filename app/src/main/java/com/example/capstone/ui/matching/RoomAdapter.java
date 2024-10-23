package com.example.capstone.ui.matching;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.common.TokenManager;
import com.example.capstone.dto.Matching.MemberInfo;
import com.example.capstone.dto.Matching.TaxiRoomRes;

import java.util.List;

import ua.naiksoftware.stomp.StompClient;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final List<TaxiRoomRes> roomList;
    private StompClient stompClient;
    private TokenManager tokenManager;

    public RoomAdapter(List<TaxiRoomRes> roomList, StompClient stompClient, Context context) {
        this.roomList = roomList;
        this.stompClient = stompClient;
        this.tokenManager = new TokenManager(context);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_taxi_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        TaxiRoomRes roomData = roomList.get(position);
        holder.timeTextView.setText("예상시간 : " + roomData.getTime() + "분");
        holder.chargeTextView.setText("예상금액 : " + roomData.getCharge() + "원");
        holder.currentMemberCntTextView.setText("정원 : " + roomData.getCurrentMemberCnt() + "/4");

        StringBuilder memberNames = new StringBuilder();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        for (MemberInfo member : roomData.getMemberList()) {
            if (memberNames.length() > 0) {
                memberNames.append(", ");
                spannableStringBuilder.append(", "); // 구분 기호도 spannable에 추가
            }

            memberNames.append(member.getNickname());

            // 색상을 설정할 위치 계산
            int start = spannableStringBuilder.length();
            spannableStringBuilder.append(member.getNickname());
            int end = spannableStringBuilder.length();

            // isReady가 true인 경우 초록색으로 설정
            if (member.isReady()) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.GREEN), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        // 최종적으로 spannableStringBuilder를 텍스트뷰에 설정
        holder.memberListTextView.setText(spannableStringBuilder);

        holder.readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stompClient.send("/pub/ready/"+roomData.getRoomId()+"/"+tokenManager.getMemberId(), null)
                        .subscribe(() -> Log.d("매칭 요청", "매칭 시작 요청을 보냈습니다."),
                                throwable -> Log.d("매칭 요청 실패", throwable.getMessage()));
            }
        });

        holder.showPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("버튼","버튼 클릭됨");
                // 지도 표시하는 로직 구현
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, chargeTextView, currentMemberCntTextView, memberListTextView;
        Button readyButton;
        ImageButton showPathButton;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.time);
            chargeTextView = itemView.findViewById(R.id.charge);
            currentMemberCntTextView = itemView.findViewById(R.id.currentMemberCnt);
            memberListTextView = itemView.findViewById(R.id.memberList);
            showPathButton = itemView.findViewById(R.id.showPathButton);
            readyButton = itemView.findViewById(R.id.readyButton);
        }
    }
}

