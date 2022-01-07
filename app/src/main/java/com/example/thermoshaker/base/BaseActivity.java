package com.example.thermoshaker.base;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.thermoshaker.R;
import com.example.thermoshaker.model.Event;
import com.example.thermoshaker.serial.DataUtils;
import com.example.thermoshaker.util.AppManager;
import com.example.thermoshaker.util.BindEventBus;
import com.example.thermoshaker.util.CloseBarUtil;
import com.example.thermoshaker.util.EventBusUtils;
import com.example.thermoshaker.util.LanguageUtil;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.licheedev.myutils.LogPlus;

import org.greenrobot.eventbus.EventBus;
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
    USBBroadcastReceiver usbBroadcastReceiver;

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
    @Subscribe
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initUsb();
        initSer();
//        celiang();

    }

    private void initSer() {
        MyApplication.getInstance().mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                final byte[] finalBytes = bytes;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogPlus.d(String.format("接收\n%s", new String(finalBytes)));
                    }
                });
            }

            @Override
            public void onDataSent(byte[] bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String s = DataUtils.ByteArrToHex(bytes);
                        LogPlus.d(String.format("发送\n%s", s+""));

                    }
                });
            }
        });
    }

    private void celiang() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        Log.e(TAG , "xdpi=" + xdpi + "; ydpi=" + ydpi);
        Log.e(TAG, "density=" + density + "; densityDPI=" + densityDPI);
        int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
        int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
        Log.e(TAG , "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);
    }

    private void initUsb(){
        /* 检查usb */
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceHashMap = usbManager.getDeviceList();
        for (Map.Entry<String, UsbDevice> entry : deviceHashMap.entrySet()) {
            if (entry.getValue().getInterface(0).getInterfaceClass() == 8) {
                Log.d(TAG, "USB准备就绪");
                Content.usb_state = Intent.ACTION_MEDIA_MOUNTED;
                Content.usb_path = "/storage/usbhost1";
            }
        }
        Log.i(TAG, "init usbBroadcastReceiver");
        usbBroadcastReceiver = new USBBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        intentFilter.addAction("android.hardware.action.USB_DISCONNECTED");
        intentFilter.addAction("android.hardware.action.USB_CONNECTED");
        intentFilter.addAction("android.intent.action.UMS_CONNECTED");
        intentFilter.addAction("android.intent.action.UMS_DISCONNECTED");
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");

        registerReceiver(usbBroadcastReceiver, intentFilter);
    };

    @Override
    protected void onPause() {
        super.onPause();

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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBusUtils.unregister(this);
        }
        if (null != usbBroadcastReceiver) {
            unregisterReceiver(usbBroadcastReceiver);
        }

        if(handler!=null){
            handler.removeCallbacks(null);
        }
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

    // 继承BroadcastReceivre基类
    public class USBBroadcastReceiver extends BroadcastReceiver {
        // 接收到广播后，则自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_MEDIA_CHECKING:
                    Content.usb_state = Intent.ACTION_MEDIA_CHECKING;
                    //Toast.makeText(context,R.string.usb_info_mounting,Toast.LENGTH_SHORT);
                    Content.usb_path = "";
                    Log.i(TAG, "正在挂载U盘");
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                    Content.usb_state = Intent.ACTION_MEDIA_MOUNTED;
                    //Toast.makeText(context,R.string.usb_info_mount_success,Toast.LENGTH_SHORT);
                    Log.i(TAG, getString(R.string.mounting_u_disk));
                    Uri uri = intent.getData();
                    if (uri != null) {
                        Content.usb_path = uri.getPath();
                    }
                    Log.i(TAG, Content.usb_path);

                    Toast.makeText(BaseActivity.this, getString(R.string.mounting_u_disk), Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_MEDIA_EJECT:
                    //Toast.makeText(context,R.string.usb_info_umount_success,Toast.LENGTH_SHORT);
                    Content.usb_state = Intent.ACTION_MEDIA_EJECT;
                    Content.usb_path = "";
                    Log.i(TAG, getString(R.string.removed_u_disk));
                    Toast.makeText(BaseActivity.this, getString(R.string.removed_u_disk), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


}
