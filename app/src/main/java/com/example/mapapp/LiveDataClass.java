package com.example.mapapp;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataClass extends ViewModel {
    public LiveDataClass() {
        this.clickedValue.setValue(0);
    }

    /*** Clicked value***/
    public MutableLiveData<Integer> clickedValue = new MediatorLiveData<Integer>();
    public int getClickedValue() {
        return clickedValue.getValue();
    }
    public void setClickedValue(int value) {
        clickedValue.setValue(value);
    }
}
