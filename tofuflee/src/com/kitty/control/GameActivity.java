package com.kitty.control;

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

import com.kitty.utils.FJLog;
import com.kitty.view.GameSurfaceView;
import com.kitty.view.GameThread;

public class GameActivity extends Activity implements SensorEventListener {
    // private GameSurfaceView gameSurfaceView;
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
        GameSurfaceView gameSurfaceView = new GameSurfaceView(this);
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
        gameThread.pause();
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
        gameThread.unPause();

        List<Sensor> accelerometerSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensors.size() > 0) {
            // 获得一个重力感应器
            Sensor sensor = accelerometerSensors.get(0);
            // 注册sensorManager
            registeredSensor = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        } else {
            FJLog.l("no sensor");
        }
        testSensor();
        gameThread.unPause();
        this.wakeLock.acquire();
    }

    private void testSensor() {
        //从传感器管理器中获得全部的传感器列表   
        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        //显示有多少个传感器   
        FJLog.l("经检测该手机有" + allSensors.size() + "个传感器，他们分别是：\n");

        //显示每个传感器的具体信息   
        for (Sensor s : allSensors) {

            String tempString = "\n" + "  设备名称：" + s.getName() + "\n" + "  设备版本：" + s.getVersion() + "\n" + "  供应商：" + s.getVendor() + "\n";

            switch (s.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    FJLog.l(s.getType() + " 加速度传感器accelerometer" + tempString);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    FJLog.l(s.getType() + " 陀螺仪传感器gyroscope" + tempString);
                    break;
                case Sensor.TYPE_LIGHT:
                    FJLog.l(s.getType() + " 环境光线传感器light" + tempString);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    FJLog.l(s.getType() + " 电磁场传感器magnetic field" + tempString);
                    break;
                case Sensor.TYPE_ORIENTATION:
                    FJLog.l(s.getType() + " 方向传感器orientation" + tempString);
                    break;
                case Sensor.TYPE_PRESSURE:
                    FJLog.l(s.getType() + " 压力传感器pressure" + tempString);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    FJLog.l(s.getType() + " 距离传感器proximity" + tempString);
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    FJLog.l(s.getType() + " 温度传感器temperature" + tempString);
                    break;
                case Sensor.TYPE_GRAVITY:
                    FJLog.l(s.getType() + " 重力传感器temperature" + tempString);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    FJLog.l(s.getType() + " 线性加速传感器temperature" + tempString);
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    FJLog.l(s.getType() + " 旋转矢量传感器temperature" + tempString);
                    break;
                default:
                    FJLog.l(s.getType() + " 未知传感器" + tempString);
                    break;
            }
        }
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
        FJLog.l("SensorEvent x=" + event.values[SensorManager.DATA_X] + ",y=" + event.values[SensorManager.DATA_Y] + ",z=" + event.values[SensorManager.DATA_Z]);
        if (gameThread == null) {
            return;
        }
        gameThread.doSensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
