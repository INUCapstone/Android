package com.example.capstone.ui.driver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.capstone.dto.DriverInfo;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<DriverInfo> driverInfo = new MutableLiveData<>();

    public void setDriverInfo(DriverInfo info) {
        driverInfo.setValue(info);
    }

    public LiveData<DriverInfo> getDriverInfo() {
        return driverInfo;
    }

    public void reSet(){
        driverInfo = new MutableLiveData<>();
    }
}
