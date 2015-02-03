package com.kitty.usedengine;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.transitions.CCFlipXTransition;
import org.cocos2d.transitions.CCTransitionScene.tOrientation;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.kitty.control.MyApplication;
import com.kitty.global.GlobalConfig;
import com.kitty.tofuflee.R;
import com.kitty.utils.FJLog;

public class FirstLayer extends CCLayer {

	private float x_pos = 160;
	private float y_pos = 160;

	private float y_speed = 0;

	private float y_acc = -1200;

	private CCSprite tofu;
	private CCSprite plate;

	private CCSprite exitBtnSprite;

	public FirstLayer() {
		this.setIsTouchEnabled(true);

		Bitmap image = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.tofu_logo_final);
		CCSprite sprite = CCSprite.sprite(image, "tofu_logo");
		FJLog.l("image width=" + image.getWidth() + ",height="
				+ image.getHeight());
		int width = GlobalConfig.deviceWidth / 2;
		int height = GlobalConfig.deviceHeight / 2;
		sprite.setPosition(width, height);

		FJLog.l("bg position=" + sprite.getPosition());
		this.addChild(sprite,-1);

		final Bitmap tofuImage = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.tofu_l);
		tofu = CCSprite.sprite(tofuImage, "tofu");
		tofu.setPosition(CGPoint.ccp(width, 0));
		this.addChild(tofu,2);
		FJLog.l("tofu position=" + tofu.getPosition());
		final Bitmap plateImage = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.p_pas_1);
		plate = CCSprite.sprite(plateImage, "plate");
		plate.setPosition(CGPoint.ccp(160, 160));
		this.addChild(plate,1);
		FJLog.l("plate position=" + plate.getPosition());

		final Bitmap exitBtn = BitmapFactory.decodeResource(MyApplication
				.getGlobalContext().getResources(), R.drawable.btn_cancel);
		exitBtnSprite = CCSprite.sprite(exitBtn, "exitBtn");
		exitBtnSprite.setPosition(CGPoint.ccp(exitBtn.getWidth() / 2,
				(GlobalConfig.deviceHeight - exitBtn.getHeight())));
		this.addChild(exitBtnSprite,1);
		FJLog.l("btn position=" + exitBtnSprite.getPosition());
		Rect exitRect = new Rect(
				(int) (exitBtnSprite.getPosition().x),
				(int) (exitBtnSprite.getPosition().y),
				(int) (exitBtnSprite.getPosition().x + 20 + exitBtn.getWidth()),
				(int) (exitBtnSprite.getPosition().y + exitBtn.getHeight()));
		CGRect rect = exitBtnSprite.displayedFrame().getRect();
		FJLog.l("btn exitRect=" + exitRect + ",btn rect =" + rect);

		this.schedule(new UpdateCallback() {

			@Override
			public void update(float d) {

				y_speed += y_acc * d;
				y_pos += y_speed * d;

				if (y_pos >= GlobalConfig.deviceHeight - tofuImage.getHeight()
						/ 2) {
					y_speed = -y_speed;
					y_pos += y_speed * d;

				} else if (y_pos < plate.getPosition().y + 30) {
					SoundEngine.sharedEngine().playEffect(
							MyApplication.getGlobalContext(), R.raw.jump);
					y_speed = 700;
					y_pos += y_speed * d;
				}

				tofu.setPosition(x_pos, y_pos);
			}
		});
	}
	private static FirstLayer instance = new FirstLayer(); 
	public static CCScene getCCScene() {
		CCScene gameScene = CCScene.node();
		if(instance != null)
		gameScene.addChild(instance);

		return gameScene;
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		Rect toucharea = new Rect(0, 0, 200, 100);
		if (toucharea.contains(x, y)) {
			exitBtnSprite.setScale(2.0f);
		}
		return super.ccTouchesBegan(event);
	}
	CCScene gameScene = null;
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		FJLog.l("touch position,x=" + x + ",y=" + y);
		Rect toucharea = new Rect(0, 0, 200, 100);
		if (toucharea.contains(x, y)) {
			exitBtnSprite.setScale(1.0f);
		if(gameScene == null){
			gameScene =new SecondLayer().getCCScene();
			CCDirector.sharedDirector().pushScene(gameScene);
			CCDirector.sharedDirector().replaceScene(gameScene);
		}else{
			CCDirector.sharedDirector().popScene();
		}
			CCFlipXTransition.transition(1.0f, gameScene,
					tOrientation.kOrientationRightOver);
		}
		return super.ccTouchesEnded(event);
	}

}
