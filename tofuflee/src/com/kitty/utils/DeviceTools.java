package com.kitty.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kitty.global.GlobalConfig;

public class DeviceTools {
    public static void getDeviceInfo(Context context) {
        //        DisplayMetrics metrics = new DisplayMetrics();
        //        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        GlobalConfig.deviceWidth = metrics.widthPixels;
        GlobalConfig.deviceHeight = metrics.heightPixels;
    }

    /**
     * 根据缩放因子缩放
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.i("info", width + " " + height);
            Matrix matrix = new Matrix();
            matrix.postScale(GlobalConfig.scaleWidth, GlobalConfig.scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }

    /**
     * 根据新宽高缩放
     * 
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleSize = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleSize, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }

}
