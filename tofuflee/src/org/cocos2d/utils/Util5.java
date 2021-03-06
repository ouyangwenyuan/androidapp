package org.cocos2d.utils;

import android.view.MotionEvent;

public class Util5 {
	public static int getPointerId(MotionEvent event, int pindex) {
		return event.getPointerId(pindex);
	}

	public static float getX(MotionEvent event, int pindex) {
		return event.getX(pindex);
	}

	public static float getY(MotionEvent event, int pindex) {
		return event.getY(pindex);
	}
	public static float getHistoryX(MotionEvent event, int pindex) {
		return event.getHistoricalX(pindex,event.getHistorySize() - 1);
	}

	public static float getHistoryY(MotionEvent event, int pindex) {
		return event.getHistoricalY(pindex,event.getHistorySize() - 1);
	}
}
