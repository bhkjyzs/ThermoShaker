package com.example.thermoshaker.util;

import static com.example.thermoshaker.util.usb.USBBroadCastReceiver.ACTION_USB_PERMISSION;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MyApplication;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
public class Utils {


    private static final String TAG = "Utils";

    public static void startDeskLaunch() {      /* 启动桌面 */
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.launcher3", "com.android.launcher3.Launcher");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            MyApplication.getInstance().startActivity(intent);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
//./keytool-importkeypair -k ./platform.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias platform

    }


    /* byte转int */
    public static int byteArrayToInt( final byte[] buffer, int offset){
        int value = (buffer[offset] & 0xff) << 8;
        value += buffer[offset + 1] & 0xff;
        /* 判断最高位是否为负 */
        if (value >> 15 == 1)
            value = value | 0xffff0000;
        return value;
    }

    /* int转byte */
    public static void intTobyteArray( int value, byte[] buffer, int offset){
        buffer[offset] = (byte) (value >> 8);
        buffer[offset + 1] = (byte) value;
    }
    //int转化为byte数组 len为字节数
    public static void intTobyteArrayTime4(int value, byte[] buffer, int offset) {
        buffer[offset] = (byte) (value >> 24);
        buffer[offset + 1] = (byte) (value >> 16);
        buffer[offset + 2] = (byte) (value >> 8);
        buffer[offset + 3] = (byte) value;

    }
    /* byte转int */
    public static int byteArrayToInt4( final byte[] buffer, int offset){
        int value = (buffer[offset] & 0xff);
        value += buffer[offset + 1] & 0xff00;
        value += buffer[offset + 2] & 0xff0000;
        value += buffer[offset + 3] & 0xff000000;

        /* 判断最高位是否为负 */
        if (value >> 15 == 1)
            value = value | 0xffff0000;
        return value;
    }


    public static void hookWebView() {
        Class<?> factoryClass = null;
        try {
            factoryClass = Class.forName("android.webkit.WebViewFactory");
            Method getProviderClassMethod = null;
            Object sProviderInstance = null;

            if (Build.VERSION.SDK_INT == 23) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> constructor = providerClass.getConstructor(delegateClass);
                if (constructor != null) {
                    constructor.setAccessible(true);
                    Constructor<?> constructor2 = delegateClass.getDeclaredConstructor();
                    constructor2.setAccessible(true);
                    sProviderInstance = constructor.newInstance(constructor2.newInstance());
                }
            } else if (Build.VERSION.SDK_INT == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> constructor = providerClass.getConstructor(delegateClass);
                if (constructor != null) {
                    constructor.setAccessible(true);
                    Constructor<?> constructor2 = delegateClass.getDeclaredConstructor();
                    constructor2.setAccessible(true);
                    sProviderInstance = constructor.newInstance(constructor2.newInstance());
                }
            } else if (Build.VERSION.SDK_INT == 21) {// Android 21无WebView安全限制
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                sProviderInstance = providerClass.newInstance();
            }
            if (sProviderInstance != null) {
                Field field = factoryClass.getDeclaredField("sProviderInstance");
                field.setAccessible(true);
                field.set("sProviderInstance", sProviderInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
