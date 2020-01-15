package com.scandecode_example;

import android.app.Application;



/**
 * @author xuyan  Application
 */
public class AppDecode extends Application {


    private static AppDecode sInstance;

    public static AppDecode getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
