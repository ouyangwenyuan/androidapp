package com.kitty.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.kitty.control.GameActivity;
import com.kitty.tofuflee.R;
import com.kitty.utils.FJLog;

public class GuideView extends View {

    public GuideView(Context context) {
        super(context);
        initField(context);
    }

    private Bitmap bgBitmap;
    private Paint paint;
    private Rect rect;
    private boolean isPressed;

    private void initField(Context context) {
        int height = getResources().getDisplayMetrics().heightPixels;
        int width = getResources().getDisplayMetrics().widthPixels;
        rect = new Rect(width / 2 - 100, height / 2 - 100, width / 2 + 100, height / 2 + 100);
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tofu_logo_final);
        //bgBitmap = Bitmap.createBitmap(bgBitmap, 0, 0, width, height);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bgBitmap, 0, 0, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        FJLog.l("touch position,x=" + x + ",y=" + y);
        if (rect.contains(x, y) && !isPressed) {
            isPressed = true;
            //TODO
            getContext().startActivity(new Intent(getContext(), GameActivity.class));
        }
        return super.onTouchEvent(event);
    }
}
