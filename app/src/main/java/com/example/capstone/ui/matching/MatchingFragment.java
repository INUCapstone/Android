package com.example.capstone.ui.matching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone.databinding.FragmentMatchingBinding;

public class MatchingFragment extends Fragment {

    private FragmentMatchingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MatchingViewModel matchingViewModel =
                new ViewModelProvider(this).get(MatchingViewModel.class);

        binding = FragmentMatchingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMatching;
        matchingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}