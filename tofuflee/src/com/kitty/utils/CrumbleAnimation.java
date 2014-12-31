package com.kitty.utils;

import com.kitty.global.ImageManager;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CrumbleAnimation {
	// 动画持续时间
	private final static int DURATION = 4;
	private int startFrame;
	// 动画对应的原踏板上边缘中点的X坐标
	private int centerX;
	// 动画对应的原踏板上边缘中点的X坐标
	private int centerY;
	// 动画完成
	private boolean hasDrawnFinished;
	// 代表被踩断的踏板
	private Bitmap bitmap;

	public CrumbleAnimation(int startFrame, int centerX, int centerY) {
		this.startFrame = startFrame;
		this.centerX = centerX;
		this.centerY = centerY;
		this.hasDrawnFinished = false;
		bitmap = ImageManager.getInstance().bitmapPas4;
	}

	/**
	 * 画动画
	 * 
	 * @param canvas
	 * @param paint
	 * @param frame
	 */
	public void drawAnimation(Canvas canvas, Paint paint, int frame) {
		if (!hasDrawnFinished) {
			int temp = (frame - startFrame) / 2;
			if (temp <= DURATION) {
				int startX = centerX - bitmap.getWidth() / 2;
				switch (temp) {
				case 1:
					canvas.save();
					canvas.rotate(-30, centerX, centerY);
					canvas.drawBitmap(bitmap, startX - bitmap.getWidth(),
							centerY - 2 * bitmap.getHeight(), paint);
					canvas.restore();
					canvas.save();
					canvas.rotate(30, centerX, centerY);
					canvas.drawBitmap(bitmap, startX + bitmap.getWidth(),
							centerY - 2 * bitmap.getHeight(), paint);
					canvas.restore();
					break;
				case 2:
					canvas.save();
					canvas.rotate(-120, centerX, centerY);
					canvas.drawBitmap(bitmap, startX - bitmap.getWidth(),
							centerY - 4 * bitmap.getHeight(), paint);
					canvas.save();
					canvas.rotate(120, centerX, centerY);
					canvas.drawBitmap(bitmap, startX + bitmap.getWidth(),
							centerY - 4 * bitmap.getHeight(), paint);
					canvas.restore();
					break;
				case 3:
					canvas.save();
					canvas.rotate(-150, centerX, centerY);
					canvas.drawBitmap(bitmap, startX - bitmap.getWidth(),
							centerY - 6 * bitmap.getHeight(), paint);
					canvas.save();
					canvas.rotate(150, centerX, centerY);
					canvas.drawBitmap(bitmap, startX + bitmap.getWidth(),
							centerY - 6 * bitmap.getHeight(), paint);
					canvas.restore();
					break;
				case 4:
					canvas.save();
					canvas.rotate(-150, centerX, centerY);
					canvas.drawBitmap(bitmap, startX - bitmap.getWidth(),
							centerY - 7 * bitmap.getHeight(), paint);
					canvas.save();
					canvas.rotate(150, centerX, centerY);
					canvas.drawBitmap(bitmap, startX + bitmap.getWidth(),
							centerY - 7 * bitmap.getHeight(), paint);
					canvas.restore();
					break;
				default:
				}
			} else {
				hasDrawnFinished = true;
			}
		}
	}

	public boolean isHasDrawnFinished() {
		return hasDrawnFinished;
	}

	public void setHasDrawnFinished(boolean hasDrawnFinished) {
		this.hasDrawnFinished = hasDrawnFinished;
	}

}
