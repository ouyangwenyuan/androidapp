package com.kitty.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

public class GameActivity extends Activity implements SensorEventListener {
    private GameSurfaceView gameSurfaceView;
    private GameThread gameThread;
    private boolean registeredSensor;
    private SensorManager sensorManager;
    // 保持屏幕常亮
    PowerManager powerManager = null;
    WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 保持屏幕常亮
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        //        setContentView(R.layout.main);
        //        gameSurfaceView = (GameSurfaceView) this.findViewById(R.id.gameview);
        //        gameSurfaceView.setTextView((TextView) findViewById(R.id.textview));
        // 取得sensorManager的实例
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);

        gameThread = gameSurfaceView.getThread();

        if (savedInstanceState == null) {
            // 游戏第一次启动时初始化游戏状态
            gameThread.doStart();
            Log.v(this.getClass().getName(), "SIS is null");
        } else {
            // 从其他应用界面切回游戏时，如果Activity重新创建，则恢复上次切除游戏时的各项数据
            gameThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nunnull");
        }

    }

    /**
     * 当Activity被切换到后天时调用，存储Activity重新创建时需要恢复到游戏数据
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gameThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }

    /**
     * 当Activity被切换到后台时调用，此处对游戏进行暂停处理
     */
    @Override
    protected void onPause() {
        super.onPause();
        gameSurfaceView.getThread().pause();
        if (!(registeredSensor)) {
            return;
        }
        sensorManager.unregisterListener(this);
        registeredSensor = false;
        this.wakeLock.release();
    }

    /**
     * 当Activity切换到前台时调用
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 游戏结束暂停状态，游戏正常进行
        gameSurfaceView.getThread().unPause();

        List<Sensor> accelerometerSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensors.size() > 0) {
            // 获得一个重力感应器
            Sensor sensor = accelerometerSensors.get(0);
            // 注册sensorManager
            registeredSensor = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }

        gameSurfaceView.getThread().unPause();
        this.wakeLock.acquire();
    }

    /**
     * 创建游戏菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 定义游戏菜单的点击事件处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(this.getClass().getName(), "onDestroy");
        // 停止游戏
        gameThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                // 阻塞Activity的主线程直至游戏线程执行完毕
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                exitGame();
            }
        }
    }

    // 退出游戏
    public void exitGame() {
        finish();
        System.exit(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameThread != null) {
            Log.i(this.getClass().getName(), "catch onTouchEvent");
            gameThread.doTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gameThread == null) {
            return;
        }
        gameThread.doSensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
