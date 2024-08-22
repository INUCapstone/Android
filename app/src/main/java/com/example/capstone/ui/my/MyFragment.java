package com.example.capstone.ui.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.capstone.R;
import com.example.capstone.databinding.FragmentMyBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFragment extends Fragment {

    private FragmentMyBinding binding;
    private TextView emailInfo,nickNameInfo,phoneNumberInfo,pointInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyViewModel myViewModel =
                new ViewModelProvider(this).get(MyViewModel.class);

        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        emailInfo = root.findViewById(R.id.emailInfo);
        nickNameInfo = root.findViewById(R.id.nickNameInfo);
        phoneNumberInfo = root.findViewById(R.id.phoneNumberInfo);
        pointInfo = root.findViewById(R.id.pointInfo);

        if(getArguments() != null){

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
