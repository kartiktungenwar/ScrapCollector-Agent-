package com.techflux.oyebhangarwalaagent.app;

import android.app.Application;

import com.firebase.client.Firebase;


/**
 * Created by Lenovo on 24/05/2017.
 */
public class MyApplication extends Application  {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing firebase
        Firebase.setAndroidContext(getApplicationContext());
    }
}
