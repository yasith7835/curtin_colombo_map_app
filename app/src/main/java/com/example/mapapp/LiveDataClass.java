package com.example.mapapp;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class LiveDataClass extends ViewModel {
    public LiveDataClass() {
        this.clickedValue.setValue(0);
        this.lastClickedValue.setValue(2);
        this.filteredList.setValue(new ArrayList<>());

    }

    /*** Clicked value***/
    public MutableLiveData<Integer> clickedValue = new MediatorLiveData<>();
    public int getClickedValue() {
        return clickedValue.getValue();
    }
    public void setClickedValue(int value) {
        clickedValue.setValue(value);
    }


    /*** Last clicked value ***/
    public MutableLiveData<Integer> lastClickedValue = new MediatorLiveData<>();
    public int getLastClickedValue() {
        return lastClickedValue.getValue();
    }
    public void setLastClickedValue(int value) {
        lastClickedValue.setValue(value);
    }

    /*** Filtered array ***/
    public MutableLiveData<ArrayList<Locations>> filteredList = new MediatorLiveData<>();
    public ArrayList<Locations> getFilteredList() {
        return filteredList.getValue();
    }
    public void setFilteredList(ArrayList<Locations> filteredList) {
        this.filteredList.setValue(filteredList);
    }
}
