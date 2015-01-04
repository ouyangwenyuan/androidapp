package com.kitty.usedengine;

import org.cocos2d.actions.CCTimer;
import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.layers.CCLayer;

public class Main extends CCLayer implements UpdateCallback {
	public enum ImageType {
		tofu, spriteManager, scoreLabel, block, plate, bonus;
	}

	public enum BonusType {
		bomb, hight, slow, skid, broken;
	}

	public static int bonusCount =12;
	public static int plateMaxDistance =300;
	public static int plateMinDistance =50;
	public static int plateCount =10;
	public static int plateTopPadding =10;
	public static int bonusMaxDistance =500;
	public static int bonusMinDistance =30;
	
	int currentPlate;
	private UpdateCallback callback;
	
	public Main(){
		
		this.schedule(callback);
	}

	public void reset() {
	}
	
	public void step(CCTimer dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float d) {
		// TODO Auto-generated method stub
		
	}
}
