package com.kitty.usedengine;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.kitty.control.MyApplication;
import com.kitty.global.GlobalConfig;
import com.kitty.tofuflee.R;
import com.kitty.utils.FJLog;

public class SecondLayer extends CCLayer {
    public class Coordinate {
        public float x;
        public float y;
    }

    public enum GameState {
        init,
        reset,
        start,
        ing,
        end;
    }

    private float x_pos = 160;
    private float y_pos = 160;
    private float x_speed = 0;
    private float y_speed = 0;

    private float y_acc = -800;

    private CCSprite tofu;
    private GameState gameState;
    private Rect playRect;
    private Rect exitRect;
    private CCSprite playBtnSprite;
    private CCSprite exitBtnSprite;

    public SecondLayer() {
        gameState = GameState.init;

        this.setIsTouchEnabled(true);
        this.setIsAccelerometerEnabled(true);

        Bitmap image = BitmapFactory.decodeResource(MyApplication.getGlobalContext().getResources(), R.drawable.background);
        CCSprite sprite = CCSprite.sprite(image, "tofu_logo");
        FJLog.l("image width=" + image.getWidth() + ",height=" + image.getHeight());
        int width = GlobalConfig.deviceWidth / 2;
        int height = GlobalConfig.deviceHeight / 2;
        sprite.setPosition(width, height);
        this.addChild(sprite);

        final Bitmap playBtn = BitmapFactory.decodeResource(MyApplication.getGlobalContext().getResources(), R.drawable.start);
        playBtnSprite = CCSprite.sprite(playBtn, "playBtn");
        playBtnSprite.setPosition(CGPoint.ccp(100, GlobalConfig.deviceHeight / 2));
        this.addChild(playBtnSprite);
        playRect = new Rect(100, GlobalConfig.deviceHeight / 2, (int) (100 + playBtn.getWidth()), (int) (GlobalConfig.deviceHeight / 2 + playBtn.getHeight()));

        final Bitmap exitBtn = BitmapFactory.decodeResource(MyApplication.getGlobalContext().getResources(), R.drawable.btn_cancel);
        exitBtnSprite = CCSprite.sprite(exitBtn, "exitBtn");
        exitBtnSprite.setPosition(CGPoint.ccp(GlobalConfig.deviceWidth / 2 + 20, GlobalConfig.deviceHeight / 2));
        this.addChild(exitBtnSprite);
        exitRect = new Rect((int) (GlobalConfig.deviceWidth / 2 + 20), GlobalConfig.deviceHeight / 2, (int) (GlobalConfig.deviceWidth / 2 + 20 + exitBtn.getWidth()),
                (int) (GlobalConfig.deviceHeight / 2 + exitBtn.getHeight()));

        final Bitmap tofuImage = BitmapFactory.decodeResource(MyApplication.getGlobalContext().getResources(), R.drawable.tofu_l);
        tofu = CCSprite.sprite(tofuImage, "tofu");
        tofu.setPosition(CGPoint.ccp(x_pos, y_pos));
        this.addChild(tofu);

        //final Bitmap plateImage = BitmapFactory.decodeResource(MyApplication.getGlobalContext().getResources(), R.drawable.p_pas_1);

        this.schedule(new UpdateCallback() {

            @Override
            public void update(float d) {
                FJLog.l("update=" + d);
                if (gameState != GameState.ing) {
                    return;
                }
                x_pos += x_speed * d;
                float max_x = GlobalConfig.deviceWidth - tofuImage.getWidth() / 2;
                float min_x = 0 + tofuImage.getWidth() / 2;
                if (x_pos > max_x)
                    x_pos = max_x;
                if (x_pos < min_x)
                    x_pos = min_x;
                y_speed += y_acc * d;
                y_pos += y_speed * d;

                if (y_pos >= GlobalConfig.deviceHeight - tofuImage.getHeight() / 2) {
                    y_speed = -y_speed;
                    y_pos += y_speed * d;
                    // } else if (y_pos < GlobalConfig.deviceHeight / 2) {
                    //y_speed = 350 + Math.abs(x_speed);
                    //                    y_speed = 0;
                    //  y_pos += y_speed * d;
                } else if (y_pos < 0 + tofuImage.getHeight() / 2) {
                    y_speed = 800;
                    y_pos += y_speed * d;
                }
                //
                //                if (y_speed < 0) {
                //                    y_speed = 350 + Math.abs(x_speed);
                //                } else if (y_pos > 240) {
                //                    float delta = y_pos - 240;
                //                    y_pos = 240;
                //
                //                }
                tofu.setPosition(x_pos, y_pos);
            }
        });
    }

    public CCScene getCCScene() {
        CCScene gameScene = CCScene.node();
        gameScene.addChild(this);

        return gameScene;
    }

    @Override
    public boolean ccTouchesEnded(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        FJLog.l("touch position,x=" + x + ",y=" + y);
        // TofuSprite.setSpeed(tofu, 0, y, world);
        if (exitRect.contains(x, y)) {
            gameState = GameState.end;
            CCDirector.sharedDirector().getActivity().finish();
        }

        if (playRect.contains(x, y)) {
            gameState = GameState.ing;
            playBtnSprite.setVisible(false);
        }
        return super.ccTouchesEnded(event);
    }

    @Override
    public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
        super.ccAccelerometerChanged(accelX, accelY, accelZ);
        //  FJLog.l("touch position,x=" + accelX + ",y=" + accelY + ",z=" + accelZ);
        float accel_filter = 0.1f;
        x_speed = x_speed * accel_filter + accelX * (1.0f - accel_filter) * 100.0f;
    }
}
