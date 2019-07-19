package com.jyx.barrageview;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DpPxUtils.init(this);
    }
}
