package com.example.thermoshaker.ui.run;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.ControlParam;
import com.example.thermoshaker.serial.DataUtils;
import com.example.thermoshaker.ui.adapter.RVStepListAdapter;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.util.dialog.TipsDialog;
import com.example.thermoshaker.util.key.Util;
import com.licheedev.myutils.LogPlus;

public class RunActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RunActivity";
    public ProgramInfo programInfo;
    private TextView tv_times, tv_ZSpeed, tv_Temp, tv_STime, tvNum;
    private LinearLayout ll_return, ll_pause, ll_next, ll_stop, ll_deil;
    private RecyclerView rv_StepList;
    private RVStepListAdapter RVStepListAdapter;

    private boolean isPause = false;

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
        tvNum = findViewById(R.id.tvNum);
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
        if (programInfo.isLoopEnable()) {
            tvNum.setVisibility(View.VISIBLE);
            tvNum.setLayoutParams(new RelativeLayout.LayoutParams(Util.dpToPx(this, 150 * (programInfo.getLoopEnd() - programInfo.getLoopStart() + 1)), 50));
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvNum.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.leftMargin = (programInfo.getLoopStart() - 1) * Util.dpToPx(this, 150);
            lp.bottomMargin = Util.dpToPx(this, 50);
            tvNum.setLayoutParams(lp);
            tvNum.setText("x" + programInfo.getLoopNum() + "");
        } else {
            tvNum.setVisibility(View.GONE);

        }


    }

    @Override
    protected void initDate() {
        updateSystemTime(tv_times);
        RVStepListAdapter = new RVStepListAdapter(R.layout.gv_step_list_item, this);
        rv_StepList.setAdapter(RVStepListAdapter);

        RVStepListAdapter.setList(programInfo.getStepList());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_return:
//                TipsDialog tipsDialog = new TipsDialog(RunActivity.this,getString(R.string.RunisExit));
//                tipsDialog.show();
//                tipsDialog.setOnDialogLister(new TipsDialog.onDialogLister() {
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onConfirm() {
//                        if(CommandDateUtil.SendCommand(ControlParam.OT_STOP)){
//                            finish();
//                        }
//
//                    }
//                });
                finish();

                break;
            case R.id.ll_pause:
                if(isPause){
                    if(CommandDateUtil.SendCommand(ControlParam.OT_RESUME)){
                        isPause = false;
                    }

                }else {
                    if(CommandDateUtil.SendCommand(ControlParam.OT_PAUSE)){
                        isPause = true;
                    }
                }

                break;
            case R.id.ll_next:

                break;
            case R.id.ll_stop:
                if(CommandDateUtil.SendCommand(ControlParam.OT_STOP)){
                    finish();
                }

                break;
            case R.id.ll_deil:

                break;
        }

    }
}