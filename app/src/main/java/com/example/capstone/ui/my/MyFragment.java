package com.example.capstone.ui.my;

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone.R;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.service.MemberService;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.databinding.FragmentMyBinding;
import com.example.capstone.dto.ModifyUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFragment extends Fragment {

    private FragmentMyBinding binding;
    private TextView emailInfo,nickNameInfo,phoneNumberInfo,pointInfo;
    private Button modifyUserButton;
    private Button sendModifyUserButton;
    private EditText nickNameModify, passwordModify, confirmPasswordModify;
    private CardView myPageInfo;
    private CardView myPageEdit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyViewModel myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setup(root);

        if(getArguments() != null){
            showUserInfo();
        }

        modifyUserButton = root.findViewById(R.id.button_edit_user);
        sendModifyUserButton = root.findViewById(R.id.button_send_server_edit_user);
        nickNameModify = root.findViewById(R.id.edit_nickNameInfo);
        passwordModify = root.findViewById(R.id.edit_password);
        confirmPasswordModify = root.findViewById(R.id.edit_confirm_password);
        nickNameInfo = root.findViewById(R.id.nickNameInfo);
        myPageInfo = root.findViewById(R.id.card_view);
        myPageEdit = root.findViewById(R.id.card_view2);
        setupButtonListner();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showUserInfo(){
        String data = getArguments().getString("myInfo");
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject memberInfo = jsonObject.optJSONObject("response");
            String email = memberInfo.optString("email", "정보 없음");
            String nickName = memberInfo.optString("nickname", "정보 없음");
            String phoneNumber = memberInfo.optString("phoneNumber", "정보 없음");
            String point = memberInfo.optInt("point",0)+"원";
            emailInfo.setText(email);
            nickNameInfo.setText(nickName);
            phoneNumberInfo.setText(phoneNumber);
            pointInfo.setText(point);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setup(View root){
        emailInfo = root.findViewById(R.id.emailInfo);
        nickNameInfo = root.findViewById(R.id.nickNameInfo);
        phoneNumberInfo = root.findViewById(R.id.phoneNumberInfo);
        pointInfo = root.findViewById(R.id.pointInfo);
    }

    private void setupButtonListner(){
        modifyUserButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                nickNameModify.setText(nickNameInfo.getText().toString());
                myPageInfo.setVisibility(View.GONE);
                myPageEdit.setVisibility(View.VISIBLE);
            }
        });

        sendModifyUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberService memberService = new MemberService(getActivity());
                ModifyUser modifyUser = ModifyUser.builder()
                        .nickname(nickNameModify.getText().toString())
                        .password(passwordModify.getText().toString())
                        .confirmPassword(confirmPasswordModify.getText().toString())
                        .build();
                memberService.modifyUserInfo(modifyUser, new RepositoryCallback(){

                    @Override
                    public void onSuccess(String message) {
                        myPageEdit.setVisibility(View.GONE);
                        myPageInfo.setVisibility(View.VISIBLE);
                        nickNameInfo.setText(modifyUser.getNickname());
                        passwordModify.setText("");
                        confirmPasswordModify.setText("");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {
                        if(errorMessages != null){
                            showErrorMessageModifyUser(errorMessages);
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
    }

    private void showErrorMessageModifyUser(Map<String, String> errorMessages){
        if(errorMessages.containsKey("nickname")){
            nickNameModify.setText("");
            nickNameModify.setHintTextColor(Color.RED);
            nickNameModify.setHint(errorMessages.get("email"));
        }
        if(errorMessages.containsKey("password")){
            passwordModify.setText("");
            passwordModify.setHintTextColor(Color.RED);
            passwordModify.setHint(errorMessages.get("password"));
        }
        if(errorMessages.containsKey("confirmPassword")){
            confirmPasswordModify.setText("");
            confirmPasswordModify.setHintTextColor(Color.RED);
            confirmPasswordModify.setHint(errorMessages.get("password"));
        }
    }
}
