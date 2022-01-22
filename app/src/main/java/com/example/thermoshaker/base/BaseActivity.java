package com.example.thermoshaker.base;

import static com.example.thermoshaker.util.usb.USBBroadCastReceiver.ACTION_USB_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import androidx.core.app.ActivityCompat;

import com.example.thermoshaker.MainActivity;
import com.example.thermoshaker.R;
import com.example.thermoshaker.model.Event;
import com.example.thermoshaker.util.AppManager;
import com.example.thermoshaker.util.BroadcastManager;
import com.example.thermoshaker.util.CloseBarUtil;
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
    public static final String ERROR_ACTION = "ERROR_ACTION";
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "com.android.example.USB_PERMISSION",
    };

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public UsbHelper usbHelper;
    public FileSystem currentFs;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case ACTION_USB_PERMISSION:
                    Log.d(TAG, "onReceive: 接收到广播");
                    break;
            }
        }
    };



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
//        celiang();

    }


    public void initUsb() {

        if (XXPermissions.isHasPermission(this, Permission.Group.STORAGE)) {

        }

        //绑定广播
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        //注册接收广播
        registerReceiver(mUsbReceiver, filter);

        usbHelper  = new UsbHelper(this, new USBBroadCastReceiver.UsbListener() {
            @Override
            public void insertUsb(UsbDevice device_add) {
                Log.d(TAG,device_add.toString()+"     device_add");
                Content.usb_state = Intent.ACTION_MEDIA_MOUNTED;
                ToastUtil.show(BaseActivity.this,getString(R.string.mounting_u_disk));

            }

            @Override
            public void removeUsb(UsbDevice device_remove) {
                Log.d(TAG,device_remove.toString()+"     device_remove");
                Content.usb_state = Intent.ACTION_MEDIA_EJECT;
                ToastUtil.show(BaseActivity.this,getString(R.string.removed_u_disk));

            }

            @Override
            public void getReadUsbPermission(UsbDevice usbDevice) {
                Log.d(TAG,usbDevice.toString()+"     getReadUsbPermission");
                Content.usb_state = Intent.ACTION_MEDIA_MOUNTED;
//                ToastUtil.show(BaseActivity.this,getString(R.string.mounting_u_disk));
            }

            @Override
            public void failedReadUsb(UsbDevice usbDevice) {
                Log.d(TAG,usbDevice.toString()+"    failedReadUsb");

            }
        });
    }
    public  void usbWrite() {
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
                String command = intent.getAction();
                if(tipsDialog==null){
                    tipsDialog = new TipsDialog(BaseActivity.this,getString(R.string.dialog_info_communication_failed));
                    tipsDialog.show();
                }else {
                    if(!tipsDialog.isShowing()){
                        tipsDialog.show();
                    }
                }


            }
        });
    };


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
        if(mUsbReceiver!=null){
            unregisterReceiver(mUsbReceiver);
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



}
