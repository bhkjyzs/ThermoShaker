package com.example.thermoshaker.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class BroadcastManager {


    private Context mContext;
    private static BroadcastManager instance;
    private Map<String, BroadcastReceiver> receiverMap;


    /**
     * 构造方法
     *
     * @param context
     */
    private BroadcastManager(Context context) {
        this.mContext = context;
        receiverMap = new HashMap<String, BroadcastReceiver>();
    }


    /**
     * [获取BroadcastManager实例，单例模式实现]
     *
     * @param context
     * @return
     */
    public static BroadcastManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BroadcastManager.class) {
                if (instance == null) {
                    instance = new BroadcastManager(context);
                }
                }
        }
        return instance;
    }


    /**
     * 添加Action,做广播的初始化
     *
     * @param
     */
    public void addAction(String action, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            mContext.registerReceiver(receiver, filter);
            receiverMap.put(action, receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加Action,做广播的初始化
     *
     * @param
     */
    public void addAction(String[] actions, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            for(String action:actions){
                filter.addAction(action);
            }
            mContext.registerReceiver(receiver, filter);
            for(String action:actions) {
                receiverMap.put(action, receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送广播
     *
     * @param action 唯一码
     */
    public void sendBroadcast(String action) {
        sendBroadcast(action, "");
    }


    /**
     * 发送广播
     *
     * @param action 唯一码
     * @param obj    参数
     */
    public void sendBroadcast(String action, Object obj) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra("result", obj.toString());
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 销毁广播
     * @param actions
     */
    public void destroy(String... actions) {
        try {
            if (receiverMap != null) {
                for(String action:actions){
                    BroadcastReceiver receiver = receiverMap.get(action);
                    if (receiver != null) {
                        mContext.unregisterReceiver(receiver);
                    }
                }
            }
        } catch (IllegalArgumentException e){
            Log.d("Broadcastmanager",e.toString());
        }
    }
}
