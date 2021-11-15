package com.example.thermoshaker.base;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thermoshaker.model.Event;
import com.example.thermoshaker.util.AppManager;
import com.example.thermoshaker.util.BindEventBus;
import com.example.thermoshaker.util.CloseBarUtil;
import com.example.thermoshaker.util.EventBusUtils;
import com.example.thermoshaker.util.LanguageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public abstract class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    private int update_system_time=1;
    private int update_system=1000;
    private TextView tv_times;

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

        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy/MM/dd EEEE HH:mm",temp);// HH:mm:ss
        tv.setText(simpleDateFormatDate.format(date));

    }

}
