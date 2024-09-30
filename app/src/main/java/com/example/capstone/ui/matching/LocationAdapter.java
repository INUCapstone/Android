package com.example.capstone.ui.matching;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.dto.Matching.DetailInfo;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private final List<DetailInfo> locationInfoList;

    public LocationAdapter(List<DetailInfo> locationInfoList) {
        this.locationInfoList = locationInfoList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);  // Replace with actual layout file
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {

        DetailInfo detailInfo = locationInfoList.get(position);

        String title = detailInfo.getTitle();
        holder.titleTextView.setText(title.replaceAll("<[^>]*>", ""));
        holder.addressTextView.setText(detailInfo.getRoadAddress());
    }

    @Override
    public int getItemCount() {
        return locationInfoList.size();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView addressTextView;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            addressTextView = itemView.findViewById(R.id.roadAddress);
        }
    }
}
