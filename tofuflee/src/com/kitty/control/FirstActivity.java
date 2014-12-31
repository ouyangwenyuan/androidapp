package com.kitty.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kitty.utils.FJLog;
import com.kitty.view.GuideView;

public class FirstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FJLog.l("onCreate(saveBundle =" + savedInstanceState);
        GuideView guideView = new GuideView(this);
        //StoryView storyView = new StoryView(this);
        setContentView(guideView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FJLog.l("onDestroy()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        FJLog.l("onCreate(saveBundle =" + intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FJLog.l(" onPause()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FJLog.l("onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        FJLog.l("onResume()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FJLog.l("onSaveInstanceState(saveBundle =" + outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FJLog.l("onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FJLog.l("onStop()");
    }

}
