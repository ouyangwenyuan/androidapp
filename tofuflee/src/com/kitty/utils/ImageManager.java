package com.kitty.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kitty.tofuflee.R;

/**
 * 图片管理类
 * 
 * @author Administrator
 * 
 */
public class ImageManager {
    // 字母
    public static final int CHAR_WIDTH = 15;

    private final Rect[] SMALL_NUM_RECT;
    // 黑洞
    public Bitmap bitmapAst;
    // 炸弹
    public Bitmap bitmapBomb;
    public Bitmap bitmapBonusCrumble;
    public Bitmap bitmapBonusCrumbleLogo;
    public Bitmap bitmapBonusHighJumpLogo;
    public Bitmap bitmapBonusHighjump;
    public Bitmap bitmapBonusSlow;
    public Bitmap bitmapBonusSlowLogo;
    public Bitmap[] bitmapChars;
    //public Bitmap bitmapFlag;
    public Bitmap bitmapLeftCow0;
    public Bitmap bitmapLeftCow1;
    public Bitmap bitmapLoading;
    public Bitmap bitmapNumSmall;
    public Bitmap bitmapPas1;
    public Bitmap bitmapPas2;
    public Bitmap bitmapPas3;
    public Bitmap bitmapPas4;
    public Bitmap bitmapPasGoal;
    public Bitmap bitmapPasTramp;
    public Bitmap bitmapPresent;
    public Bitmap bitmapRightCow0;
    public Bitmap bitmapRightCow1;
    public Bitmap bitmapStart0;
    public Bitmap bitmapStart1;
    public Bitmap bitmapUp;
    public Bitmap bitmapWaterbg;
    public Bitmap bitmapWindmill;
    public Bitmap bitmapOver;

    // 数字图片
    private ImageManager() {
        Rect[] arrayOfRect = new Rect[10];
        arrayOfRect[0] = new Rect(0, 0, 16, 20);
        arrayOfRect[1] = new Rect(16, 0, 32, 20);
        arrayOfRect[2] = new Rect(32, 0, 48, 20);
        arrayOfRect[3] = new Rect(48, 0, 64, 20);
        arrayOfRect[4] = new Rect(64, 0, 80, 20);
        arrayOfRect[5] = new Rect(80, 0, 96, 20);
        arrayOfRect[6] = new Rect(96, 0, 112, 20);
        arrayOfRect[7] = new Rect(112, 0, 128, 20);
        arrayOfRect[8] = new Rect(128, 0, 144, 20);
        arrayOfRect[9] = new Rect(144, 0, 160, 20);
        SMALL_NUM_RECT = arrayOfRect;

    }

    // 上下文
    private Context context;
    // 本类实例
    private static ImageManager instance;

    // 单例模式获取本类的实例
    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    public void setContext(Context paramContext) {
        context = paramContext;
    }

    /**
     * 画字符串
     * 
     * @param paramString
     * @param canvas
     * @param paint
     * @param centerX
     * @param y
     */
    public void drawString(String paramString, Canvas canvas, Paint paint, int centerX, int y) {
        // 将所有在此字符串中的字符小写的规则给定的Locale.
        String tempStr = paramString.toLowerCase();
        // 如果字符串不全是由字母和空格组成，则不画
        if (!tempStr.matches("[a-z\\s]+")) {
            return;
        }
        // 组成字符串的所有字符串
        char[] chs = tempStr.toCharArray();
        // 所有字符对应的图片数组的索引
        int[] indexs = new int[chs.length];
        // 所有字符串对应的宽度
        int[] widths = new int[chs.length];
        // 所有字符的总宽度
        int sumWidth = 0;
        // 定义空格的宽度
        int blankWidth = 20;

        // 计算索引及宽度
        for (int i = 0; i < chs.length; i++) {
            if (chs[i] == ' ') {
                widths[i] = blankWidth;
            } else {
                indexs[i] = chs[i] - 'a';
                widths[i] = bitmapChars[indexs[i]].getWidth();
            }
            sumWidth += widths[i];
        }
        // 定义画出来的字符串宽度为：每个字符宽度-2后再相加的结果
        int overlayWidth = 4;
        int drawWidth = sumWidth - overlayWidth * chs.length;
        // 结尾字符的右边缘X坐标
        int endX = centerX + drawWidth / 2;
        // 倒着画，造成后面的字母被前面的字母压住的效果
        for (int i = chs.length - 1; i >= 0; i--) {
            if (chs[i] != ' ') {
                //				canvas.drawBitmap(bitmapChars[indexs[i]], endX - widths[i],
                //						paint);
            }
            endX -= widths[i] - overlayWidth;
        }

    }

    /**
     * 加载游戏中所有图片
     */
    public void loadBitmaps() {
        //		bitmapOver = BitmapFactory.decodeResource(context.getResources(), R.drawable)
        bitmapAst = BitmapFactory.decodeResource(context.getResources(), R.drawable.ice);
        bitmapBomb = BitmapFactory.decodeResource(context.getResources(), R.drawable.bombdrop);
        bitmapBonusCrumble = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonus_crumble);
        bitmapBonusHighjump = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonus_highjump);
        bitmapBonusSlow = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonus_slowmo);
        bitmapRightCow0 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tofu_r);
        bitmapRightCow1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.tofu_l);
        bitmapPas1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.p_pas_1);
        bitmapPas2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.p_pas_2);
        bitmapPas3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.p_pas_3);
        bitmapPas4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.p_pas_4);
        bitmapPasTramp = BitmapFactory.decodeResource(context.getResources(), R.drawable.p_pas_tramp);
        bitmapPasGoal = BitmapFactory.decodeResource(context.getResources(), R.drawable.p_pas_goal);
        bitmapPresent = BitmapFactory.decodeResource(context.getResources(), R.drawable.present);
        bitmapWaterbg = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        bitmapWindmill = BitmapFactory.decodeResource(context.getResources(), R.drawable.frontlayer);
        //bitmapFlag = BitmapFactory.decodeResource(context.getResources(), R.drawable.ice);
        bitmapBonusCrumbleLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonus_logo_cr);
        bitmapBonusHighJumpLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonus_logo_hj);
        bitmapBonusSlowLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonus_logo_sm);
        bitmapNumSmall = BitmapFactory.decodeResource(context.getResources(), R.drawable.number_small);
        bitmapUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.shit);
        bitmapStart0 = BitmapFactory.decodeResource(context.getResources(), R.drawable.start);
        bitmapStart1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.start_s);
        bitmapChars = new Bitmap[26];
        bitmapChars[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.a);
        bitmapChars[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.b);
        bitmapChars[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c);
        bitmapChars[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.d);
        bitmapChars[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.e);
        bitmapChars[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.f);
        bitmapChars[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.g);
        bitmapChars[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.h);
        bitmapChars[8] = BitmapFactory.decodeResource(context.getResources(), R.drawable.i);
        bitmapChars[9] = BitmapFactory.decodeResource(context.getResources(), R.drawable.j);
        bitmapChars[10] = BitmapFactory.decodeResource(context.getResources(), R.drawable.k);
        bitmapChars[11] = BitmapFactory.decodeResource(context.getResources(), R.drawable.l);
        bitmapChars[12] = BitmapFactory.decodeResource(context.getResources(), R.drawable.m);
        bitmapChars[13] = BitmapFactory.decodeResource(context.getResources(), R.drawable.n);
        bitmapChars[14] = BitmapFactory.decodeResource(context.getResources(), R.drawable.o);
        bitmapChars[15] = BitmapFactory.decodeResource(context.getResources(), R.drawable.p);
        bitmapChars[16] = BitmapFactory.decodeResource(context.getResources(), R.drawable.q);
        bitmapChars[17] = BitmapFactory.decodeResource(context.getResources(), R.drawable.r);
        bitmapChars[18] = BitmapFactory.decodeResource(context.getResources(), R.drawable.s);
        bitmapChars[19] = BitmapFactory.decodeResource(context.getResources(), R.drawable.t);
        bitmapChars[20] = BitmapFactory.decodeResource(context.getResources(), R.drawable.u);
        bitmapChars[21] = BitmapFactory.decodeResource(context.getResources(), R.drawable.v);
        bitmapChars[22] = BitmapFactory.decodeResource(context.getResources(), R.drawable.w);
        bitmapChars[23] = BitmapFactory.decodeResource(context.getResources(), R.drawable.x);
        bitmapChars[24] = BitmapFactory.decodeResource(context.getResources(), R.drawable.y);
        bitmapChars[25] = BitmapFactory.decodeResource(context.getResources(), R.drawable.z);
        // 对奶牛朝向右的图片做镜像变换得到朝左的图片
        Matrix localMatrix = new Matrix();
        localMatrix.setScale(-1, 1);
        bitmapLeftCow0 = Bitmap.createBitmap(bitmapRightCow0, 0, 0, bitmapRightCow0.getWidth(), bitmapRightCow0.getHeight(), localMatrix, false);
        bitmapLeftCow1 = Bitmap.createBitmap(bitmapRightCow1, 0, 0, bitmapRightCow1.getWidth(), bitmapRightCow1.getHeight(), localMatrix, false);
    }

    /**
     * 用数字图片画数字
     * 
     * @param num
     * @param canvas
     * @param paint
     * @param x
     * @param y
     */
    public void drawNum(int num, Canvas canvas, Paint paint, int x, int y) {
        if (num < 0) {
            return;
        }
        if (num < 10) {
            canvas.drawBitmap(bitmapNumSmall, SMALL_NUM_RECT[num], new Rect(x, y, x + 16, y + 20), paint);
        } else {
            ArrayList<Integer> singleNumList = new ArrayList<Integer>();
            int temp = num;
            do {
                singleNumList.add(temp % 10);
                temp = temp / 10;
            } while (temp > 0);
            final int size = singleNumList.size();
            for (int i = size - 1; i >= 0; i--) {
                canvas.drawBitmap(bitmapNumSmall, SMALL_NUM_RECT[singleNumList.get(i)], new Rect(x + (size - 1 - i) * 16, y, x + (size - i) * 16, y + 10), paint);
            }
        }
    }

}
