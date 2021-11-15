package com.example.thermoshaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.ui.setting.SettingActivity;
import com.example.thermoshaker.util.LanguageUtil;
import com.example.thermoshaker.util.Utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private int update_system_time=1;
    private int update_system=1000;


    private LinearLayout ll_file,ll_setting,ll_run,ll_inching;
    private TextView tv_date,tv_time,tv_file,tv_setting,tv_running,tv_inching;


    private Handler handler =  new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    updateSystemTime();
                    handler.sendEmptyMessageDelayed(update_system_time,update_system);
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

//     Utils.startDeskLaunch();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        if(tv_date!=null){
            tv_file.setText(getString(R.string.file));
            tv_setting.setText(getString(R.string.setting));
            tv_running.setText(getString(R.string.run));
            tv_inching.setText(getString(R.string.inching));
        }

    }

    private void initGetView() {
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
        ll_inching.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_file:
                startActivity(new Intent(MainActivity.this, FileActivity.class));
                break;
            case R.id.ll_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));

                break;
            case R.id.ll_run:

                break;
            case R.id.ll_inching:

                break;

        }


    }

    @Override
    protected void initDate() {
        updateSystemTime();
        handler.sendEmptyMessageDelayed(update_system_time,update_system);

    }
    public void updateSystemTime(){
        Locale temp = LanguageUtil.getLocale(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm",temp);// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        tv_time.setText(simpleDateFormat.format(date));
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy/MM/dd EEEE",temp);// HH:mm:ss
        tv_date.setText(simpleDateFormatDate.format(date));

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
        if(handler!=null){
            handler.removeCallbacks(null);
        }
    }
}