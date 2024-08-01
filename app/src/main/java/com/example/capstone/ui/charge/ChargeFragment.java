package com.example.capstone.ui.charge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone.databinding.FragmentChargeBinding;

public class ChargeFragment extends Fragment {

    private FragmentChargeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChargeViewModel chargeViewModel =
                new ViewModelProvider(this).get(ChargeViewModel.class);

        binding = FragmentChargeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCharge;
        chargeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}