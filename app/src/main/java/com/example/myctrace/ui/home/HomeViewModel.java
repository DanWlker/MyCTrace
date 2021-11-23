package com.example.myctrace.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    //mUName, mConfirmedCase, mRecoveredCase, mVacProgress, mRiskStatus, mRiskUpdated, mVacStatus, mVacUpdated
    String uname = "Username";

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hello, " + uname);
    }

    public LiveData<String> getText() {
        return mText;
    }
}