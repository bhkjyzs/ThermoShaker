package com.example.thermoshaker.ui.fast;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.util.dialog.base.CustomKeyEditDialog;

public class FastActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FastActivity";
    private LinearLayout ll_lid;
    private TextView tv_times,tv_lidTm;
    private Button btnReturn,btnRun,Btn_SetTM,Btn_SetSpeed,Btn_SetTime;
    private int run_flag=0;//0是没有运行 1 是正在运行，2是暂停
    private boolean isRuning = false;//当前快速模式是否运行过
    @Override
    protected int getLayout() {
        return R.layout.activity_fast;
    }

    @Override
    protected void initView() {
        GetViews();
    }

    private void GetViews() {
        Btn_SetTM = findViewById(R.id.Btn_SetTM);
        Btn_SetSpeed = findViewById(R.id.Btn_SetSpeed);
        Btn_SetTime = findViewById(R.id.Btn_SetTime);
        btnReturn = findViewById(R.id.btnReturn);
        btnRun = findViewById(R.id.btnRun);
        tv_lidTm = findViewById(R.id.tv_lidTm);
        tv_times = findViewById(R.id.tv_times);
        ll_lid = findViewById(R.id.ll_lid);
        ll_lid.setOnClickListener(this);
        Btn_SetTM.setOnClickListener(this);
        Btn_SetTime.setOnClickListener(this);
        Btn_SetSpeed.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        btnRun.setOnClickListener(this);
        updateSystemTime(tv_times);
    }

    @Override
    protected void initDate() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Btn_SetTM:
                showkeyDialog(CustomKeyEditDialog.TYPE.Temp,String.valueOf(37));
                break;
            case R.id.Btn_SetSpeed:
                showkeyDialog(CustomKeyEditDialog.TYPE.RPM,String.valueOf(1000));

                break;
            case R.id.Btn_SetTime:
                showkeyDialog(CustomKeyEditDialog.TYPE.Time,String.valueOf(MyApplication.getInstance().dateFormat.format(0)));

                break;
            case R.id.btnReturn:
                finish();
                break;
            case R.id.btnRun:
                run_flag=1;
                Btn_SetTM.setEnabled(false);
                Btn_SetTM.setTextColor(getResources().getColor(R.color.gray));
                Btn_SetSpeed.setEnabled(false);
                Btn_SetSpeed.setTextColor(getResources().getColor(R.color.gray));
                Btn_SetTime.setEnabled(false);
                Btn_SetTime.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.ll_lid:
                showLidkeyDialog(String.valueOf(38));
                break;


        }


    }

    private void showLidkeyDialog(String name) {
        CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(this);
        customKeyEditDialog.show();
        customKeyEditDialog.init(name,CustomKeyEditDialog.TYPE.Temp,0);
        customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
            @Override
            public void onConfirm() {
                tv_lidTm.setText(customKeyEditDialog.getOutStr()+"°C");
            }
        });

    }
    private void showkeyDialog(CustomKeyEditDialog.TYPE type,String name) {
        CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(this);
        customKeyEditDialog.show();
        customKeyEditDialog.init(name,type,0);
        customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
            @Override
            public void onConfirm() {
                Log.d(TAG,customKeyEditDialog.getOutStr()+"    "+customKeyEditDialog.getOutTime());
                switch (type){
                    case RPM:
                        Btn_SetSpeed.setText(customKeyEditDialog.getOutStr()+"");
                        break;
                    case Temp:
                        Btn_SetTM.setText(customKeyEditDialog.getOutStr()+"");
                        break;
                    case Time:
                        Btn_SetTime.setText(customKeyEditDialog.getOutTime()+"");
                        break;
                }
            }
        });
    }
}