package com.kitty.model;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

import com.kitty.global.ImageManager;
import com.kitty.utils.AudioUtil;
import com.kitty.view.GameThread;

public class Tofu {
    public static final String TAG = "Cow";
    // 增高跳跃高度
    public static int jumpHeightHigh;
    // 普通跳跃高度
    public static int jumpHeightNormal;
    // 正常的弹跳状态
    private final static int JUMP_STATE_NORMAL = 0;
    // 能踩断踏板的弹跳状态
    private final static int JUMP_STATE_CRUMBLE = 1;
    // 跳得更高的弹跳装状态
    private final static int JUMP_STATE_HIGHJUMP = 2;
    // 跳的慢的弹跳状态
    private final static int JUMP_STATE_MOVESLOWLY = 3;
    // 踩到弹簧踏板后获得的一级跳的更高的弹跳状态
    private final static int JUMP_STATE_HIGHJUMP_ONCE = 4;
    // 碰撞地图物体结果：游戏继续
    private final static int COLLIDE_RESULT_CONTIONUE = 0;
    // 碰撞地图物体结果：游戏失败
    private final static int COLLIDE_RESULT_LOSE = 1;
    // 碰撞地图物体结果：游戏胜利
    private final static int COLLIDE_RESULT_WIN = 2;
    // 垂直方向移动速度：普通
    private static int MOVE_SPEED_VER_NORMAL = 80;
    // 垂直方向移动速度：缓慢
    private static int MOVE_SPEED_VER_SLOW = MOVE_SPEED_VER_NORMAL / 2;
    // 水平方向移动速度
    private static int MOVE_SPPED_HOR_NORMAL = 8;
    // tofu图片的高度
    public int bitmapHeight;
    // tofu图片的宽度
    public int bitmapWidth;
    // 当前的弹跳状态
    private int jumpState;
    private int collideResult;
    private boolean isLookLeft = false;
    // 向左
    //public static final int DIRECTION_LEFT = 0;
    // 向右
    //public static final int DIRECTION_RIGHT = 1;
    // 当前的方向
    // private int direction;
    // 线程类
    private GameThread gameThread;
    // 是否改变方向
    private boolean isChangeDirection;
    // 是否下落
    private boolean isDown;
    // 是否移动
    private boolean isMove;
    // 地图对象
    private GameMap map;
    // 当前的奖励
    private Bonus curBonus;
    // tofu的坐标
    private int x;
    private int y;
    // 起跳高度
    private int jumpStartY;
    //    private float x_pos = 160;
    //    private float y_pos = 160;
    private float x_speed = 0;
    private float y_speed = 0;
    private float y_acc = -800;
    // 上次由于移动产生的Y
    private int lastMoveY;
    private float xOffset;

    // 构造方法
    public Tofu() {
        //this.direction = DIRECTION_RIGHT;
        //this.gameThread = gameThread;
        this.gameThread = GameThread.getInstance();
        this.isDown = false;
        this.isMove = false;
        this.map = gameThread.getGameMap();
        this.bitmapWidth = ImageManager.getInstance().bitmapRightCow0.getWidth();
        this.bitmapHeight = ImageManager.getInstance().bitmapRightCow0.getHeight();
        this.jumpState = JUMP_STATE_NORMAL;
    }

    /**
     * 处理按下按键事件
     * 
     * @param keyCode
     * @param event
     */
    public void doKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        }
    }

    /**
     * 处理按键弹起的按键
     * 
     * @param keyCode
     * @param event
     */
    public void doKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

        }
    }

    /**
     * 是否改变方向
     * 
     * @return
     */
    public boolean isChangeDirection() {
        return isChangeDirection;
    }

    /**
     * 是否下落
     * 
     * @return
     */
    public boolean isDown() {
        return isDown;
    }


    /**
     * 绘制tofu
     * 
     * @param canvas
     * @param paint
     * @param paramInt动画帧数
     */
    public void draw(Canvas canvas, Paint paint, int paramInt) {
        int i = map.getCurBottom() + GameThread.screenHeight - y;
        canvas.save();
        //        switch (isLookLeft) {
        //            case DIRECTION_LEFT:
        // 改变方向
        if (isLookLeft) {
            if (isChangeDirection) {
                // 改变方向移动时，为图片添加旋转角度
                //canvas.rotate(-5, x, i);
            }
            // 下落
            if (isDown) {
                canvas.drawBitmap(ImageManager.getInstance().bitmapLeftCow0, x, i, paint);
            }
            // 上升
            else {
                canvas.drawBitmap(ImageManager.getInstance().bitmapRightCow1, x, i, paint);
            }
        } else {

            // 改变方向移动时，为图片添加旋转角度
            if (isChangeDirection) {
                //canvas.rotate(5, x + bitmapWidth, i);
            }
            if (isDown) {
                canvas.drawBitmap(ImageManager.getInstance().bitmapRightCow0, x, i, paint);
            } else {
                canvas.drawBitmap(ImageManager.getInstance().bitmapRightCow1, x, i, paint);
            }

        }
        canvas.restore();
        if (null != curBonus) {
            curBonus.draw(canvas, paint, paramInt);
        }
    }

    public void collideWidthMapObject() {
        // 对两屏可见的地图物体进行碰撞检测
        if ((collideWidthMapObjects(map.getObjectList1()) > COLLIDE_RESULT_CONTIONUE) || (collideWidthMapObjects(map.getObjectList2()) > COLLIDE_RESULT_CONTIONUE)) {
            if (collideResult == COLLIDE_RESULT_LOSE) {
                // 死亡
                die();
            } else {
                // 胜利
                win();
            }
        }
    }

    /**
     * 与某一屏的地图物体进行碰撞检测
     * 
     * @param objectList地图物体
     * @return 碰撞结果
     */
    private int collideWidthMapObjects(ArrayList<MapObject> objectList) {
        ArrayList<MapObject> toRemovePresents = new ArrayList<MapObject>();

        // 检测对于黑洞、炸弹、礼物盒的碰撞
        for (MapObject obj : objectList) {
            if (obj.isVisibleInScreen(map.getCurBottom()) && this.intersectMapObject(obj)) {
                switch (obj.getType()) {
                // 黑洞
                    case MapObject.TYPE_AST:
                        isMove = false;
                        collideResult = COLLIDE_RESULT_LOSE;
                        return collideResult;
                        // 炸弹
                    case MapObject.TYPE_BOME:
                        isMove = false;
                        objectList.remove(obj);
                        collideResult = COLLIDE_RESULT_LOSE;
                        return collideResult;
                    case MapObject.TYPE_PRESENT:
                        // tofu与礼物盒碰撞
                        collideWidthPresent();
                        toRemovePresents.add(obj);
                        break;
                    default:
                }
            }
        }
        // 删除礼物盒对象
        objectList.removeAll(toRemovePresents);

        MapObject toRemocePas = null;
        // tofu下落过程中检测对于踏板的碰撞
        if (isDown) {
            for (MapObject obj : objectList) {
                if (obj.isVisibleInScreen(map.getCurBottom()) && this.intersectMapObject(obj) && isHigherThanMapObject(obj)) {
                    Log.i(this.getClass().getName(), "tofu踩到踏板！");
                    switch (obj.getType()) {
                        case MapObject.TYPE_PAS_1:
                        case MapObject.TYPE_PAS_2:
                        case MapObject.TYPE_PAS_3:
                        case MapObject.TYPE_PAS_4:
                        case MapObject.TYPE_PAS_TRAMP:
                            // 发生弹跳事件，tofu移动方向改为上升
                            isDown = false;
                            // tofu的当前Y坐标
                            y = obj.getPasTopY() + bitmapHeight;
                            // 起跳点为踏板上缘坐标
                            jumpStartY = obj.getPasTopY();
                            updateJumpState();
                            if (jumpState == JUMP_STATE_CRUMBLE) {
                                toRemocePas = obj;
                            }
                            playJumpSound();
                            updateCurBonus();
                            if (obj.getType() == MapObject.TYPE_PAS_TRAMP) {
                                y = obj.getY() + bitmapHeight;
                                jumpState = JUMP_STATE_HIGHJUMP_ONCE;
                                AudioUtil.getAudioUtil().play(AudioManager.STREAM_MUSIC, 1);
                            }
                            break;
                        case MapObject.TYPE_PAS_GOAL:
                            // tofu碰撞到终点踏板上缘时游戏胜利
                            if (y - bitmapHeight <= obj.getPasTopY()) {
                                // tofu当前Y坐标
                                y = obj.getPasTopY() + bitmapHeight;
                                collideResult = COLLIDE_RESULT_WIN;
                                return collideResult;
                            }
                            break;
                        default:
                    }
                }
                // 如果已与一个踏板发生了碰撞导致tofu弹起，则不在检测下面的踏板
                if (!isDown) {
                    break;
                }
            }
        }
        // 移除被踩断的踏板
        if (null != toRemocePas) {
            objectList.remove(toRemocePas);
            int tempY = map.getCurBottom() + GameThread.screenHeight - toRemocePas.getY();
            gameThread.addCrumbleAnimation(toRemocePas.getX() + toRemocePas.getBitmap().getWidth() / 2, tempY);
        }
        return collideResult;
    }

    /**
     * 更新奖励
     */
    private void updateCurBonus() {
        switch (jumpState) {
            case JUMP_STATE_NORMAL:
                break;
            case JUMP_STATE_HIGHJUMP_ONCE:
                jumpState = JUMP_STATE_NORMAL;
                break;
            case JUMP_STATE_CRUMBLE:
            case JUMP_STATE_HIGHJUMP:
            case JUMP_STATE_MOVESLOWLY:
                if (curBonus != null) {
                    curBonus.reduceTimes();
                    if (curBonus.getTimes() < 0) {
                        jumpState = JUMP_STATE_NORMAL;
                        curBonus = null;
                    }
                } else {
                    jumpState = JUMP_STATE_NORMAL;
                }
                break;
        }
    }

    /**
     * 播放tofu弹跳的背景音乐
     */
    private void playJumpSound() {
        switch (jumpState) {
            case JUMP_STATE_HIGHJUMP:
                AudioUtil.getAudioUtil().play(AudioUtil.SOUND_HIJUMP, 0);
                break;
            case JUMP_STATE_CRUMBLE:
                AudioUtil.getAudioUtil().play(AudioUtil.SOUND_CRUMBLE, 0);
            default:
        }
    }

    /**
     * 更新tofu弹跳状态
     */
    private void updateJumpState() {
        if (null != curBonus) {
            switch (curBonus.getType()) {
                case Bonus.BONUS_TYPE_CRUMBLE:
                    jumpState = JUMP_STATE_CRUMBLE;
                    break;
                case Bonus.BONUS_TYPE_HIGHJUMP:
                    jumpState = JUMP_STATE_HIGHJUMP;
                    break;
                case Bonus.BONUS_TYPE_SLOW:
                    jumpState = JUMP_STATE_MOVESLOWLY;
                    break;
            }
        }

    }

    /**
     * 检测高度
     * 
     * @param obj
     * @return
     */
    private boolean isHigherThanMapObject(MapObject obj) {
        return lastMoveY - bitmapHeight > obj.getPasTopY();
    }

    private void collideWidthPresent() {
        Random rand = new Random();
        curBonus = new Bonus(rand.nextInt(Bonus.BONUS_TYPES.length), gameThread.getFrame());
    }

    /**
     * tofu是否与地图物体发生碰撞
     * 
     * @param obj
     * @return
     */
    private boolean intersectMapObject(MapObject obj) {
        return (obj.getX() < x + bitmapWidth) && (obj.getY() - obj.getBitmap().getHeight() < y) && (obj.getX() + obj.getBitmap().getWidth() > x) && (obj.getY() > y - bitmapHeight);
    }

    /**
     * 游戏胜利
     */
    private void win() {
        isMove = false;
        AudioUtil.getAudioUtil().play(AudioUtil.SOUND_FANFARE, 1);
        gameThread.gameWin();
    }

    /**
     * tofu死亡
     */
    private void die() {
        AudioUtil.getAudioUtil().play(AudioUtil.SOUND_LOSE, 0);
        gameThread.gameOver();
    }

    /**
     * 游戏失败
     */
    public void gameOver() {

    }

    /**
     * 检查tofu是否落出屏外死亡
     * 
     * @return
     */
    public boolean checkDead() {
        boolean isDead = false;
        if (y + bitmapHeight < map.getCurBottom()) {
            isDead = true;
        }
        return isDead;
    }

    /**
     * tofu方向向左
     */
    public void changDirectionLeft(float xOffset) {
        this.xOffset = xOffset;
        isChangeDirection = true;
        //direction = DIRECTION_LEFT;
        isLookLeft = true;
    }

    /**
     * tofu方向向右
     */
    public void changDirectionRight(float xOffset) {
        this.xOffset = xOffset;
        isChangeDirection = true;
        //direction = DIRECTION_RIGHT;
        isLookLeft = false;
    }

    /**
     * tofu方向不变
     */
    public void stopChangDirection() {
        this.xOffset = 0.0f;
    }

    /**
     * tofu移动
     */
    public void move(float d) {
        if (!isMove) {
            return;
        }
        lastMoveY = y;

        int verSpeed = (jumpState == JUMP_STATE_MOVESLOWLY) ? MOVE_SPEED_VER_SLOW : MOVE_SPEED_VER_NORMAL;
        // 垂直方向移动
        if (isDown) {
            xOffset = 0.0f;
            y -= verSpeed;
            // 第一屏不会死亡
            if (map.getCurBottom() == 0) {
                if (y - bitmapHeight < map.getCurBottom()) {
                    y = bitmapHeight;
                    isDown = false;
                    jumpStartY = 0;
                }
            } else if (checkDead()) {
                die();
                Log.v(TAG, "the cow is dead!!");
            }
        } else {
            // 上升的上限高度
            int moveHeightLimit = (jumpState == JUMP_STATE_HIGHJUMP || jumpState == JUMP_STATE_HIGHJUMP_ONCE) ? jumpHeightHigh : jumpHeightNormal;

            int oldY = y;
            // 如果到达上限高度则下落
            if (y + verSpeed - jumpStartY - bitmapHeight >= moveHeightLimit) {
                y = jumpStartY + bitmapHeight + moveHeightLimit;

                isDown = true;
            } else {
                y += verSpeed;
            }
            // 更新tofu的跳跃高度
            gameThread.updateCurheight(y);

            // 拉动地图上升
            gameThread.moveMap(oldY, y);
        }
        // 水平方向移动
        if (isChangeDirection) {
            //            switch (direction) {
            //                case DIRECTION_LEFT:
            if (isLookLeft) {
                x -= MOVE_SPPED_HOR_NORMAL * xOffset;
                if (x + bitmapWidth < 0) {
                    x += GameThread.screenWidth;
                }
            } else {
                x += MOVE_SPPED_HOR_NORMAL * xOffset;
                if (x > GameThread.screenWidth) {
                    x -= GameThread.screenWidth;
                }
            }
        }
    }

    public void setDown(boolean isDown) {
        this.isDown = isDown;
    }

    public void setMove(boolean isMove) {
        this.isMove = isMove;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(int bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(int bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public void setChangeDirection(boolean isChangeDirection) {
        this.isChangeDirection = isChangeDirection;
    }

    public int getJumpStartY() {
        return jumpStartY;
    }

    public void setJumpStartY(int jumpStartY) {
        this.jumpStartY = jumpStartY;
    }
}
