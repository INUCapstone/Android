package com.example.capstone.ui.driver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone.R;
import com.example.capstone.databinding.FragmentDriverBinding;

public class DriverFragment extends Fragment {

    private FragmentDriverBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDriverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getDriverInfo().observe(getViewLifecycleOwner(), driverInfo -> {
            if (driverInfo != null) {
                TextView driverInfoLogo = root.findViewById(R.id.driverInfoLogo);
                TextView driverCarNum = root.findViewById(R.id.driverCarNum);
                TextView driverName = root.findViewById(R.id.driverName);
                TextView driverPhoneNum = root.findViewById(R.id.driverPhoneNum);
                TextView pickupTime = root.findViewById(R.id.pickupTime);

                driverInfoLogo.setText("기사 배정완료!");
                driverCarNum.setText("차 번호            : "+driverInfo.getCarNumber());
                driverName.setText("기사이름         : "+driverInfo.getName());
                driverPhoneNum.setText("기사 번호        : "+driverInfo.getPhoneNumber());
                pickupTime.setText("픽업 소요시간 : "+driverInfo.getPickupTime()+"분");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}