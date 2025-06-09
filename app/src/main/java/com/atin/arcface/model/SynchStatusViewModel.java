package com.atin.arcface.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SynchStatusViewModel extends ViewModel {
    private final MutableLiveData<Boolean> selectedItem = new MutableLiveData<Boolean>();

    public void setStatus(Boolean item) {
        selectedItem.setValue(item);
    }
    public LiveData<Boolean> getStatus() {
        return selectedItem;
    }
}