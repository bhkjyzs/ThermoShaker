package com.example.thermoshaker.ui.run;

import android.content.Intent;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.ui.adapter.RVStepListAdapter;

public class RunActivity extends BaseActivity implements View.OnClickListener {
    public ProgramInfo programInfo;
    private TextView tv_times,tv_ZSpeed,tv_Temp,tv_STime;
    private LinearLayout ll_return,ll_pause,ll_next,ll_stop,ll_deil;
    private RecyclerView rv_StepList;
    private RVStepListAdapter RVStepListAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_run;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();

        programInfo = (ProgramInfo) intent.getSerializableExtra("programInfo");
        GetViews();
    }

    private void GetViews() {
        tv_ZSpeed = findViewById(R.id.tv_ZSpeed);
        tv_Temp = findViewById(R.id.tv_Temp);
        tv_STime = findViewById(R.id.tv_STime);
        tv_times = findViewById(R.id.tv_times);
        ll_return = findViewById(R.id.ll_return);
        ll_pause = findViewById(R.id.ll_pause);
        ll_next = findViewById(R.id.ll_next);
        ll_stop = findViewById(R.id.ll_stop);
        ll_deil = findViewById(R.id.ll_deil);
        rv_StepList = findViewById(R.id.rv_StepList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_StepList.setLayoutManager(linearLayoutManager);
        ll_return.setOnClickListener(this);
        ll_pause.setOnClickListener(this);
        ll_next.setOnClickListener(this);
        ll_stop.setOnClickListener(this);
        ll_deil.setOnClickListener(this);


    }

    @Override
    protected void initDate() {
        updateSystemTime(tv_times);
        RVStepListAdapter = new RVStepListAdapter(R.layout.gv_step_list_item,this);
        rv_StepList.setAdapter(RVStepListAdapter);
        RVStepListAdapter.setList(programInfo.getStepList());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_return:
                finish();
                break;
            case R.id.ll_pause:

                break;
            case R.id.ll_next:

                break;
            case R.id.ll_stop:

                break;
            case R.id.ll_deil:


                break;
        }

    }
}