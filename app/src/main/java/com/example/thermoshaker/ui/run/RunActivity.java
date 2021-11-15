package com.example.thermoshaker.ui.run;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;

public class RunActivity extends BaseActivity {
    private TextView tv_times;


    @Override
    protected int getLayout() {
        return R.layout.activity_run;
    }

    @Override
    protected void initView() {
        GetViews();

    }

    private void GetViews() {
        tv_times = findViewById(R.id.tv_times);

    }

    @Override
    protected void initDate() {
        updateSystemTime(tv_times);


    }
}