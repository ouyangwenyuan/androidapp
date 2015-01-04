package com.kitty.control;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;

import com.kitty.usedengine.FirstLayer;
import com.kitty.utils.DeviceTools;
import com.kitty.utils.FJLog;

public class FirstActivity extends Activity {

	private CCDirector director;
	static{
		System.loadLibrary("jbox");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FJLog.l("onCreate(saveBundle =" + savedInstanceState);

		DeviceTools.getDeviceInfo(this);
		director = CCDirector.sharedDirector();
		director.setAnimationInterval(1 / 60);
		director.setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
		director.setDisplayFPS(false);

		CCGLSurfaceView view = new CCGLSurfaceView(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		float x = displayMetrics.widthPixels;
		float y = displayMetrics.heightPixels;
		view.setLayoutParams(params);
		view.frame = CGSize.make(x, y);
		view.bringToFront();
		director.attachInView(view);
		// director.setScreenSize(x, y);
		FirstLayer firstLayer = new FirstLayer();
		CCScene firstScene =firstLayer.getCCScene();
		director.runWithScene(firstScene);
		CCTexture2D.setDefaultAlphaPixelFormat(Bitmap.Config.ARGB_8888);
		setContentView(view);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		FJLog.l("onDestroy()");
		director.end();

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
		director.onPause();
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
		director.onResume();
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
