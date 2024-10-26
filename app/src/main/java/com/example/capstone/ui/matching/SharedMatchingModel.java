package com.example.capstone.ui.matching;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.capstone.dto.DriverInfo;
import com.example.capstone.dto.Matching.TaxiRoomRes;

public class SharedMatchingModel extends ViewModel {

    private MutableLiveData<TaxiRoomRes> roomInfo = new MutableLiveData<>();
    private MutableLiveData<Integer> status = new MutableLiveData<>();

    public void setRoomInfo(TaxiRoomRes info) {
        roomInfo.setValue(info);
    }

    public LiveData<TaxiRoomRes> getRoomInfo() {
        return roomInfo;
    }

    public void reSet(){
        roomInfo = new MutableLiveData<>();
    }

    public void setStatus(Integer tmp){
        status.setValue(tmp);
    }

    public LiveData<Integer> getStatus(){
        return status;
    }

    public void reSetStatus(){
        roomInfo = new MutableLiveData<>();
    }
}
