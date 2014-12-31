package com.kitty.control;

import android.app.Application;

import com.kitty.utils.AudioUtil;
import com.kitty.utils.MediaPlayerUtil;

public class MyApplication extends Application {

    private static MyApplication app;

    public static MyApplication getGlobalContext() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AudioUtil.init(this);
        MediaPlayerUtil.init(this);
    }
}
