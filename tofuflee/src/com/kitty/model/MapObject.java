package com.kitty.model;

import java.util.Random;

import com.kitty.global.ImageManager;
import com.kitty.view.GameThread;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 地图中的物体
 * 
 * @author Administrator
 * 
 */
public class MapObject {
	private final static String TAG = "MapObject";
	// 类型：踏板1
	public final static int TYPE_PAS_1 = 1;
	// 类型：踏板2
	public final static int TYPE_PAS_2 = 2;
	// 类型：踏板3
	public final static int TYPE_PAS_3 = 3;
	// 类型：踏板4
	public final static int TYPE_PAS_4 = 4;
	// 类型：弹簧踏板
	public final static int TYPE_PAS_TRAMP = 5;
	// 类型：最终踏板
	public final static int TYPE_PAS_GOAL = 6;
	// 类型：礼物盒
	public final static int TYPE_PRESENT = 7;
	// 类型：黑洞
	public final static int TYPE_AST = 8;
	// 类型：炸弹
	public final static int TYPE_BOME = 9;
	// 炸弹移动速度
	private final static int BOMB_SPEED = 5;
	// 踏板移动速度
	private final static int PAS_SPEED = 5;
	// 移动方向：左
	private final static int DIRECTION_LEFT = 0;
	// 移动方向：右
	private final static int DIRECTION_RIGHT = 0;
	// 物体对应图片
	private Bitmap bitmap;
	// 踏板的移动方向
	private int direction;
	// 是否移动
	private boolean isMove;
	// 类型
	private int type;
	// x坐标
	private int x;
	// y坐标
	private int y;

	public MapObject(int type, int x, int y, boolean isMove) {
		this.isMove = isMove;
		this.type = type;
		this.x = x;
		this.y = y;

		switch (type) {
		// 踏板1到4
		case TYPE_PAS_1:
			bitmap = ImageManager.getInstance().bitmapPas1;
			break;
		case TYPE_PAS_2:
			bitmap = ImageManager.getInstance().bitmapPas2;
			break;
		case TYPE_PAS_3:
			bitmap = ImageManager.getInstance().bitmapPas3;
			break;
		case TYPE_PAS_4:
			bitmap = ImageManager.getInstance().bitmapPas4;
			break;
		// 弹簧踏板
		case TYPE_PAS_TRAMP:
			bitmap = ImageManager.getInstance().bitmapPasTramp;
			break;
		// 礼物盒
		case TYPE_PRESENT:
			bitmap = ImageManager.getInstance().bitmapPresent;
			break;
		// 黑洞
		case TYPE_AST:
			bitmap = ImageManager.getInstance().bitmapAst;
			break;
		// 炸弹
		case TYPE_BOME:
			bitmap = ImageManager.getInstance().bitmapBomb;
			break;
		default:
		}
		// 判断是否移动
		if (isMove) {
			switch (type) {
			case TYPE_PAS_1:
			case TYPE_PAS_2:
			case TYPE_PAS_3:
			case TYPE_PAS_4:
				// 随机产生0到1之间的数，进行向左或向右移动
				Random random = new Random();
				direction = random.nextInt(2);
				break;
			default:
			}
		}
	}

	/**
	 * 移动
	 */
	public void move() {
		if (!isMove) {
			return;
		}
		switch (type) {
		// 炸弹，垂直下落
		case TYPE_BOME:
			y -= BOMB_SPEED;
			break;
		// 踏板
		case TYPE_PAS_1:
		case TYPE_PAS_2:
		case TYPE_PAS_3:
		case TYPE_PAS_4:
			if (direction == DIRECTION_LEFT) {
				x -= PAS_SPEED;
				if (x <= 0) {
					direction = DIRECTION_RIGHT;
				}
			} else {
				x += PAS_SPEED;
				if (x + bitmap.getWidth() >= GameThread.screenWidth) {
					direction = DIRECTION_LEFT;
				}
			}
			break;
		default:
		}
	}

	/**
	 * 绘画
	 * 
	 * @param canvas
	 * @param paint
	 * @param frame
	 * @param curBottom
	 */
	public void draw(Canvas canvas, Paint paint, int frame, int curBottom) {
		int tempY = curBottom + GameThread.screenHeight - y;
		switch (type) {
		// 踏板
		case TYPE_PAS_1:
		case TYPE_PAS_2:
		case TYPE_PAS_3:
		case TYPE_PAS_4:
		// 弹簧踏板
		case TYPE_PAS_TRAMP:
		// 最终踏板
		case TYPE_PAS_GOAL:
		// 礼物盒
		case TYPE_PRESENT:
			canvas.drawBitmap(bitmap, x, tempY, paint);
			break;
		// 黑洞
		case TYPE_AST:
			canvas.save();
			// 每次进行选择365度
			canvas.rotate(10 * frame % 36, x + bitmap.getWidth() / 2, tempY
					+ bitmap.getHeight() / 2);
			canvas.drawBitmap(bitmap, x, tempY, paint);
			canvas.restore();
			break;
		// 炸弹
		case TYPE_BOME:
			// 每3帧变化一次角度
			canvas.save();
			// 0度
			float degrees = 0;
			int tempFrame = frame % 12;
			if ((tempFrame >= 3) && (tempFrame < 6)) {
				// 逆时针摆动10度
				degrees = -10;
			} else if (tempFrame >= 9) {
				degrees = 10;
			} else {
				degrees = 0;
			}
			canvas.rotate(degrees, x + bitmap.getWidth() / 2,
					tempY + bitmap.getHeight() / 2);
			canvas.drawBitmap(bitmap, x, tempY, paint);
			canvas.restore();
			break;
		default:
		}
	}

	/**
	 * 是否在地图中可见
	 * 
	 * @param curBottom
	 *            地图底部Y坐标
	 * @return true 可见,false 不可见
	 */
	public boolean isVisibleInScreen(int curBottom) {
		return y > curBottom && y < curBottom + GameThread.screenHeight;
	}

	/**
	 * 获取踏板上缘坐标
	 * 
	 * @return
	 */
	public int getPasTopY() {
		return y - bitmap.getHeight()
				+ ImageManager.getInstance().bitmapPas4.getHeight();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("type:");
		sb.append(type);
		sb.append("\t");
		sb.append(x);
		sb.append("\t");
		sb.append("y:");
		sb.append(y);
		sb.append("\t");
		sb.append("isMove:");
		sb.append(isMove);
		return sb.toString();
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

	public int getType() {
		return type;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setType(int type) {
		this.type = type;
	}

}
