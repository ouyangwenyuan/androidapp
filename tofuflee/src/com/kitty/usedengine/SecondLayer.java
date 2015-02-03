package com.kitty.usedengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.kitty.control.MyApplication;
import com.kitty.global.GlobalConfig;
import com.kitty.tofuflee.R;
import com.kitty.utils.FJLog;

public class SecondLayer extends CCLayer {
	public enum Tag4Sprite {
		kSpriteManager, kBird, kScoreLabel, kCloudsStartTag, kPlatformsStartTag, kBonusStartTag, kBackground
	}
	public enum Bonus {
		kBonus5, kBonus10, kBonus50, kBonus100, kNumBonuses
	}
	public enum GameState {
		init, reset, start, ing, end;
	}
	private static final int kPlatformsStartTag = 200;
	private static final int kFPS = 60;
	private static final int kMinPlatformStep = 50;
	private static final int kMaxPlatformStep = 300;
	private static final int kNumberPlatforms = 15;
	private static final int kPlatformTopPadding = 20;
	private static final int kMinBonusStep = 30;
	private static final int kMaxBonusStep = 50;

	CGPoint bird_pos;
	CGPoint bird_vel;
	CGPoint bird_acc;

	float currentPlatformY;
	int currentPlatFormTag;
	float currentMaxPlatformStep;
	int currentBonusPlatformIndex;
	int currentBonusType;
	int platformCount;
	boolean tofuLookingRight;

	int score;

	// private float x_pos = 160;
	// private float y_pos = 160;
	// private float x_speed = 0;
	// private float y_speed = 0;
	// private float y_acc = -1800;
	// private int max_plate = 20;
	// private boolean isDown = false;
	// private int last_tofu_y = 0;

	private CCSprite tofu;
	private GameState gameState;
	// private List<CCSprite> plates = null;
	private Bitmap plateImage;

	public CCScene getCCScene() {
		CCScene gameScene = CCScene.node();
		gameScene.addChild(this);

		return gameScene;
	}

	public SecondLayer() {
		gameState = GameState.init;

		Bitmap image = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.background);
		CCSprite sprite = CCSprite.sprite(image, Tag4Sprite.kBackground.name());
		FJLog.l("image width=" + image.getWidth() + ",height="
				+ image.getHeight());
		int width = GlobalConfig.deviceWidth / 2;
		int height = GlobalConfig.deviceHeight / 2;
		sprite.setPosition(width, height);
		this.addChild(sprite);

		final Bitmap tofuImage = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.tofu_l);
		tofu = CCSprite.sprite(tofuImage, Tag4Sprite.kBird.name());
		// tofu.setPosition(CGPoint.ccp(x_pos, y_pos));
		tofu.setAnchorPoint(0.5f, 0.5f);
		this.addChild(tofu, 4);

		plateImage = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.p_pas_1);
		initPlatforms();
		this.scheduleUpdate();
		this.setIsTouchEnabled(true);
		this.setIsAccelerometerEnabled(true);
		startGame();
	}

	private void startGame() {
		gameState = GameState.start;
		score = 0;
		resetPlatforms();
		resetTofu();

	}

	private void resetTofu() {
		bird_pos = CGPoint.ccp(0, 0);
		bird_pos.x = 160;
		bird_pos.y = GlobalConfig.deviceHeight / 2;
		tofu.setPosition(bird_pos);

		bird_vel = CGPoint.ccp(0, 0);
		bird_vel.x = 0;
		bird_vel.y = 0;

		bird_acc = CGPoint.ccp(0, 0);
		bird_acc.x = 0;
		bird_acc.y = -1000.0f;
		tofuLookingRight = true;
		tofu.setScaleX(1.0f);

	}

	private void initPlatforms() {
		currentPlatFormTag = kPlatformsStartTag;
		while (currentPlatFormTag < kPlatformsStartTag + kNumberPlatforms) {
			initPlatform();
			currentPlatFormTag++;
		}
		resetPlatforms();
	}

	private void initPlatform() {
		CCSprite child = CCSprite.sprite(plateImage, "plate");
		this.addChild(child, 3, currentPlatFormTag);
	}

	private void resetPlatforms() {
		currentPlatformY = -1;
		currentPlatFormTag = kPlatformsStartTag;
		currentBonusPlatformIndex = 0;
		currentMaxPlatformStep = 60.0f;
		currentBonusType = 0;
		platformCount = 0;
		while (currentPlatFormTag < kPlatformsStartTag + kNumberPlatforms) {
			resetPlatform();
			currentPlatFormTag++;
		}
	}

	private void resetPlatform() {
		if (currentPlatFormTag < 0) {
			currentPlatformY = 50.0f;
		} else {
			currentPlatformY += rand()
					% (int) (currentMaxPlatformStep - kMinPlatformStep)
					+ kMinPlatformStep;
			if (currentMaxPlatformStep < kMaxPlatformStep) {
				currentMaxPlatformStep += 0.5f;
			}
		}
		CCSprite platform = (CCSprite) getChildByTag(currentPlatFormTag);

		if (rand() % 2 == 1)
			platform.setScaleX(-1.0f);

		float x;
		CGSize size = platform.getContentSize();
		if (currentPlatformY == 30.0f) {
			x = 160.0f;
		} else {
			x = rand() % (GlobalConfig.deviceWidth - (int) size.width)
					+ size.width / 2;
		}
		platform.setPosition(x, currentPlatformY);
		platformCount++;

	}

	public void update(float d) {
		if (gameState != GameState.start) {
			return;
		}
		bird_pos.x += bird_vel.x * d;
		if (bird_vel.x < -30.0f && tofuLookingRight) {
			tofuLookingRight = false;
			tofu.setScaleX(-1.0f);
		} else if (bird_vel.x > 30.0f && !tofuLookingRight) {
			tofuLookingRight = true;
			tofu.setScaleX(1.0f);
		}
		CGSize bird_size = tofu.getContentSize();
		float max_x = GlobalConfig.deviceWidth- bird_size.width / 2;
		float min_x = bird_size.width / 2;
		if (bird_pos.x > max_x)
			bird_pos.x = min_x;
		if (bird_pos.x < min_x)
			bird_pos.x = max_x;

		bird_vel.y += bird_acc.y * d;
		bird_pos.y += bird_vel.y * d;

		int t = 0;
		if (bird_vel.y < 0) {

			for (t = kPlatformsStartTag; t < kPlatformsStartTag
					+ kNumberPlatforms; t++) {
				CCSprite platform = (CCSprite) getChildByTag(t);
				CGSize platform_size = platform.getContentSize();
				CGPoint platform_pos = platform.getPosition();
				max_x = platform_pos.x - platform_size.width / 2 - 20;
				min_x = platform_pos.x - platform_size.width / 2 + 20;
				float min_y = platform_pos.y
						+ (platform_size.height + bird_size.height) / 2
						- kPlatformTopPadding;
				
				if (bird_pos.x > max_x && bird_pos.x < min_x
						&& bird_pos.y > platform_pos.y && bird_pos.y < min_y) {
					jump();
				}
			}
			if (bird_pos.y < -bird_size.height / 2) {
				showHighscores();
			}
		} else if (bird_pos.y > GlobalConfig.deviceHeight/2) {
			float delta = bird_pos.y - GlobalConfig.deviceHeight/2;
			//bird_pos.y = GlobalConfig.deviceHeight/2;

			currentPlatformY -= delta;
			// t= kCloudsStartTag;
			// for(t;t<kCloudsStartTag+kNumClouds;t++){
			// CCSprite *cloud = (CCSprite *)batchNode->getChildByTag(t);
			// CCPoint pos =cloud->getPosition();
			// pos.y -= delta*(cloud->getScaleY())*0.8f;
			// if(pos.y < - cloud->getContentSize().height/2){
			// currentCloudTag =t;
			// resetCloud();
			// }else{
			// cloud->setPosition(pos);
			// }
			// }

			for (t = kPlatformsStartTag; t < kPlatformsStartTag
					+ kNumberPlatforms; t++) {
				CCSprite platform = (CCSprite) getChildByTag(t);
				CGPoint pos = platform.getPosition();
				if (pos.y < -platform.getContentSize().height / 2) {
					currentPlatFormTag = t;
					resetPlatform();
				} else {
					platform.setPosition(pos);
				}
			}
			score += (int)delta;
		}
		// if(bonus->isVisible()){
		// CCPoint pos = bonus->getPosition();
		// pos.y -= delta;
		// if(pos.y<- bonus->getContentSize().height/2){
		// resetBonus();
		// }else{
		// bonus->setPosition(pos);
		// }
		// }

		tofu.setPosition(bird_pos);
	}

	private void showHighscores() {
		FJLog.e("score =" + score);
		// CCScene gameScene =FirstLayer.getCCScene();
		// CCDirector.sharedDirector().replaceScene(gameScene);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startGame();
			}
		}).start();
	}

	private void jump() {
		bird_vel.y = 800.0f + Math.abs(bird_vel.x);
		SoundEngine.sharedEngine().playEffect(
				MyApplication.getGlobalContext(), R.raw.jump);
		score += bird_pos.y;
	}

	// FJLog.l("update=" + d);
	// // if (gameState != GameState.ing) {
	// // return;
	// // }
	// x_pos += x_speed * d;
	// float max_x = GlobalConfig.deviceWidth;
	// float min_x = 0;
	// if (x_pos > max_x)
	// x_pos = min_x;
	// if (x_pos < min_x)
	// x_pos = max_x;
	//
	// y_speed += y_acc * d;
	// y_pos += y_speed * d;
	// if (y_pos < 30) {
	// y_speed = 1200;
	// y_pos += y_speed * d;
	// }
	//
	// if (x_speed < 0) {
	// isDown = true;
	// } else {
	// isDown = false;
	// }
	//
	// if (isDown) {
	// if (checkCollision()) {
	// SoundEngine.sharedEngine().playEffect(
	// MyApplication.getGlobalContext(), R.raw.jump);
	// y_speed = 1000;
	// }
	// }
	//
	// tofu.setPosition(x_pos, y_pos);
	// last_tofu_y = (int) y_pos;
	//
	// }
	// protected boolean checkCollision() {
	//
	// CGSize tofusize = tofu.displayedFrame().getRect().size;
	//
	// for (CCSprite plate : plates) {
	// CGSize platesize = plate.displayedFrame().getRect().size;
	// if (tofu.getPosition().x < plate.getPosition().x
	// || tofu.getPosition().x > plate.getPosition().x
	// + platesize.width) {
	// return false;
	// }
	// if (last_tofu_y < plate.getPosition().y) {
	// return false;
	// }
	// if (Math.abs((tofu.getPosition().x - tofusize.getWidth() / 2)
	// - (plate.getPosition().x - platesize.getWidth() / 2)) < Math
	// .abs((tofusize.getWidth() + platesize.getWidth()) / 2)) {
	// return (Math
	// .abs((tofu.getPosition().y - tofusize.getHeight() / 2)
	// - (plate.getPosition().y - platesize
	// .getHeight() / 2)) < Math.abs((tofusize
	// .getHeight() + platesize.getHeight()) / 2));
	// }
	//
	// }
	//
	// return false;
	// }

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		FJLog.l("touch position,x=" + x + ",y=" + y);

		return super.ccTouchesEnded(event);
	}

	@Override
	public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
		super.ccAccelerometerChanged(accelX, accelY, accelZ);
		// FJLog.l("touch position,x=" + accelX + ",y=" + accelY + ",z=" +
		// accelZ);
		float accel_filter = 0.1f;
		bird_vel.x = bird_vel.x * accel_filter + accelX *(1.0f-accel_filter)*500.0f;

	}
}
