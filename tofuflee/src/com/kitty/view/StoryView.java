package com.kitty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kitty.tofuflee.R;
import com.kitty.utils.FJLog;

public class StoryView extends SurfaceView implements SurfaceHolder.Callback2 {
    private Paint paint;
    private Bitmap bgBitmap;
    private SurfaceHolder holder;
    private Rect rect = null;
    private boolean isPressed;

    public StoryView(Context context) {
        super(context);
        initField(context);
    }

    private void initField(Context context) {
        //        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        //        int width = displayMetrics.widthPixels;
        //        int height = displayMetrics.heightPixels;
        rect = new Rect(0, 0, 200, 200);
        paint = new Paint();
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tofu1);
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        FJLog.l("storyView onDraw" + canvas);
        canvas.drawBitmap(bgBitmap, 0, 0, paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (holder) {
            Canvas canvas = holder.lockCanvas();
            try {
                onDraw(canvas);
            } finally {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        FJLog.l("touch position,x=" + x + ",y=" + y);
        //        if (rect.contains(x, y) && !isPressed) {
        //            isPressed = true;
        //            //TODO
        //            getContext().startActivity(new Intent(getContext(), GameActivity.class));
        //        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

}
