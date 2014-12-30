package com.kitty.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.kitty.model.GameMap;
import com.kitty.model.Tofu;
import com.kitty.tofuflee.R;
import com.kitty.utils.AudioUtil;
import com.kitty.utils.CrumbleAnimation;
import com.kitty.utils.ImageManager;
import com.kitty.utils.MediaPlayerUtil;

public class GameThread extends Thread {
    // 游戏线程每执行一次需要睡眠的时间
    private final static int DELAY_TIME = 50;
    // 上下文，方便获取到应用的各项资源，如图片、音乐和字符串
    private Context context;
    // 与Activity其他view交互用的handler
    private Handler handler;
    // 由SurfaceView提供的surfaceHoler
    private SurfaceHolder surfaceHolder;
    // 游戏线程运行开关
    private boolean running = false;
    // 游戏状态
    private int gameState;
    // 游戏是否暂停
    private boolean isPaused = false;
    // 感应器
    // private SensorManager sensorManager;
    // 是否注册了传感器
    // private boolean registeredSensor;
    // 最小偏移量
    private float SENSOR_CHANGE_DIRECTION_VALUE = 0.0f;
    // 奶牛
    private Tofu tofu;
    // 帧数
    private int frame;
    private int curHeight;
    // 当前surface的高度
    public static int screenHeight;
    // 当前surface的宽度
    public static int screenWidth;
    //开始按钮区域
    public Rect startButtonRect = new Rect();
    //启动状态
    private String starState;
    // 初始化
    public static final int GS_INIT = 0;
    // 死亡
    public static final int GS_OVER = 3;
    // 准备
    public static final int GS_READY = 1;
    // 启动
    public static final int GS_START = 2;
    //音效处理类
    private AudioUtil au;
    //
    private CrumbleAnimation ca;
    //地图类
    private GameMap gameMap;
    //音乐处理类
    private MediaPlayerUtil mpu;
    //实例化一个画笔
    private Paint paint = new Paint();
    private boolean hasInitFinish;
    private boolean hasPressedStart;

    // 构造方法
    public GameThread(Context context, SurfaceHolder holder, Handler handler) {
        super();
        this.context = context;
        this.handler = handler;
        this.surfaceHolder = holder;
        this.au = new AudioUtil(context);
        this.mpu = new MediaPlayerUtil(context, R.raw.abd_mus1);
    }

    /**
     * 设置游戏状态
     */
    public void setState(int mode) {
        synchronized (surfaceHolder) {
            setState(mode, null);
        }
    }

    /**
     * 设置游戏状态
     * 
     * @param mode
     * @param message
     */
    public void setState(int mode, CharSequence message) {
        synchronized (surfaceHolder) {
        }
    }

    /**
     * 暂停游戏
     */
    public void pause() {
        synchronized (surfaceHolder) {
            isPaused = true;
            mpu.pause();
        }
    }

    /**
	 * 
	 */
    public void unPause() {
        // 如果游戏中存在时间，需要将其在此调整到正常
        synchronized (surfaceHolder) {
            isPaused = false;
            mpu.resume();
        }
    }

    /**
     * 当Activity因销毁而被重新创建时，在此恢复上次游戏运行的数据
     * 
     * @param saveState activity传来的保存游戏数据的容器
     */
    public void restoreState(Bundle saveState) {
        doStart();
    }

    /**
     * 在Activity切至后台时保存游戏
     * 
     * @param outState 保存游戏数据的容器
     */
    public void saveState(Bundle outState) {
    }

    /**
     * 设置游戏线程运行开关
     */
    public void setRunning(boolean b) {
        running = b;
    }

    /**
     * 处理按下键的事件
     * 
     * @param keyCode 按键事件动作值
     * @param event 按键事件对象
     * @return 是否处理完毕
     */
    public boolean doKeyDown(int keyCode, KeyEvent event) {
        synchronized (surfaceHolder) {
            if (tofu != null)
                tofu.doKeyDown(keyCode, event);
            return false;
        }
    }

    /**
     * 处理按键的事件
     * 
     * @param keyCode
     * @param event
     * @return
     */
    public boolean doKeyUp(int keyCode, KeyEvent event) {
        synchronized (surfaceHolder) {
        }
        return false;
    }

    /**
     * 设置surface的宽度和高度
     * 
     * @param width
     * @param height
     */
    public void setSurfaceSize(int width, int height) {
        synchronized (surfaceHolder) {
            screenWidth = width;
            screenHeight = height;
            // 每次画布的宽度和高度改变时，需要在此对图片等资源进行缩放等相关适配器屏幕的处理
        }
    }

    /**
     * 子线程的主run方法
     */
    public void run() {
        // 判断程序是否运行
        while (running) {
            // 判断程序是否暂停
            if (!isPaused) {
                Canvas canvas = null;
                try {
                    // Canvas对象缓冲
                    canvas = surfaceHolder.lockCanvas(null);
                    // 锁定surfaceHolder对象
                    synchronized (surfaceHolder) {
                        // 绘制地图
                        doDraw(canvas);
                    }
                    // 游戏逻辑处理
                    logic();
                } finally {
                    if (canvas != null) {
                        // 最后向系统提交绘制的地图对象
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try {
                    //睡眠的时间
                    Thread.sleep(DELAY_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 游戏逻辑处理
     */
    private void logic() {
        switch (gameState) {
            case GS_INIT:
                if (!hasInitFinish) {
                    //加载所有图片
                    ImageManager.getInstance().loadBitmaps();
                    //开始按钮
                    initStartButtonRect();
                    hasInitFinish = true;
                }
                //实例化一个地图和一个奶牛对象
                gameMap = new GameMap();
                tofu = new Tofu(this);
                //初始化奶牛参数
                initCowParams();
                //初始化图片
                initBgBitmap();
                //初始化地图数据
                gameMap.initData();
                //把GS_READY赋值给游戏状态
                gameState = GS_READY;
                break;
            case GS_READY:
                break;
            //游戏启动
            case GS_START:
                //地图中的物体移动
                gameMap.move();
                if (null != tofu) {
                    //奶牛移动
                    tofu.move();
                }
                if (null != tofu) {
                    tofu.collideWidthMapObject();
                }
                break;
            //死亡
            case GS_OVER:

                break;
            default:
        }
    }

    /**
     * 初始化奶牛参数
     */
    private void initCowParams() {
        // 初始化奶牛的跳跃高度
        Tofu.jumpHeightNormal = screenHeight / 3;
        Tofu.jumpHeightHigh = screenHeight / 2;

        // 初始化奶牛坐标
        tofu.setX(screenWidth / 2 - ImageManager.getInstance().bitmapRightCow0.getWidth() / 2);
        tofu.setY(ImageManager.getInstance().bitmapRightCow0.getHeight());
        tofu.setJumpStartY(0);
    }

    /**
     * 初始化开始按钮对应的矩形的位置
     */
    private void initStartButtonRect() {
        startButtonRect.left = screenWidth / 2 - ImageManager.getInstance().bitmapStart0.getWidth() / 2;
        startButtonRect.right = startButtonRect.left + ImageManager.getInstance().bitmapStart0.getWidth();
        startButtonRect.top = screenHeight / 2;
        startButtonRect.bottom = startButtonRect.top + ImageManager.getInstance().bitmapStart0.getHeight();
    }

    /**
     * 初始化图片
     */
    public void initBgBitmap() {
        // 彩旗
        //        ImageManager.getInstance().bitmapFlag = Bitmap.createScaledBitmap(ImageManager.getInstance().bitmapFlag, screenWidth, ImageManager.getInstance().bitmapFlag.getHeight(),
        //                false);
        // 蓝天
        ImageManager.getInstance().bitmapWaterbg = Bitmap.createScaledBitmap(ImageManager.getInstance().bitmapWaterbg, screenWidth, screenHeight, false);
    }

    /**
     * 初始化开始时的参数
     */
    public void doStart() {
        ImageManager.getInstance().setContext(context);
        // loading
        ImageManager.getInstance().bitmapLoading = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading);
        gameState = 0;
    }

    /**
     * 游戏绘画
     */
    private void doDraw(Canvas canvas) {
        // 设置抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        // 刷背景
        canvas.drawColor(Color.BLACK);
        if (gameState == GS_INIT) {
            canvas.drawBitmap(ImageManager.getInstance().bitmapLoading, screenWidth / 2 - (ImageManager.getInstance().bitmapLoading.getWidth() / 2), screenHeight / 2, paint);
        } else {
            if (null != gameMap) {
                gameMap.draw(canvas, paint, frame);
            }
            // 绘制积分当前高度
            drawCurheight(canvas, paint);

            // 画奶牛
            if (tofu != null) {
                tofu.draw(canvas, paint, frame);
            }

            switch (gameState) {
                case GS_READY:
                    // 绘制开始按钮
                    drawStartButton(canvas, paint);
                    break;
                case GS_START:
                    // 画踩断踏板的动画
                    if (null != ca) {
                        ca.drawAnimation(canvas, paint, frame);
                        if (ca.isHasDrawnFinished()) {
                            ca = null;
                        }
                    }
                    break;
                case GS_OVER:

                default:
            }
        }
    }

    /**
     * 绘制开始按钮
     * 
     * @param canvas
     * @param paint
     */
    private void drawStartButton(Canvas canvas, Paint paint) {
        if (!hasPressedStart) {
            canvas.drawBitmap(ImageManager.getInstance().bitmapStart0, screenWidth / 2 - ImageManager.getInstance().bitmapStart0.getWidth() / 2,
                    screenHeight / 2 - ImageManager.getInstance().bitmapStart0.getHeight(), paint);
        } else {
            canvas.drawBitmap(ImageManager.getInstance().bitmapStart1, screenWidth / 2 - ImageManager.getInstance().bitmapStart1.getWidth() / 2,
                    screenHeight / 2 - ImageManager.getInstance().bitmapStart0.getHeight(), paint);
        }

    }

    /**
     * 绘制积分当前高度
     * 
     * @param canvas
     * @param paint
     */
    public void drawCurheight(Canvas canvas, Paint paint) {
        canvas.drawBitmap(ImageManager.getInstance().bitmapUp, 0, 0, paint);
        ImageManager.getInstance().drawNum(curHeight, canvas, paint, ImageManager.getInstance().bitmapUp.getWidth(), 20);
    }

    /**
     * 处理感应器的变化 event 获取的sensor对象 cow 奶牛对象
     * 
     */
    public void doSensorChanged(SensorEvent event) {
        // 接受加速度传感器的类型
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 获取X轴上的重力加速度
            float sensorValueX = event.values[SensorManager.DATA_X];
            Log.i(getName(), "sensorValueX =" + sensorValueX + ",SENSOR_CHANGE_DIRECTION_VALUE =" + SENSOR_CHANGE_DIRECTION_VALUE);
            if (null != tofu) {
                if (sensorValueX > SENSOR_CHANGE_DIRECTION_VALUE) {
                    // 奶牛方向向左
                    tofu.changDirectionLeft(sensorValueX);
                } else if (sensorValueX < SENSOR_CHANGE_DIRECTION_VALUE) {
                    // 奶牛方向向右
                    tofu.changDirectionRight(-sensorValueX);
                } else {
                    // 奶牛方向不变
                    tofu.stopChangDirection();
                }
            }
        }
    }

    /**
     * 处理触摸事件
     */
    public void doTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (gameState) {
            case GS_READY:
                if (startButtonRect.contains(x, y) && !hasPressedStart) {
                    hasPressedStart = true;
                    new Timer().schedule(new TimerTask() {

                        @Override
                        public void run() {
                            gameState = GS_START;
                            tofu.setMove(true);
                            hasPressedStart = false;
                            mpu.play();
                        }
                    }, 200);
                }
                break;
            case GS_START:
                if (x < tofu.getX()) {
                    tofu.changDirectionLeft(0.1f);
                } else if (x > tofu.getX() + tofu.getBitmapWidth()) {
                    tofu.changDirectionRight(-0.1f);
                } else {
                    tofu.stopChangDirection();
                }
                break;
        }
    }

    /**
     * 游戏胜利
     */
    public void gameWin() {
        gameState = GS_OVER;
        starState = context.getString(R.string.gamewin);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("text", starState);
        msg.setData(bundle);
        handler.sendMessage(msg);
        mpu.stop();
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                gameState = 0;
            }
        }, 3000);
    }

    /**
     * 游戏失败
     */
    public void gameOver() {

        gameState = GS_OVER;
        starState = context.getString(R.string.gameover);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("text", starState);
        msg.setData(bundle);
        handler.sendMessage(msg);
        mpu.stop();
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                tofu = null;
                gameState = GS_INIT;
            }
        }, 3000);
    }

    /**
	 * 
	 */
    public void updateCurheight(int y) {
        curHeight = y;
    }

    /**
     * 移动地图
     * 
     * @param oldY
     * @param y
     */
    public void moveMap(int cowOldY, int cowY) {
        // 当奶牛的当前高度已超过终点高度时，不移动地图
        if (cowY - tofu.getBitmapHeight() >= GameMap.MAX_HEIGHT) {
            //TODO 
            return;
        }
        // 当奶牛跳过半屏高度时移动地图
        if (cowY - gameMap.getCurBottom() > screenHeight * 2 / 3) {
            gameMap.setCurBottom(gameMap.getCurBottom() + cowY - cowOldY);
            if (gameMap.getCurBottom() > (gameMap.getInitialScreenNum() - 1) * screenHeight) {
                gameMap.initData();
            }
        }
    }

    public void backStartMap() {

    }

    public void addCrumbleAnimation(int i, int tempY) {
        ca = new CrumbleAnimation(frame, i, tempY);
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public AudioUtil getAudioUtil() {
        return this.au;
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

}
