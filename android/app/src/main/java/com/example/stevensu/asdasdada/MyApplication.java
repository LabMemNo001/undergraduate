package com.example.stevensu.asdasdada;

import android.app.Application;
import android.content.Context;

/**
 * Created by Steven Su on 2016/8/11.
 * 获取Context的Application
 */
public class MyApplication extends Application {
    private  static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
