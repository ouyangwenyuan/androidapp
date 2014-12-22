package com.kitty.model;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.kitty.utils.ImageManager;
import com.kitty.view.GameThread;

/**
 * 地图类
 * 
 * @author Administrator
 * 
 */
public class GameMap {

    private final static String TAG = "GameMap";
    // 终点踏板的高度
    public final static int MAX_HEIGHT = 30000;
    // 每两层踏板之间的最小间隔
    private final static int LINE_HEIGHT = 40;
    // 每屏中炸弹最大数量
    private final static int MAX_BOME_NUM = 1;
    // 每屏中弹簧踏板的最大数量
    private final static int MAX_TRAMPPAS_NUM = 1;
    // 每屏中礼物盒的最大数量
    private final static int MAX_PRESENT_NUM = 2;
    // 每屏中黑洞的最大数量
    private final static int MAX_AST_NUM = 1;
    // 已产生炸弹的数量
    private int bombNum = 0;
    // 已产生的弹簧踏板的数量
    private int trampPasNum = 0;
    // 已产生的黑洞数量
    private int astNum = 0;
    // 已产生礼物盒的数量
    private int presentNum = 0;
    // 第一屏地图物体的集合
    public ArrayList<MapObject> objectList1 = new ArrayList<MapObject>();
    // 第二屏地图物体的集合
    public ArrayList<MapObject> objectList2 = new ArrayList<MapObject>();
    // 当前地图底部的坐标
    private int curBottom;
    // 已初始化数据的屏数
    private int initialScreenNum = 0;
    // 随机数产生器
    private Random random = new Random();
    // 上次普通踏板产生的高度
    private int lastPasCreatedHeight;

    public GameMap() {
    }

    /**
     * 绘画
     * 
     * @param canvas
     * @param paint
     * @param frame
     */
    public void draw(Canvas canvas, Paint paint, int frame) {
        // 绘制地图背景
        drawBackGround(canvas, paint);
        // 绘制地图中的物体
        drawMapObjects(canvas, paint, frame);
        // 绘制地图旗帜
        drawForeGround(canvas, paint);
    }

    /**
     * 移动地图的物体
     */
    public void move() {
        if (objectList1.size() <= 0 || objectList2.size() <= 0) {
            return;
        }
        // 对于第一屏的物体，移动Y坐标大于地图底部坐标的
        for (int i = 0; i < objectList1.size(); i++) {
            MapObject obj = objectList1.get(i);
            if (obj.getY() > curBottom) {
                obj.move();
            }
        }
        // 对于第二屏的物体，移动Y坐标小于地图底部坐标加上屏幕高度的
        for (int i = 0; i < objectList2.size(); i++) {
            MapObject obj = objectList2.get(i);
            if (obj.getY() > GameThread.screenHeight) {
                obj.move();
            }
        }
    }

    /**
     * 初始化地图数据
     */
    public void initData() {
        if (initialScreenNum == 0) {
            // 游戏刚开始时初始化两屏的数据
            for (int i = LINE_HEIGHT; i < GameThread.screenHeight; i += LINE_HEIGHT) {
                objectList1.addAll(createRandomMapObjects(i));
            }
            for (int i = GameThread.screenHeight + LINE_HEIGHT; i < 2 * GameThread.screenHeight; i += LINE_HEIGHT) {
                objectList2.addAll(createRandomMapObjects(i));
            }
            initialScreenNum = 2;
        } else {
            // 初始化地图物体中的数量
            initNums();
            // 将游戏中的第二屏复制给第一屏
            objectList1 = objectList2;
            // 初始化第二屏的数据
            objectList2 = new ArrayList<MapObject>();
            for (int i = initialScreenNum * GameThread.screenHeight + LINE_HEIGHT; i < (initialScreenNum + 1) * GameThread.screenHeight; i += LINE_HEIGHT) {
                if (i >= MAX_HEIGHT) {
                    if (i - lastPasCreatedHeight >= Tofu.jumpHeightHigh) {
                        int tempHeight = lastPasCreatedHeight + Tofu.jumpHeightNormal - LINE_HEIGHT;
                        if (tempHeight < MAX_HEIGHT) {
                            MapObject obj = createRealNormalPas(tempHeight);
                            lastPasCreatedHeight = obj.getY() > lastPasCreatedHeight ? obj.getY() : lastPasCreatedHeight;
                            objectList2.add(obj);
                        }
                    }
                    // 产生终点踏板
                    objectList2.add(new MapObject(MapObject.TYPE_PAS_GOAL, random.nextInt(GameThread.screenWidth - ImageManager.getInstance().bitmapPasGoal.getWidth()), MAX_HEIGHT
                            + GameThread.screenWidth - ImageManager.getInstance().bitmapPasGoal.getHeight() - ImageManager.getInstance().bitmapPas4.getHeight(), false));
                    break;
                } else {
                    objectList2.addAll(createRandomMapObjects(i));
                }
            }
            // 增加初始化的屏幕数量
            initialScreenNum++;
        }
    }

    private void initNums() {
        // 黑洞数量
        this.astNum = 0;
        // 炸弹的数量
        this.bombNum = 0;
        // 礼物盒的数量
        this.presentNum = 0;
        // 弹簧踏板的数量
        this.trampPasNum = 0;
    }

    /**
     * 根据出现位置的高度产生随机的地图物体集合，包括炸弹、黑洞、礼物盒、踏板
     * 
     * @param height 出现位置的高度
     * @return 产生的地图物体
     */
    private ArrayList<MapObject> createRandomMapObjects(int height) {
        ArrayList<MapObject> objList = new ArrayList<MapObject>();
        // 黑洞
        MapObject ast = createAst(height);
        if (ast != null) {
            objList.add(ast);
        }
        // 踏板
        MapObject normalPas = createNormalpas(height);
        if (normalPas != null) {
            objList.add(normalPas);
        }
        // 弹簧踏板
        MapObject trampPas = createTrampPas(height);
        if (trampPas != null) {
            objList.add(trampPas);
        }
        // 礼物盒
        MapObject present = createPresent(height);
        if (present != null) {
            objList.add(present);
        }
        // 炸弹
        MapObject bomb = createBomb(height);
        if (bomb != null) {
            objList.add(bomb);
        }
        return objList;
    }

    /**
     * 创建炸弹
     * 
     * @param paramInt
     * @return
     */
    private MapObject createBomb(int paramInt) {
        int i = GameThread.screenHeight;
        MapObject localMapObject = null;
        if (paramInt > i) {
            int j = bombNum;
            localMapObject = null;
            if (j < 1) {
                int k = random.nextInt(30);
                localMapObject = null;
                if (k == 0) {
                    localMapObject = new MapObject(9, random.nextInt(GameThread.screenWidth - ImageManager.getInstance().bitmapBomb.getWidth()), paramInt, true);
                    bombNum = 1 + bombNum;
                }
            }
        }
        return localMapObject;
    }

    /**
     * 创建弹簧踏板
     */
    private MapObject createTrampPas(int paramInt) {
        int i = GameThread.screenHeight;
        MapObject localMapObject = null;
        if (paramInt > i) {
            int j = trampPasNum;
            localMapObject = null;
            if (j < 1) {
                int k = random.nextInt(50);
                localMapObject = null;
                if (k == 0) {
                    localMapObject = new MapObject(5, random.nextInt(GameThread.screenWidth - ImageManager.getInstance().bitmapPasTramp.getWidth()), paramInt, false);
                    trampPasNum = 1 + trampPasNum;
                }
            }
        }
        return localMapObject;
    }

    /**
     * 创建礼物盒
     * 
     * @param paramInt
     * @return
     */
    private MapObject createPresent(int paramInt) {
        int i = GameThread.screenHeight;
        MapObject localMapObject = null;
        if (paramInt > i) {
            int j = presentNum;
            localMapObject = null;
            if (j < 2) {
                int k = random.nextInt(30);
                localMapObject = null;
                if (k == 0) {
                    localMapObject = new MapObject(7, random.nextInt(GameThread.screenWidth - ImageManager.getInstance().bitmapPresent.getWidth()), paramInt, false);
                    presentNum = 1 + presentNum;
                }
            }
        }
        return localMapObject;
    }

    /**
     * 创建踏板
     * 
     * @param paramInt
     * @return
     */
    private MapObject createNormalpas(int height) {
        MapObject obj = null;
        // 如果当前的距离一个产生的高度已超过奶牛的跳跃的高度的画
        if (height - lastPasCreatedHeight >= Tofu.jumpHeightNormal) {
            obj = createRealNormalPas(lastPasCreatedHeight + curBottom);
            lastPasCreatedHeight = obj.getY() - lastPasCreatedHeight;
        } else if (random.nextInt(3) > 0) {
            obj = createRealNormalPas(height);
            lastPasCreatedHeight = height;
        }
        return obj;
    }

    /**
     * @param height
     * @return
     */
    private MapObject createRealNormalPas(int height) {
        final double sqrtHeight = Math.sqrt(height);
        int type = random.nextInt(MapObject.TYPE_PAS_4) + 1;
        boolean isMove = false;
        // 两屏以上的踏板才可以移动
        int saveHeight = (int) sqrtHeight;
        if (random.nextInt((int) saveHeight >= 1 ? saveHeight : 1) > Math.sqrt(GameThread.screenHeight)) {
            isMove = true;
        }
        return new MapObject(type, random.nextInt(GameThread.screenWidth - ImageManager.getInstance().bitmapPas1.getWidth()), height, isMove);
    }

    /**
     * 产生黑洞
     * 
     * @param height
     * @return
     */
    private MapObject createAst(int height) {
        MapObject obj = null;

        // 一屏以上的高度1/100几率产生黑洞
        if (height > GameThread.screenHeight) {
            if (astNum < MAX_AST_NUM) {
                if (random.nextInt(100) == 0) {
                    obj = new MapObject(MapObject.TYPE_AST, 0, height, false);
                    astNum++;
                }
            }
        }
        return obj;
    }

    /**
     * 绘制地图旗帜
     * 
     * @param canvas
     * @param paint
     */
    private void drawForeGround(Canvas canvas, Paint paint) {
        if (20 + curBottom >= GameThread.screenHeight) {
            return;
        }
        canvas.drawBitmap(ImageManager.getInstance().bitmapFlag, 0, 20 + curBottom, paint);
    }

    /**
     * 绘制地图中的物体
     * 
     * @param canvas
     * @param paint
     * @param frame
     */
    private void drawMapObjects(Canvas canvas, Paint paint, int frame) {
        if (objectList1.size() <= 0 || objectList2.size() <= 0) {
            return;
        }
        // 对于第一个物体的，画y坐标大雨地图的底部的坐标
        for (int i = 0; i < objectList1.size(); i++) {
            MapObject obj = objectList1.get(i);
            if (obj.isVisibleInScreen(curBottom)) {
                obj.draw(canvas, paint, frame, curBottom);
            }
        }
        // 对于第二屏的物体，画y坐标的地图底部要加上屏幕的高度
        for (int i = 0; i < objectList2.size(); i++) {
            MapObject obj = objectList2.get(i);
            if (obj.isVisibleInScreen(curBottom)) {
                obj.draw(canvas, paint, frame, curBottom);
            }
        }
    }

    /**
     * 绘制地图背景
     * 
     * @param canvas
     * @param paint
     */
    private void drawBackGround(Canvas canvas, Paint paint) {
        // 蓝天
        canvas.drawBitmap(ImageManager.getInstance().bitmapWaterbg, 0, 0, paint);
        // 画风车
        if (curBottom < GameThread.screenHeight) {
            canvas.drawBitmap(ImageManager.getInstance().bitmapWindmill, 0, GameThread.screenHeight - ImageManager.getInstance().bitmapWindmill.getHeight(), paint);
        } else if (curBottom - GameThread.screenHeight < ImageManager.getInstance().bitmapWaterbg.getHeight() - curBottom) {
            canvas.drawBitmap(ImageManager.getInstance().bitmapWaterbg, 0, curBottom - ImageManager.getInstance().bitmapWaterbg.getHeight(), paint);
        }
    }

    public int getCurBottom() {
        return curBottom;
    }

    public void setCurBottom(int curBottom) {
        this.curBottom = curBottom;
    }

    public int getInitialScreenNum() {
        return initialScreenNum;
    }

    public void setInitialScreenNum(int initialScreenNum) {
        this.initialScreenNum = initialScreenNum;
    }

    public ArrayList<MapObject> getObjectList1() {
        return objectList1;
    }

    public ArrayList<MapObject> getObjectList2() {
        return objectList2;
    }

}
