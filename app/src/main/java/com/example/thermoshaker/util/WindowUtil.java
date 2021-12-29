package com.example.thermoshaker.util;


import com.example.thermoshaker.base.MyApplication;

/**
 * 手机窗口工具类
 */
public class WindowUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int toPx(float dpValue) {
        float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int toDp(float pxValue) {
        float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获得手机屏幕宽度得到像素px
     */
    public static int getWidth() {
        return MyApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获得手机屏幕高度得到像素px
     */
    public static int getHeight() {
        return MyApplication.getInstance().getResources().getDisplayMetrics().heightPixels;
    }
}
