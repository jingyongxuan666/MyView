package com.jyx.barrageview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Mzc on 2015/9/9.
 */
public class DpPxUtils {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static int STATUS_HEIGHT;
    public static float DENSITY;
    public static float FONT_SCALE;

    public static void init(Context context) {
        getDeviceMetrics(context);
    }

    private static void getDeviceMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            STATUS_HEIGHT = context.getResources().getDimensionPixelSize(resourceId);
        }
        SCREEN_WIDTH = metric.widthPixels;
        SCREEN_HEIGHT = metric.heightPixels;
        DENSITY = metric.density;
        FONT_SCALE = metric.scaledDensity;
    }

    /**
     * dp转成px
     *
     * @param dpValue
     * @return
     */
    public static int dpToPx(float dpValue) {
        return (int) (dpValue * DENSITY + 0.5f);
    }

    /**
     * px转成dp
     *
     * @param pxValue
     * @return
     */
    public static int pxToDp(float pxValue) {
        return (int) (pxValue / DENSITY + 0.5f);
    }

    /**
     * dp转px
     *
     * @param i
     * @return
     */
    public static int dipToPx(int i) {
        return (int) (0.5D + (double) (getDensity() * (float) i));
    }

    public static float getDensity() {
        return DENSITY;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getStatusHeight() {
        return STATUS_HEIGHT;
    }

}
