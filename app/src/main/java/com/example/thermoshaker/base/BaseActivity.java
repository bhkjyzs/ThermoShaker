package com.example.thermoshaker.base;

import static com.example.thermoshaker.util.usb.USBBroadCastReceiver.ACTION_USB_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.example.thermoshaker.MainActivity;
import com.example.thermoshaker.R;
import com.example.thermoshaker.model.Event;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.serial.ByteUtil;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.ui.run.RunActivity;
import com.example.thermoshaker.util.AppManager;
import com.example.thermoshaker.util.BroadcastManager;
import com.example.thermoshaker.util.CloseBarUtil;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.EventBusUtils;
import com.example.thermoshaker.util.LanguageUtil;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.Utils;
import com.example.thermoshaker.util.dialog.TipsDialog;
import com.example.thermoshaker.util.usb.USBBroadCastReceiver;
import com.example.thermoshaker.util.usb.UsbHelper;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.licheedev.myutils.LogPlus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    private int update_system_time=1;
    private int update_system=1000;
    private TextView tv_times;
    private TipsDialog tipsDialog;
    public boolean isHide = true;//页面是否可见
    public static final String ERROR_ACTION = "ERROR_ACTION";
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "com.android.example.USB_PERMISSION",
    };

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static UsbHelper usbHelper;
    public static FileSystem currentFs;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isHide = true;
        /* 隐藏安卓导航栏 */
        MyApplication app = MyApplication.getInstance();
//        if (Build.VERSION.SDK_INT == 22)
//            hideStatusBar();
//        else
//            app.exec(MainType.CMD.ShowBar.getValue());
//            Log.d(TAG,Build.VERSION.SDK_INT+"");
    }
    /* 隐藏系统导航栏 */
    public void hideStatusBar() {
        Settings.System.putInt(getContentResolver(), "systembar_hide", 1);
        Intent i = new Intent("com.tchip.changeBarHideStatus");
        sendBroadcast(i);
    }
    private Handler handler =  new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    updateSystemTime(tv_times);
                    handler.sendEmptyMessageDelayed(update_system_time,update_system);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }

        }
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscribe
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        Locale temp = LanguageUtil.getLocale(this);
        if(temp==null){
            temp = Locale.CHINA;
        }
        for (int i = 0; i < 5; i++) {
            boolean isSuccess = LanguageUtil.updateLocale(this, temp);
            if (isSuccess) {
                break;
            }
        }
        setContentView(getLayout());
        CloseBarUtil.closeBar();
        if (isRegisterEventBus()) {
            EventBusUtils.register(this);
        }
        initView();
        initDate();
        AppManager.getAppManager().addActivity(this);
        hideBottomUIMenu();
        /* 屏蔽键盘 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        /* 隐藏软键盘 */
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }




        bradCast();

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
                currentFs = device.getPartitions().get(0).getFileSystem();
                Log.d(TAG, "Capacity: " + currentFs.getCapacity());
                Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
                Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
                Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());
                Log.d(TAG, "getRootDirectory: " + currentFs.getRootDirectory());

//                device.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void bradCast(){
        BroadcastManager.getInstance(this).addAction(ERROR_ACTION, new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String result = intent.getStringExtra("result");
                if(isHide){

                if(tipsDialog==null){
                    tipsDialog = new TipsDialog(BaseActivity.this,result+"");
                    tipsDialog.show();
                }else {
                    if(!tipsDialog.isShowing()){
                        tipsDialog.show();
                    }
                }
                }

            }
        });
    };



    public void runFile(ProgramInfo programInfo){


        if(getProgramByte(programInfo)){

            String jsonOutput = JSON.toJSONString(programInfo);
            DataUtil.writeData(jsonOutput, DataUtil.data_path + DataUtil.param_name, "temp.Tso", false);
            Intent intent = new Intent(this, RunActivity.class);
            intent.putExtra("programInfo", programInfo);
            startActivity(intent);
            overridePendingTransition(0, 0);
            MyApplication.getInstance().isRunWork = true;

        }
        SystemClock.sleep(500);
        Intent intentRun = new Intent(UartServer.MSG);
        intentRun.putExtra("serialport", new UartClass(null, UartType.OT_RUN_BYTE));
        sendBroadcast(intentRun);
    }






    @Override
    protected void onPause() {
        super.onPause();
        isHide = false;

    }


    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }


    protected abstract int getLayout();
    protected abstract void initView();
    protected abstract void initDate();

    //方法名可以随意，但是一定要加上@Subscribe注解，并指定线程
    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onEvent(Event event){
        Log.d(TAG,event.toString());
        // do something
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBusUtils.unregister(this);
        }

        if(handler!=null){
            handler.removeCallbacks(null);
        }
        BroadcastManager.getInstance(this).destroy(ERROR_ACTION);

    }
    public void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
            Window _window = getWindow();
            WindowManager.LayoutParams params = _window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
            _window.setAttributes(params);
        }
    }

    public void updateSystemTime(TextView tv){
        tv_times = tv;
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        Locale temp = LanguageUtil.getLocale(this);
        if(temp==null){
            temp = Locale.CHINA;
        }
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy/MM/dd EEEE HH:mm",temp);// HH:mm:ss
        tv.setText(simpleDateFormatDate.format(date));

    }


    /**
     * 文件数据
     * @param programInfo
     * @return
     */
    public boolean getProgramByte(ProgramInfo programInfo) {
        try {
            byte[] bytes1 = new byte[2];
            int ceshi = 3000;
         Utils.intTobyteArray(3000,bytes1,0);
            Log.d(TAG, ByteUtil.bytesToHexString(bytes1)+"");


            byte[] bytes = new byte[116];
            int[] runSetting = MyApplication.getInstance().appClass.getRunSetting();
        //升温设置
        // 0：升温动作同步
        //1：先升温后动作
        //2-51：升温到低于目标温度（1-50℃）开始动作
        bytes[0] = (byte) runSetting[0];
        //程序提前几步开始加热   1-3：程序提前1-3步开始加热
        bytes[1] = (byte) runSetting[1];
        //加热通道开关  0：对应加热通道关闭   1：对应加热通道开启
        bytes[2] = (byte) runSetting[2];
        //步骤总数
        bytes[3] = (byte) (programInfo.getStepList().size());
        //当前步骤 当前程序开始运行步骤
        bytes[4] = (byte) (1);
        //循环开关
        bytes[5] = (byte) (programInfo.isLoopEnable()==false? 0:1);
        //开始步骤
        bytes[6] = (byte) programInfo.getLoopStart();
        //结束步骤
        bytes[7] = (byte) programInfo.getLoopEnd();
        //循环次数
        bytes[8] = (byte) programInfo.getLoopNum();
        //热盖温度
        Utils.intTobyteArray(Math.round(programInfo.getLidTm()*100F),bytes,9);
        for (int i = 0; i < programInfo.getStepList().size(); i++) {
            int index = 11 + i * 21;
            ProgramStep programStep = programInfo.getStepList().get(i);
            //设置温度
            Utils.intTobyteArray(Math.round(programStep.getTemperature()*100F),bytes,0+index);
            //升温速率
            if(programStep.getUpSpeed()==3.0){
                bytes[2+index] = 4;

            }else if(programStep.getUpSpeed()==2.0){
                bytes[2+index] = 3;

            }else if(programStep.getUpSpeed()==1.0){
                bytes[2+index] = 2;

            }else if(programStep.getUpSpeed()==0.1){
                bytes[2+index] = 1;

            }else if (programStep.getUpSpeed()==5.0){
                bytes[2+index] = 5;

            }
            //降温速率
            if(programStep.getDownSpeed()==1.0){
                bytes[3+index] = 3;

            }else if(programStep.getDownSpeed()==0.5){
                bytes[3+index] = 2;

            }else if(programStep.getDownSpeed()==0.1){
                bytes[3+index] = 1;
            }else if(programStep.getDownSpeed()==5.0){
                bytes[3+index] = 4;

            }

            //电机转速
            Utils.intTobyteArray(programStep.getZSpeed(),bytes,4+index);
            //方向
            switch (programStep.getDirection())
            {
                case Forward:
                    bytes[6+index] =0;
                    break;
                case Reversal:
                    bytes[6+index] =1;
                    break;
                case ForwardAndReverse:
                    bytes[6+index] =2;
                    break;
            }
            //步骤时间
            Date date = new Date(programStep.getTime());
            Utils.intTobyteArray(Math.round(date.getTime()/1000L),bytes,7+index);
            //混匀方式
            bytes[9+index] = (byte) programStep.getMixingMode();
            //持续时间
            Date dateContinue = new Date(programStep.getContinued());
            Utils.intTobyteArray(Math.round(dateContinue.getTime()/1000L),bytes,10+index);
            //间隔时间
            Date dateIntermission = new Date(programStep.getIntermission());
            Utils.intTobyteArray(Math.round(dateIntermission.getTime()/1000L),bytes,12+index);
            //混合起点
            bytes[14+index] = (byte) programStep.getBlendStart();
        }

        /* 发送运行文件 */
        int size = 116;
        byte[] array = new byte[size + 8];
        array[0] = (byte) 0xaa;
        array[1] = (byte) 0x3d;
        array[2] = (byte) 0xda;
        array[3] = (byte) (size >> 8);
        array[4] = (byte) size;
        System.arraycopy(bytes, 0, array, 5, size);
        Intent intent2 = new Intent(UartServer.MSG);
        intent2.putExtra("serialport", new UartClass(null, array));
        sendBroadcast(intent2);
            return true;
        }catch (Exception e){
            LogPlus.d(e.getMessage()+"");
        }
        return false;
    }





}
