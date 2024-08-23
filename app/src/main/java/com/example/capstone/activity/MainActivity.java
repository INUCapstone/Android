package com.example.capstone.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.capstone.R;
import com.example.capstone.api.RepositoryCallback;
import com.example.capstone.api.service.MemberService;
import com.example.capstone.common.ExceptionCode;
import com.example.capstone.databinding.ActivityMainBinding;
import com.example.capstone.dto.ModifyUser;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button modifyUserButton;
    private Button sendModifyUserButton;
    private TextView nickNameInfo;
    private EditText nickNameModify, passwordModify, confirmPasswordModify;
    private CardView myPageInfo;
    private CardView myPageEdit;
    private boolean hasRequestedMy = false;
    private boolean hasRequestedDriver = false;
    private boolean hasRequestedCharge = false;
    private boolean hasRequestedMatching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        // 기본 제목 제거
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        modifyUserButton = findViewById(R.id.button_edit_user);
        sendModifyUserButton = findViewById(R.id.button_send_server_edit_user);
        nickNameModify = findViewById(R.id.edit_nickNameInfo);
        passwordModify = findViewById(R.id.edit_password);
        confirmPasswordModify = findViewById(R.id.edit_confirm_password);
        nickNameInfo = findViewById(R.id.nickNameInfo);
        myPageInfo = findViewById(R.id.card_view);
        myPageEdit = findViewById(R.id.card_view2);
        setupButtonListner();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination,
                                             @Nullable Bundle arguments) {
                handleNavigationChange(destination.getId());
            }
        });

    }

    private void handleNavigationChange(int destinationId) {

        if (destinationId == R.id.navigation_my && !hasRequestedMy) {
            sendMyPage();
        } else if (destinationId == R.id.navigation_driver && !hasRequestedDriver) {
            sendRequestToServer("dashboard");
        } else if (destinationId == R.id.navigation_matching && !hasRequestedMatching) {
            sendRequestToServer("notifications");
        } else if (destinationId == R.id.navigation_charge && !hasRequestedCharge) {
            sendRequestToServer("my");
        }
    }

    private void sendRequestToServer(String pageName) {
        // 미구현
        hasRequestedMy = false;
        hasRequestedCharge = true;
        hasRequestedDriver = true;
        hasRequestedMatching = true;
    }

    private void sendMyPage(){
        hasRequestedCharge = false;
        hasRequestedDriver = false;
        hasRequestedMatching = false;
        hasRequestedMy = true;

        MemberService memberService = new MemberService(MainActivity.this);
        memberService.getUserInfo(new RepositoryCallback(){

            @Override
            public void onSuccess(String message) {

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {
                Toast.makeText(MainActivity.this, exceptionCode.getExceptionMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                MemberService memberService = new MemberService(MainActivity.this);
                ModifyUser modifyUser = ModifyUser.builder()
                        .nickname(nickNameModify.getText().toString())
                        .password(passwordModify.getText().toString())
                        .confirmPassword(confirmPasswordModify.getText().toString())
                        .build();
                memberService.modifyUserInfo(modifyUser, new RepositoryCallback(){

                    @Override
                    public void onSuccess(String message) {
                        hasRequestedMy = false;
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(ExceptionCode exceptionCode, Map<String, String> errorMessages) {
                        if(errorMessages != null){
                            showErrorMessageModifyUser(errorMessages);
                        }

                        if(errorMessages != null && errorMessages.containsKey("message")){
                            Toast.makeText(MainActivity.this, errorMessages.get("message"), Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(MainActivity.this, exceptionCode.getExceptionMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                myPageEdit.setVisibility(View.GONE);
                myPageInfo.setVisibility(View.VISIBLE);
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