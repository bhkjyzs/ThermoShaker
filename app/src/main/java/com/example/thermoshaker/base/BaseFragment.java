package com.example.thermoshaker.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.thermoshaker.R;
import com.example.thermoshaker.model.Event;
import com.example.thermoshaker.util.BindEventBus;
import com.example.thermoshaker.util.EventBusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(),container,false);
//        // 若使用BindEventBus注解，则绑定EventBus
//        if(this.getClass().isAnnotationPresent(BindEventBus.class)){
//            EventBusUtils.register(this); //必需要先注册
//        }
        return view;

    }
    protected abstract int getLayout();
    protected abstract void initView();
    protected abstract void initDate();

    //方法名可以随意，但是一定要加上@Subscribe注解，并指定线程
    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onEvent(Event event){
        // do something
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        // 若使用BindEventBus注解，则解绑定EventBus
//        if(this.getClass().isAnnotationPresent(BindEventBus.class)){
//            EventBusUtils.unregister(this);//必须要解除注册，防止内存泄漏
//        }
    }


}
