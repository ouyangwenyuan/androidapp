package com.kitty.usedengine;

import java.util.Iterator;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.kitty.control.GameActivity;
import com.kitty.control.MyApplication;
import com.kitty.global.GlobalConfig;
import com.kitty.tofuflee.R;
import com.kitty.utils.FJLog;

public class FirstLayer extends CCLayer {

	private Rect rect;
	public class Coordinate {
		public float x;
		public float y;
	}
	// float r, x, y;
	// 最大垂直速度 方向向上 -16.
	float MAXVERTICALSPEED = -10, MAXVERTICALA = 1;
	// 默认最大高度
	float defaultJumpHight;
	// 小球在垂直方向上需要移动的距离！
	float verticalMove;
	float ha = 0, va = 1f, v0v, vtv, v0h, vth;

	// private int currentPlatformY;
	private float jumpStartY;

	private float x_pos;
	private float y_pos;
	private float x_speed;
	private float y_speed = 40;
	private boolean isDown;
	// private float x_acc;
	// private float y_acc =2;

	private CCSprite tofu;
	private World world;

	public FirstLayer() {
		this.setIsTouchEnabled(true);
		this.setIsAccelerometerEnabled(true);
		Bitmap image = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.tofu_logo_final);
		CCSprite sprite = CCSprite.sprite(image, "tofu_logo");
		FJLog.l("image width=" + image.getWidth() + ",height="
				+ image.getHeight());
		int width = GlobalConfig.deviceWidth / 2;
		int height = GlobalConfig.deviceHeight / 2;
		sprite.setPosition(width, height);
		rect = new Rect(width - 100, height - 100, width + 100, height + 100);
		this.addChild(sprite);

		Bitmap tofuImage = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.tofu_l);
		tofu = CCSprite.sprite(tofuImage, "tofu");
		tofu.setPosition(CGPoint.ccp(width, 0));
		this.addChild(tofu);

		defaultJumpHight = height * 2 / 3;
		// r = width / 18;
		// y =( height - r )* 2;
		// x = width - r;
		// 根据加速度公式计算得出Vt^2-V0^2=2AX
		MAXVERTICALSPEED = -(int) ((float) Math.sqrt(2 * 20 / 3 * MAXVERTICALA));
		verticalMove = defaultJumpHight;
		v0v = MAXVERTICALSPEED;
		world = TofuSprite.createWorld();
	
		this.schedule(new UpdateCallback() {

			@Override
			public void update(float dt) {
				int velocityIterations =8;
				int positionIterations =1;
				world.step(dt, velocityIterations, positionIterations);
				
				if(world.getBodyCount()>0){
					Iterator<Body> bodies = world.getBodies();
					while(bodies.hasNext()){
					Body body = bodies.next();
					Object data = body.getUserData();
					if(data != null && data instanceof CCSprite){
						CCSprite box = (CCSprite) data;
						float newy = box.getPosition().y;
						FJLog.l("world  y= " + newy );
						tofu.setPosition((float)box.getPosition().x,newy);
						
					}
						
					}
				}
				//tofu.setPosition(x_pos, y_pos);
			}
		});

	}
			

				// FJLog.l("schedule s= " + dt);
//
//				x_pos += x_speed * dt;
//				float max_x = GlobalConfig.deviceWidth;
//				float min_x = 0;
//
//				if (x_pos > max_x)
//					x_pos = max_x;
//				if (x_pos < min_x)
//					x_pos = min_x;
//
//				 vtv = v0v + va;
//				 // 当下降速度达到一定程度时，设置加速度为0.4f。
//				 if (vtv > -5 * MAXVERTICALSPEED / 8) {
//				 va = 0.4f;
//				 } else {
//				 va = MAXVERTICALA;
//				 }
//				 // 当当前还需上升的高度 大于默认高度时，速度继续保持最大速度，vtv<0表示方向向上
//				 if (verticalMove > defaultJumpHight && vtv < 0) {
//				 vtv = MAXVERTICALSPEED;
//				 }
//				 float vMove = (v0v + vtv) / 2; // 这一次垂直高度移动距离。
//				 verticalMove = verticalMove + vMove;// 减小时，表示网上移动了
//				 y_pos = y_pos + vMove;
//				 v0v = vtv;
//				 if (y_pos <= 0) {// 触地了
//				 y_pos = 0;
//				 v0v = MAXVERTICALSPEED;
//				 verticalMove = defaultJumpHight;
//				 }

				// // 垂直方向移动
				// if (isDown) {
				// y_pos -= y_speed;
				// if(y_pos <=10){
				// isDown = false;
				// y_pos =0;
				// }
				//
				// } else {
				// // 上升的上限高度
				// int moveHeightLimit =
				// GlobalConfig.deviceHeight/2;//(jumpState ==
				// JUMP_STATE_HIGHJUMP || jumpState == JUMP_STATE_HIGHJUMP_ONCE)
				// ? jumpHeightHigh : jumpHeightNormal;
				// // 如果到达上限高度则下落
				// if (y_pos + y_speed - jumpStartY - 20 >= moveHeightLimit) {
				// y_pos = jumpStartY + 20 + moveHeightLimit;
				//
				// isDown = true;
				// } else {
				// y_pos += y_speed;
				// }
				// }

				// if(y_speed < 0) {

				// t = kPlatformsStartTag;
				// for(t; t < kPlatformsStartTag + kNumPlatforms; t++) {
				// CCSprite *platform = (CCSprite*)[batchNode getChildByTag:t];
				//
				// CGSize platform_size = platform.contentSize;
				// CGPoint platform_pos = platform.position;
				//
				// max_x = platform_pos.x - platform_size.width/2 - 10;
				// min_x = platform_pos.x + platform_size.width/2 + 10;
				// float min_y = platform_pos.y +
				// (platform_size.height+bird_size.height)/2 -
				// kPlatformTopPadding;
				// y_speed = 50.0f + Math.abs(x_speed);
				
//				try {
//					// FJLog.l("xpos = " +x_pos +",ypos="+y_pos);
//					if (y_pos >= 0 && y_pos <= GlobalConfig.deviceHeight / 2) {
//						if (y_pos >= GlobalConfig.deviceHeight / 4) {
//							y_speed = 20;
//						} else {
//							y_speed = 40;
//						}
//						if (isDown) {
//							y_pos -= y_speed;
//						} else {
//							y_pos += y_speed;
//						}
//					} else if (y_pos < 0) {
//						y_pos = 0;
//						Thread.sleep(50);
//						isDown = false;
//					} else if (y_pos > GlobalConfig.deviceHeight / 2) {
//						y_pos = GlobalConfig.deviceHeight / 2;
//						Thread.sleep(50);
//						isDown = true;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
				// }

				// if(y_pos < -bird_size.height/2) {
				// [self showHighscores];
				// }

				// } else
				// if(y_pos > GlobalConfig.deviceHeight/2) {
				// y_speed += y_acc * dt;
				// y_pos -= y_speed * dt;
				//
				// float delta = y_pos - 240;
				// y_pos = 240;
				//
				// currentPlatformY -= delta;
				//
				// t = kCloudsStartTag;
				// for(t; t < kCloudsStartTag + kNumClouds; t++) {
				// CCSprite *cloud = (CCSprite*)[batchNode getChildByTag:t];
				// CGPoint pos = cloud.position;
				// pos.y -= delta * cloud.scaleY * 0.8f;
				// if(pos.y < -cloud.contentSize.height/2) {
				// currentCloudTag = t;
				// [self resetCloud];
				// } else {
				// cloud.position = pos;
				// }
				// }
				//
				// t = kPlatformsStartTag;
				// for(t; t < kPlatformsStartTag + kNumPlatforms; t++) {
				// CCSprite *platform = (CCSprite*)[batchNode getChildByTag:t];
				// CGPoint pos = platform.position;
				// pos = ccp(pos.x,pos.y-delta);
				// if(pos.y < -platform.contentSize.height/2) {
				// currentPlatformTag = t;
				// [self resetPlatform];
				// } else {
				// platform.position = pos;
				// }
				// }
				//
				// if(bonus.visible) {
				// CGPoint pos = bonus.position;
				// pos.y -= delta;
				// if(pos.y < -bonus.contentSize.height/2) {
				// [self resetBonus];
				// } else {
				// bonus.position = pos;
				// }
				// }
				//
				// score += (int)delta;
				// NSString *scoreStr = [NSString stringWithFormat:@"%d",score];
				//
				// CCLabelBMFont *scoreLabel = (CCLabelBMFont*)[self
				// getChildByTag:kScoreLabel];
				// [scoreLabel setString:scoreStr];
				// }

				// bird.position = bird_pos;

//				tofu.setPosition(x_pos, y_pos);
//			}
//		});
//
//	}

	public CCScene getCCScene() {
		CCScene gameScene = CCScene.node();
		gameScene.addChild(this);

		return gameScene;
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		return super.ccTouchesBegan(event);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		FJLog.l("touch position,x=" + x + ",y=" + y);
		TofuSprite.setSpeed(tofu, 0, y, world);
		if (rect.contains(x, y)) {

			// TODO
//			Intent intent = new Intent(MyApplication.getGlobalContext(),
//					GameActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			MyApplication.getGlobalContext().startActivity(intent);
		}
		return super.ccTouchesEnded(event);
	}

	@Override
	public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
		super.ccAccelerometerChanged(accelX, accelY, accelZ);
	//	FJLog.l("touch position,x=" + accelX + ",y=" + accelY + ",z=" + accelZ);
		float accel_filter = 0.1f;
		x_speed = x_speed * accel_filter + accelX * (1.0f - accel_filter)
				* 500.0f;
	}

}
