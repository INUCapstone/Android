package com.example.capstone.ui.matching;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.capstone.dto.Matching.TaxiRoomRes;

import java.util.List;

public class SharedMatchingModel extends ViewModel {

    private MutableLiveData<TaxiRoomRes> roomInfo = new MutableLiveData<>();
    private MutableLiveData<Boolean> isMatching = new MutableLiveData<>();
    private MutableLiveData<List<TaxiRoomRes>> roomList = new MutableLiveData<>();

    public void setRoomInfo(TaxiRoomRes info) {
        roomInfo = new MutableLiveData<>();
        roomInfo.setValue(info);
    }
    public void reSetRoomInfo(){
        roomInfo = new MutableLiveData<>();
    }
    public LiveData<TaxiRoomRes> getRoomInfo() {
        return roomInfo;
    }


    public void setMatching(){
        isMatching.setValue(true);
    }
    public void setNoMatching(){
        isMatching = new MutableLiveData<>();
        isMatching.setValue(false);
    }

    public LiveData<Boolean> getIsMatching(){
        return isMatching;
    }

    public void reSetRoomList(){
        roomList = new MutableLiveData<>();
    }
}
