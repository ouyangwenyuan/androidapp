package com.kitty.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.TextView;

/**
 * synchronized--防止线程死锁
 * @author Administrator
 * 
 */
public class GameSurfaceView extends SurfaceView implements Callback {
	private GameThread gameThread;
	private TextView textView;

	/**
	 * 负责初始化界面和类的对象的实例化
	 * 
	 * @param context
	 * @param attrs
	 */
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		gameThread = new GameThread(context, holder, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				textView.setText(msg.getData().getString("text"));
			}
		});
		// 设置可获得焦点，确保能捕获到keyCode
		setFocusable(true);
	}

	/**
	 * 获取一个Activity传来的view协助surfaceview显示游戏视图， view的具体类型可以根据游戏需要设定
	 * 
	 * @param view
	 */
	public void setTextView(TextView view) {
		this.textView = view;
	}

	/**
	 * 获取处理业务的线程
	 * 
	 * @return
	 */
	public GameThread getThread() {
		return gameThread;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return gameThread.doKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return gameThread.doKeyUp(keyCode, event);
	}

	/**
	 * 当surfaceview得到或失去焦点时调用，是游戏暂停、恢复运行 捕获屏幕焦点变化的事件
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (!hasWindowFocus) {
			gameThread.pause();
		} else {
			gameThread.unPause();
		}
	}

	/**
	 * 根据界面的变化，更新界面
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v(this.getClass().getName(), "surfaceChanged");
		gameThread.setSurfaceSize(width, height);
		gameThread.setRunning(true);
		if (gameThread.isAlive()) {
			Log.v(this.getClass().getName(), "unpause gameThread");
			gameThread.unPause();
		} else {
			Log.v(this.getClass().getName(), "statte gameThread");
			//启动线程，调用run方法，游戏开始运行
			gameThread.start();
		}
	}

	/**
	 * 创建SurfaceView时执行
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(this.getClass().getName(), "surfaceCreated");
	}

	/**
	 * 销毁SurfaceView时执行 为防止surface还会被创建导致gamethread再次启动出现错误，
	 * 且Activity的onpause方法中以作暂停处理，此处不对gamethread进行处理
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(this.getClass().getName(), "surfaceDestroyed");
	}

}
