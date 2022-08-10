package com.example.thermoshaker.util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcel;
import android.os.UserHandle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.ui.run.RunActivity;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class websocketClass extends WebSocketServer {
    private final static String TAG = websocketClass.class.getSimpleName();
    private boolean isOpen = false;

    public websocketClass(int port) {
        super(new InetSocketAddress(port));

        setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Log.d(TAG, "onOpen");

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Log.d(TAG, "onClose");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMessage(WebSocket arg0, String arg1) {
        JSONObject jsonObject = JSON.parseObject(arg1);
        Log.d(TAG, "onMessage" + jsonObject.toJSONString() + "");
        String action = jsonObject.getString("action");
        String target = jsonObject.getString("target");
        switch (action) {
            case "read":
                if(jsonObject.getString("code").equals("file")){
                    String tempfile = DataUtil.readData(DataUtil.data_path + DataUtil.param_name + "temp.Tso");
                    ProgramInfo programInfofile = JSON.parseObject(tempfile, ProgramInfo.class);
                    Map<String, Object> mapReadRunfile  = new HashMap<String, Object>();
                    JSON.toJSON(programInfofile);
                    mapReadRunfile.put("programInfofile", JSON.toJSON(programInfofile));
                    mapReadRunfile.put("runState",MyApplication.getInstance().isRunWork);
                    jsonObject.put("target-data", mapReadRunfile);
                    arg0.send(jsonObject.toJSONString());

                }else {
                    MyApplication app = MyApplication.getInstance();
                    Map<String, Object> mapReadRun = new HashMap<String, Object>();
                    mapReadRun.put("state",app.runningClass.getRunState()+"");
                    mapReadRun.put("step",app.runningClass.getRunStep()+"");
                    mapReadRun.put("CUREndTimeStr",app.runningClass.getCUREndTimeStr()+"");
                    mapReadRun.put("DispTemp1A",app.runningClass.getDispTemp1A()+"");
                    mapReadRun.put("LidDispTemp",app.runningClass.getLidDispTemp()+"");
                    mapReadRun.put("RunCir",app.runningClass.getRunCir()+"");
                    mapReadRun.put("StepSurplusStr",app.runningClass.getStepSurplusStr()+"");
                    mapReadRun.put("runState",MyApplication.getInstance().isRunWork);

                    jsonObject.put("target-data", mapReadRun);
                    Log.d(TAG, jsonObject.toJSONString() + "");
                    arg0.send(jsonObject.toJSONString());

                }


                break;
            case "write":
                String code = jsonObject.getString("code");
                String action_data = jsonObject.getString("action-data");
                try {
                    Intent intent = new Intent();
                    intent.putExtra("action-data", action_data);
                    intent.putExtra("code", code);
                    intent.setAction(RunActivity.PHONE_RUN);
                    Parcel parcel = Parcel.obtain();
                    parcel.writeInt(-1);
                    MyApplication.getInstance().sendBroadcastAsUser(intent,new UserHandle(parcel));

                } catch (Exception e) {

                    Log.d(TAG, e.getMessage() + "");
                }
                break;

            case "init":
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("DeviceName", LogSaveUtil.getName(MyApplication.getInstance()));
                map.put("DeviceType", "TDM");
                jsonObject.put("target-data", map);
                arg0.send(jsonObject.toJSONString());
                break;
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Log.d(TAG, "onError" + e.getMessage() + "");

    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");

    }
}
