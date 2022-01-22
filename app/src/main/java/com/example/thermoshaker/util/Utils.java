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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
public class Utils {


    private static final String TAG = "Utils";

    public static void startDeskLaunch(){      /* 启动桌面 */
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


    public static void usbWrite() {
        UsbManager mUsbManager = (UsbManager) MyApplication.getInstance().getSystemService(Context.USB_SERVICE);
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(MyApplication.getInstance() /* Context or Activity */);
        PendingIntent permissionIntent = PendingIntent.getBroadcast(MyApplication.getInstance(), 0, new Intent(ACTION_USB_PERMISSION), 0);

        for (UsbMassStorageDevice device : devices) {
            mUsbManager.requestPermission(device.getUsbDevice(), permissionIntent);
            // before interacting with a device you need to call init()!
            try {
                device.init();
                // Only uses the first partition on the device
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                Log.d(TAG, "Capacity: " + currentFs.getCapacity());
                Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
                Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
                Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());
                Log.d(TAG, "getRootDirectory: " + currentFs.getRootDirectory());

                Content.currentFs = currentFs;
//                UsbFile root = currentFs.getRootDirectory();
//                UsbFile[] files = root.listFiles();
//                for (UsbFile file : files) {
//                    Log.d(TAG, file.getName());
//                    if (file.isDirectory()) {
////                Log.d(TAG, "" + file.getLength());
//                    }
//                }
//                device.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




















}
