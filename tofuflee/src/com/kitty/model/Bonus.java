package com.kitty.model;

import com.kitty.global.ImageManager;
import com.kitty.view.GameThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 道具类
 * 
 * @author Administrator
 * 
 */
public class Bonus {
	// 奖励次数
	public static final int BONUS_TIMES = 9;
	// 奖励类型：踩断
	public static final int BONUS_TYPE_CRUMBLE = 0;
	// 奖励类型：跳的更高
	public static final int BONUS_TYPE_HIGHJUMP = 1;
	// 奖励类型：减速
	public static final int BONUS_TYPE_SLOW = 2;
	// 所有的奖励
	public static final int[] BONUS_TYPES = { BONUS_TYPE_CRUMBLE,
			BONUS_TYPE_HIGHJUMP, BONUS_TYPE_SLOW };
	// 获得奖励时动画持续帧数
	private static final int animationDuration = 4;
	// 获得奖励时的动画开始帧数
	private int animationStartFrame;
	// 奖励可用时间
	private int times;

	// 奖励类型
	private int type;

	private int scaledParam;

	// 构造方法
	public Bonus(int type, int animationStartFrame) {
		this.type = type;
		this.animationStartFrame = animationStartFrame;
		this.times = BONUS_TIMES;
	}

	/**
	 * 绘画
	 * 
	 * @param canvas
	 * @param paint
	 * @param paramInt
	 */
	public void draw(Canvas canvas, Paint paint, int frame) {
		drawCreateAnimation(canvas, paint, frame);
		drawLogo(canvas, paint);
	}

	/**
	 * 画奖励LOGO
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawLogo(Canvas canvas, Paint paint) {
		if (times <= 0) {
			return;
		}
		int startY = GameThread.screenHeight * 7 / 8;
		int startX = GameThread.screenWidth * 1 / 2;
		Bitmap logo = null;
		switch (type) {
		case BONUS_TYPE_CRUMBLE:
			logo = ImageManager.getInstance().bitmapBonusCrumbleLogo;
			break;
		case BONUS_TYPE_HIGHJUMP:
			logo = ImageManager.getInstance().bitmapBonusHighJumpLogo;
			break;
		case BONUS_TYPE_SLOW:
			logo = ImageManager.getInstance().bitmapBonusHighJumpLogo;
		default:
		}
		if (null != logo) {
			canvas.drawBitmap(logo, startX, startY, paint);
			// ImageManager.getInstance().drawNum(times, canvas, paint,
			// GameThread.screenWidth/2, paint);
		}
	}

	/**
	 * 画奖励时产生的动画
	 * 
	 * @param canvas
	 * @param paint
	 * @param frame
	 */
	private void drawCreateAnimation(Canvas canvas, Paint paint, int frame) {
		if (animationStartFrame == 0) {
			return;
		}
		int tempParam = (frame - animationStartFrame) / 2;
		if (tempParam < animationDuration) {
			drawScaledBitmap(canvas, paint, type, tempParam);
		} else {
			animationStartFrame = 0;
		}
	}

	/**
	 * 
	 * @param canvas
	 * @param paint
	 *            画笔
	 * @param type
	 *            奖励类型
	 * @param tempParam缩放参数
	 */
	private void drawScaledBitmap(Canvas canvas, Paint paint, int type,
			int tempParam) {
		Bitmap originalBitmap = null;
		switch (type) {
		case BONUS_TYPE_CRUMBLE:
			originalBitmap = ImageManager.getInstance().bitmapBonusCrumble;
			break;
		case BONUS_TYPE_HIGHJUMP:
			originalBitmap = ImageManager.getInstance().bitmapBonusHighjump;
			break;
		case BONUS_TYPE_SLOW:
			originalBitmap = ImageManager.getInstance().bitmapBonusSlow;
			break;
		default:
		}
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();

		// 第一帧动画不需缩放
		if (scaledParam == 0) {
			canvas.drawBitmap(originalBitmap, GameThread.screenHeight / 2
					- width / 2, GameThread.screenHeight / 2 - height / 2,
					paint);
		} else {
			// 后面的动画按（动画总帧数-当前帧数）/动画总帧数的倍率缩放
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,
					width * (animationDuration - scaledParam)
							/ animationDuration, height
							* (animationDuration - scaledParam)
							/ animationDuration, false);
			width = scaledBitmap.getWidth();
			height = scaledBitmap.getHeight();

			canvas.drawBitmap(scaledBitmap, GameThread.screenWidth / 2 - width
					/ 2, GameThread.screenHeight / 2 - height / 2
					+ GameThread.screenHeight / animationDuration, paint);
		}
	}

	/**
	 * 减少奖励可用的次数
	 */
	public void reduceTimes() {
		times--;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
}
