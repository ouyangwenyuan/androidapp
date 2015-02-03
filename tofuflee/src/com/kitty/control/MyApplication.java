package com.kitty.control;

import org.cocos2d.sound.SoundEngine;

import android.app.Application;

import com.kitty.global.ImageManager;
import com.kitty.tofuflee.R;

public class MyApplication extends Application {

    private static MyApplication app;

    public static MyApplication getGlobalContext() {
        return app;
    }
    //private int[] effectIds;
   // private int[] soundIds;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
       // AudioUtil.init(this);
       // MediaPlayerUtil.init(this);
        ImageManager.getInstance();
        ImageManager.getInstance().setContext(this);
        SoundEngine soundEngine = SoundEngine.sharedEngine();
       // SoundEngine.sharedEngine().preloadEffect(app, R.raw.crumble);
        //SoundEngine.sharedEngine().preloadSound(app, R.raw.abd_mus1);
    }
}
