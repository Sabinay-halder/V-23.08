package com.widevision.pillreminder.activity;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by mercury-one on 14/8/15.
 */
public class VirtualNurse extends Application {


    public static final String TAG = "VIVZ";

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // Notice this initialization code here
        ActiveAndroid.initialize(this);


    }
}
