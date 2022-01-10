package com.example.thermoshaker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.serial.DataUtils;
import com.example.thermoshaker.ui.fast.FastActivity;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.ui.run.RunActivity;
import com.example.thermoshaker.ui.setting.SettingActivity;
import com.example.thermoshaker.util.LanguageUtil;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
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
    private int update_system_time=1;
    private int update_system=1000;
    private int ChooseFilePos = -1;


    private LinearLayout ll_file,ll_setting,ll_run,ll_fast;
    private TextView tv_date,tv_time,tv_file,tv_setting,tv_running,tv_inching;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

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
            tv_inching.setText(getString(R.string.Fast));
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
        ll_fast = findViewById(R.id.ll_fast);
        ll_file.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_run.setOnClickListener(this);
        ll_fast.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_file:
                startActivity(new Intent(MainActivity.this, FileActivity.class));
                overridePendingTransition(0, 0);

                break;
            case R.id.ll_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.ll_run:
                ChoosePrograms();


                break;
            case R.id.ll_fast:
                startActivity(new Intent(MainActivity.this, FastActivity.class));
                overridePendingTransition(0, 0);

                break;

        }


    }

    /**
     * 选择运行程序
     */
    private void ChoosePrograms() {
        ChooseFilePos=-1;
        List<ProgramInfo> dataRefre = MyApplication.getInstance().getDataRefre();

        CustomkeyDialog customkeyDialog = new CustomkeyDialog.Builder(this)
                .view(R.layout.choose_programs_layout)
                .style(R.style.CustomDialog)
                .build();
        customkeyDialog.show();
        RecyclerView rv_list = customkeyDialog.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(MainActivity.this,6));
        RVListFileAdapter rvListFileAdapter = new RVListFileAdapter(R.layout.file_list_item);
        View view = LayoutInflater.from(this).inflate(R.layout.empty_layout,null,false);
        rvListFileAdapter.setEmptyView(view);
        customkeyDialog.findViewById(R.id.dialog_inout_run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ChooseFilePos==-1){
                    ToastUtil.show(MainActivity.this,getString(R.string.pleasechoosefile));

                    return;
                }
                Intent intent = new Intent(MainActivity.this, RunActivity.class);
                intent.putExtra("programInfo",dataRefre.get(ChooseFilePos));
                startActivity(intent);
                overridePendingTransition(0, 0);
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
        handler.sendEmptyMessageDelayed(update_system_time,update_system);

    }
    public void updateSystemTime(){

        Locale temp = LanguageUtil.getLocale(this);
        if(temp==null){
             temp = Locale.CHINA;
        }
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
        MyApplication.getInstance().CloseSerialPort();
    }


    class RVListFileAdapter extends BaseQuickAdapter<ProgramInfo, BaseViewHolder> {


        public RVListFileAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ProgramInfo s) {
            LinearLayout mll_ = baseViewHolder.getView(R.id.mll_);
            baseViewHolder.setText(R.id.tv_fileName,s.getFileName()+"");
            if(ChooseFilePos == baseViewHolder.getAdapterPosition()){
                mll_.setBackground(getResources().getDrawable(R.drawable.file_bg_shape_true));

            }else {
                mll_.setBackground(getResources().getDrawable(R.drawable.file_bg_shape));
            }


        }
    }
}