package com.example.capstone.ui.charge;

import android.graphics.Color;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone.R;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.service.ChargeService;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.databinding.FragmentChargeBinding;
import com.example.capstone.dto.ChargePointReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ChargeFragment extends Fragment {

    private FragmentChargeBinding binding;
    private Button chargeButton;
    private Button plusPointButton5000,plusPointButton10000,plusPointButton20000,plusPointButton50000;
    private TextView currentPointText;
    private EditText chargingPointEdit;
    private Long currentPoint;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChargeViewModel chargeViewModel =
                new ViewModelProvider(this).get(ChargeViewModel.class);

        binding = FragmentChargeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        chargeButton = root.findViewById(R.id.button_charge);
        plusPointButton5000 = root.findViewById(R.id.button_plus_5000);
        plusPointButton10000 = root.findViewById(R.id.button_plus_10000);
        plusPointButton20000 = root.findViewById(R.id.button_plus_20000);
        plusPointButton50000 = root.findViewById(R.id.button_plus_50000);
        currentPointText = root.findViewById(R.id.currentPoint_text);
        chargingPointEdit = root.findViewById(R.id.chargingPoint_edit);

        if(getArguments() != null){

            try {
                String data = getArguments().getString("myInfo");
                JSONObject jsonObject = new JSONObject(data);
                JSONObject memberInfo = jsonObject.optJSONObject("response");
                currentPointText.setText(memberInfo.optInt("point",0)+"원");
                currentPoint = (long) memberInfo.optInt("point",0);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        plusPointButton5000.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                long chargingPoint = Long.parseLong(chargingPointEdit.getText().toString());

                chargingPointEdit.setText(String.valueOf(chargingPoint + 5000));
            }
        });
        plusPointButton10000.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                long chargingPoint = Long.parseLong(chargingPointEdit.getText().toString());

                chargingPointEdit.setText(String.valueOf(chargingPoint + 10000));
            }
        });
        plusPointButton20000.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                long chargingPoint = Long.parseLong(chargingPointEdit.getText().toString());

                chargingPointEdit.setText(String.valueOf(chargingPoint + 20000));
            }
        });
        plusPointButton50000.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                long chargingPoint = Long.parseLong(chargingPointEdit.getText().toString());

                chargingPointEdit.setText(String.valueOf(chargingPoint + 50000));
            }
        });

        chargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long chargingPoint = Long.parseLong(chargingPointEdit.getText().toString());

                ChargeService chargeService = new ChargeService(getActivity());
                chargeService.chargePoint(new ChargePointReq(chargingPoint), new RepositoryCallback() {
                    @Override
                    public void onSuccess(String message) {
                        long chargingPoint = Long.parseLong(chargingPointEdit.getText().toString());
                        currentPoint += chargingPoint;

                        currentPointText.setText(chargingPoint+"원");
                        chargingPointEdit.setText("0");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {
                        if(errorMessages != null && errorMessages.containsKey("point")){
                            chargingPointEdit.setText("");
                            chargingPointEdit.setHintTextColor(Color.RED);
                            chargingPointEdit.setHint(errorMessages.get("point"));
                        }

                        if(errorMessages != null && errorMessages.containsKey("message")){
                            Toast.makeText(getActivity(), errorMessages.get("message"), Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getActivity(), exceptionCode.getExceptionMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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