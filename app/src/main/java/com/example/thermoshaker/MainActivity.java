package com.example.thermoshaker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.serial.DataUtils;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.serial.uart.running.TdfileRunType;
import com.example.thermoshaker.ui.fast.FastActivity;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.ui.run.RunActivity;
import com.example.thermoshaker.ui.setting.SettingActivity;
import com.example.thermoshaker.util.BroadcastManager;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.LanguageUtil;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.Utils;
import com.example.thermoshaker.util.dialog.DebugDialog;
import com.example.thermoshaker.util.dialog.base.CustomKeyEditDialog;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;
import com.example.thermoshaker.util.usb.USBBroadCastReceiver;
import com.example.thermoshaker.util.usb.UsbHelper;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.licheedev.myutils.LogPlus;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public final static String MSG = MainActivity.class.getName();

    private int update_system_time = 1;
    private int update_system = 1000;
    private int ChooseFilePos = -1;


    private LinearLayout ll_file, ll_setting, ll_run, ll_inching;
    private TextView tv_date, tv_time, tv_file, tv_setting, tv_running, tv_inching;
    private Button btn_dian;
    private static MainActivity instance;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private final int msgUart = 2;// 动画的消息号
    private final int msgUartDelayed = 2000;
    private int errorDispTime = 10 * 1000 / msgUartDelayed; // 错误报警时间
    private Intent intentUart; // 给定时器查询运行数据

    private Button dialog_factory_no_msg;
    private LinearLayout mll_haveMsg;
    private TextView tv_con_msg;
    public static MainActivity getInstance() {
        return instance;
    }


    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MyApplication app = MyApplication.getInstance();

            switch (msg.what) {
                case 1:
                    updateSystemTime();
                    handler.sendEmptyMessageDelayed(update_system_time, update_system);
                    break;
                case msgUart:
                    try {
                        /* 如果没有运行,则两秒发送取运行数据 */
                        if (app.isRunWork == false) {
                            sendBroadcast(intentUart);
                            /* 报警信息,间隔十秒 */
                            if (errorDispTime < 1) {
                                if (app.appClass.isUartReady() == false) {
                                    dialog_factory_no_msg.setVisibility(View.VISIBLE);
                                    mll_haveMsg.setVisibility(View.GONE);
                                    tv_con_msg.setVisibility(View.VISIBLE);
                                    errorDispTime = 10 * 1000 / msgUartDelayed;
//                                    BroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(ERROR_ACTION,getString(R.string.dialog_info_communication_failed)+"");

                                } else {
                                    dialog_factory_no_msg.setVisibility(View.GONE);
                                    mll_haveMsg.setVisibility(View.VISIBLE);
                                    tv_con_msg.setVisibility(View.GONE);

                                    String str = app.runningClass.getSystemErrCodeStr();
                                    if (!str.equals("")) {
                                        errorDispTime = 10 * 1000 / msgUartDelayed;
//                                        BroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(ERROR_ACTION,str+"");

                                    }
                                }
                            } else {
                                errorDispTime--;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessageDelayed(msgUart, msgUartDelayed);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }

        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        instance = this;
        // 隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        /* 横屏或竖屏 */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        /* 设置默认桌面 */
        String pageName = getPackageName();
        ComponentName componentName = new ComponentName(pageName, "com.example.thermoshaker.MainActivity");
        switchLauncher(this, componentName);
//         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initGetView();
        PersMiss();
//        Utils.usbWrite();
        openLoopQuery();
        initUsb();
        usbWrite();
//     Utils.startDeskLaunch();

    }

    private void openLoopQuery() {
//        if(MyApplication.getInstance().appClass.isUartReady()){
//
//        }
        /* 注册广播 */
        registerReceiver(recevier, new IntentFilter(MSG));
        /* 用于循环查询 */
        intentUart = new Intent(UartServer.MSG);
        intentUart.putExtra("serialport", new UartClass(MSG, UartType.ASK_RUNDATA_BYTE));
        handler.sendEmptyMessageDelayed(msgUart, msgUartDelayed);
        //系统参数，开机查询一次
        Intent intentSystem = new Intent(UartServer.MSG);
        intentSystem.putExtra("serialport", new UartClass(MSG, UartType.ASK_SYSTEM_BYTE));
        sendBroadcast(intentSystem);
        //温度参数，开机查询一次
        Intent intent2 = new Intent(UartServer.MSG);
        intent2.putExtra("serialport", new UartClass(MSG, UartType.ASK_TEMPADJ_BYTE));
        sendBroadcast(intent2);
    }

    public void initUsb() {

        if (XXPermissions.isHasPermission(this, Permission.Group.STORAGE)) {

        }

        //绑定广播
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        //注册接收广播
        registerReceiver(mUsbReceiver, filter);

        usbHelper = new UsbHelper(this, new USBBroadCastReceiver.UsbListener() {
            @Override
            public void insertUsb(UsbDevice device_add) {
                Log.d(TAG, device_add.toString() + "     device_add");
                Content.usb_state = Intent.ACTION_MEDIA_MOUNTED;
                ToastUtil.show(MainActivity.this, getString(R.string.mounting_u_disk));

            }

            @Override
            public void removeUsb(UsbDevice device_remove) {
                Log.d(TAG, device_remove.toString() + "     device_remove");
                Content.usb_state = Intent.ACTION_MEDIA_EJECT;
                ToastUtil.show(MainActivity.this, getString(R.string.removed_u_disk));

            }

            @Override
            public void getReadUsbPermission(UsbDevice usbDevice) {
                Log.d(TAG, usbDevice.toString() + "     getReadUsbPermission");
                Content.usb_state = Intent.ACTION_MEDIA_MOUNTED;
//                ToastUtil.show(BaseActivity.this,getString(R.string.mounting_u_disk));
            }

            @Override
            public void failedReadUsb(UsbDevice usbDevice) {
                Log.d(TAG, usbDevice.toString() + "    failedReadUsb");

            }
        });
    }

    private void PersMiss() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //未授权，提起权限申请
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 100);
            return;
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


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (tv_date != null) {
            tv_file.setText(getString(R.string.file));
            tv_setting.setText(getString(R.string.setting));
            tv_running.setText(getString(R.string.Fast));
            tv_inching.setText(getString(R.string.inching));
        }

    }

    private void initGetView() {
        btn_dian = findViewById(R.id.btn_dian);
        tv_con_msg = findViewById(R.id.tv_con_msg);
        mll_haveMsg = findViewById(R.id.mll_haveMsg);
        dialog_factory_no_msg = findViewById(R.id.dialog_factory_no_msg);
        tv_inching = findViewById(R.id.tv_inching);
        tv_running = findViewById(R.id.tv_running);
        tv_setting = findViewById(R.id.tv_setting);
        tv_file = findViewById(R.id.tv_file);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        ll_file = findViewById(R.id.ll_file);
        ll_setting = findViewById(R.id.ll_setting);
        ll_run = findViewById(R.id.ll_run);
        ll_inching = findViewById(R.id.ll_inching);
        ll_file.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_run.setOnClickListener(this);
        dialog_factory_no_msg.setOnClickListener(this);
        ll_inching.setOnClickListener(this);
        ll_inching.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    // 按下 处理相关逻辑
                    MyApplication.getInstance().sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_JOG)));

                } else if (action == MotionEvent.ACTION_UP) {
                    // 松开 todo 处理相关逻辑
                    MyApplication.getInstance().sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_STOP_BYTE)));

                }
                return false;

            }
        });

    }

    @Override
    public void onClick(View v) {
        MyApplication.getInstance().KeySound();
        switch (v.getId()) {
            case R.id.dialog_factory_no_msg:
                factoryDialog();
                break;
            case R.id.ll_file:
                startActivity(new Intent(MainActivity.this, FileActivity.class));
                overridePendingTransition(0, 0);

                break;
            case R.id.ll_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.ll_run:
//                ChoosePrograms();
                startActivity(new Intent(MainActivity.this, FastActivity.class));
                overridePendingTransition(0, 0);

                break;


        }


    }

    /**
     * 选择运行程序
     */
    private void ChoosePrograms() {
        ChooseFilePos = -1;
        List<ProgramInfo> dataRefre = MyApplication.getInstance().getDataRefre();

        CustomkeyDialog customkeyDialog = new CustomkeyDialog.Builder(this)
                .view(R.layout.choose_programs_layout)
                .style(R.style.CustomDialog)
                .build();
        customkeyDialog.show();
        RecyclerView rv_list = customkeyDialog.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(MainActivity.this, 6));
        RVListFileAdapter rvListFileAdapter = new RVListFileAdapter(R.layout.file_list_item);
        View view = LayoutInflater.from(this).inflate(R.layout.empty_layout, null, false);
        rvListFileAdapter.setEmptyView(view);
        customkeyDialog.findViewById(R.id.dialog_inout_run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChooseFilePos == -1) {
                    ToastUtil.show(MainActivity.this, getString(R.string.pleasechoosefile));

                    return;
                }
                runFile(dataRefre.get(ChooseFilePos));

                customkeyDialog.dismiss();
            }
        });
        customkeyDialog.findViewById(R.id.dialog_inout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customkeyDialog.dismiss();
            }
        });

        rvListFileAdapter.setList(dataRefre);
//        tv_number.setText(rvListFileAdapter.getData().size()+"/"+ Content.FileNumberNum);
        rv_list.setAdapter(rvListFileAdapter);
        rvListFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ChooseFilePos = position;
                rvListFileAdapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    protected void initDate() {
        updateSystemTime();
        handler.sendEmptyMessageDelayed(update_system_time, update_system);

    }

    public void updateSystemTime() {

        Locale temp = LanguageUtil.getLocale(this);
        if (temp == null) {
            temp = Locale.CHINA;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", temp);// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        tv_time.setText(simpleDateFormat.format(date));
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy/MM/dd EEEE", temp);// HH:mm:ss
        tv_date.setText(simpleDateFormatDate.format(date));
//        tv_time.setVisibility(View.GONE);
    }

    private void switchLauncher(Context context, ComponentName activity) {
        PackageManager pm = context.getPackageManager();
        Log.d("YYY", "switch launcher " + activity);
        try {
            Class<?> packageManager = Class.forName("android.content.pm.PackageManager");
            Method replacePreferedActivity = packageManager.getMethod("replacePreferredActivity", IntentFilter.class,
                    int.class, ComponentName[].class, ComponentName.class);
            IntentFilter homeFilter = new IntentFilter(Intent.ACTION_MAIN);
            homeFilter.addCategory(Intent.CATEGORY_HOME);
            homeFilter.addCategory(Intent.CATEGORY_DEFAULT);

            List<ResolveInfo> resolveInfos = new ArrayList<ResolveInfo>();
            @SuppressWarnings("unused")
            ComponentName curLauncher = listHomeActivitys(context, resolveInfos);
            if (resolveInfos != null && resolveInfos.size() > 0) {
                ComponentName[] componentNames = new ComponentName[resolveInfos.size()];
                for (int i = 0; i < resolveInfos.size(); i++) {
                    ActivityInfo activityInfo = resolveInfos.get(i).activityInfo;
                    if (activityInfo != null) {
                        ComponentName cn = new ComponentName(activityInfo.packageName, activityInfo.name);
                        componentNames[i] = cn;
                        Log.d("YYY", "launcher:" + cn);
                    }
                }
                replacePreferedActivity.setAccessible(true);
                replacePreferedActivity.invoke(pm, homeFilter, IntentFilter.MATCH_CATEGORY_EMPTY, componentNames,
                        activity);
                // killPackage(context,curLauncher.getPackageName());
            } else {
                Log.e("YYY", "get home resolve info empty");
            }

        } catch (Exception e) {
            Log.e("YYY", "" + e);
            e.printStackTrace();
        }
    }

    private ComponentName listHomeActivitys(Context context, List<ResolveInfo> outs) {
        PackageManager pm = context.getPackageManager();
        Object cn = null;
        try {
            Class<?> packageManager = Class.forName("android.content.pm.PackageManager");
            Method getHomeActivities = packageManager.getMethod("getHomeActivities", List.class);
            getHomeActivities.setAccessible(true);
            cn = getHomeActivities.invoke(pm, outs);
        } catch (Exception e) {
            Log.e("YYY", "" + e);
            e.printStackTrace();
        }
        return (ComponentName) cn;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(null);
        }
        if (mUsbReceiver != null) {
            unregisterReceiver(mUsbReceiver);
        }
        if (recevier != null) {
            unregisterReceiver(recevier);
        }
    }


    class RVListFileAdapter extends BaseQuickAdapter<ProgramInfo, BaseViewHolder> {


        public RVListFileAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ProgramInfo s) {
            LinearLayout mll_ = baseViewHolder.getView(R.id.mll_);
            baseViewHolder.setText(R.id.tv_fileName, s.getFileName() + "");
            if (ChooseFilePos == baseViewHolder.getAdapterPosition()) {
                mll_.setBackground(getResources().getDrawable(R.drawable.file_bg_shape_true));

            } else {
                mll_.setBackground(getResources().getDrawable(R.drawable.file_bg_shape));
            }


        }
    }


    /* 串口心跳包 */
    private BroadcastReceiver recevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                byte[] bin = intent.getByteArrayExtra("serialport");
                if (bin != null) {
                    MyApplication app = MyApplication.getInstance();
                    if (app.runningClass.analysis(bin)) {
                        app.appClass.setUartReady(true);
                        //做运行逻辑处理
                        if(app.runningClass.getRunState()== TdfileRunType.RunStateEnum.START.getValue()){
                            //正在运行
                            switch (app.runningClass.getARunFileDefectEnum()){
                                case FULL:
                                    //有文件，获取本地文件，跳转到运行界面
                                    String temp = DataUtil.readData(DataUtil.data_path + DataUtil.param_name + "temp.Naes");
                                    ProgramInfo programInfo = JSON.parseObject(temp, ProgramInfo.class);
                                    Intent intentRun = new Intent(MainActivity.this, RunActivity.class);
                                    intentRun.putExtra("programInfo", programInfo);
                                    startActivity(intentRun);
                                    overridePendingTransition(0, 0);
                                    app.isRunWork = true;
                                    break;
                                case EMPTY:

                                    break;
                            }
                        }else {
                            //无运行
                        }


                        return;
                    }
                    /* 系统初始化处理 */
                    if (app.systemClass.analysis(bin)) {
                        app.appClass.setUartReady(true);
                        app.appClass.setMachineNumber(app.systemClass.getDeviceNum());
                        app.appClass.setSystemInit(true);
                        return;
                    }
                    /* 温度初始化处理 */
                    if (app.adjustClass.analysis(bin)) {
                        return;
                    }
                }
            } catch (Exception e) {

            }
        }
    };
    private void factoryDialog() {
        CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(this);
        customKeyEditDialog.show();
        customKeyEditDialog.init(getString(R.string.setting_factory) + "", CustomKeyEditDialog.TYPE.Null, 0);
        customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
            @Override
            public void onConfirm() {
                switch (customKeyEditDialog.getOutStr()) {
                    case "11111":
                        Utils.startDeskLaunch();
                        Toast.makeText(MainActivity.this, "密码正确", Toast.LENGTH_SHORT).show();

                        break;

                    default:

                        Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });

    }

}