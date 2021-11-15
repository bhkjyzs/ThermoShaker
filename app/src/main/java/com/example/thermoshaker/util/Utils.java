package com.example.thermoshaker.util;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.example.thermoshaker.base.MyApplication;

public class Utils {



    public static void startDeskLaunch(){      /* 启动桌面 */
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.launcher", "com.android.launcher2.Launcher");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            MyApplication.getInstance().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
//./keytool-importkeypair -k ./platform.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias platform

    }

}
