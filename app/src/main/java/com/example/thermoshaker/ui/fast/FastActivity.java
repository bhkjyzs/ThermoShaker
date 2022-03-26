package com.example.thermoshaker.ui.fast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.serial.uart.running.TdfileRunType;
import com.example.thermoshaker.ui.run.RunActivity;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.dialog.base.CustomKeyEditDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FastActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FastActivity";
    private LinearLayout ll_lid;
    private TextView tv_times,tv_lidTm;
    private TextView tv_Tamp,tv_ZSpeed,tv_time;
    private Button btnReturn,btnRun,Btn_SetTM,Btn_SetSpeed,Btn_SetTime;
    private int run_flag=0;//0是没有运行 1 是正在运行
    private boolean isRuning = false;//当前快速模式是否运行过
    private ProgramInfo programInfo= new ProgramInfo("FastFiles");
    @Override
    protected int getLayout() {
        return R.layout.activity_fast;
    }

    @Override
    protected void initView() {
        GetViews();
    }

    private void GetViews() {
        tv_Tamp = findViewById(R.id.tv_Tamp);
        tv_ZSpeed = findViewById(R.id.tv_ZSpeed);
        tv_time = findViewById(R.id.tv_time);
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
        List<ProgramStep> stepList = new ArrayList<>();
        ProgramStep step = new ProgramStep();
        stepList.add(step);
        programInfo.setStepList(stepList);


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

                if(run_flag!=1){
                    fastRunFile(programInfo);
                    btnReturn.setText(getString(R.string.run_state_stop));
                }else {
                    sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_STOP_BYTE)));
                    finish();
                }

                break;
            case R.id.btnRun:
                if(run_flag==0){
                    finish();
                }

                break;
            case R.id.ll_lid:
                showLidkeyDialog(String.valueOf(38));
                break;


        }


    }

    public void fastRunFile(ProgramInfo programInfo){


        if(getProgramByte(programInfo)){

            String jsonOutput = JSON.toJSONString(programInfo);

            DataUtil.writeData(jsonOutput, DataUtil.data_path + DataUtil.fast_param_name, "temp.Naes", false);
            run_flag=1;
            Btn_SetTM.setEnabled(false);
            Btn_SetTM.setTextColor(getResources().getColor(R.color.gray));
            Btn_SetSpeed.setEnabled(false);
            Btn_SetSpeed.setTextColor(getResources().getColor(R.color.gray));
            Btn_SetTime.setEnabled(false);
            Btn_SetTime.setTextColor(getResources().getColor(R.color.gray));

            mThread = new fileRunThread();
            mThread.start();
            /* 注册广播 */
            registerReceiver(recevier, new IntentFilter(MSG));
            handler.sendEmptyMessageDelayed(msg_refresh,1000);

            MyApplication.getInstance().isRunWork = true;

        };
        SystemClock.sleep(500);
        Intent intentRun = new Intent(UartServer.MSG);
        intentRun.putExtra("serialport", new UartClass(null, UartType.OT_RUN_BYTE));
        sendBroadcast(intentRun);
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
                        programInfo.getStepList().get(0).setZSpeed(Integer.parseInt(customKeyEditDialog.getOutStr()));

                        break;
                    case Temp:
                        Btn_SetTM.setText(customKeyEditDialog.getOutStr()+"");
                        programInfo.getStepList().get(0).setTemperature(Float.parseFloat(customKeyEditDialog.getOutStr()));
                        break;
                    case Time:
                        Btn_SetTime.setText(customKeyEditDialog.getOutTime()+"");
                        try {

                            programInfo.getStepList().get(0).setTime(MyApplication.getInstance().dateFormat.parse(customKeyEditDialog.getOutTime()).getTime());

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
        });
    }


    public final static String MSG = FastActivity.class.getName();
    private fileRunThread mThread; // 会话线程

    private class fileRunThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {

                int stopInt = TdfileRunType.RunStateEnum.STOP.getValue();
                /* 查询一次 */
                MyApplication app = MyApplication.getInstance();
                Intent intentData = new Intent(UartServer.MSG);
                intentData.putExtra("serialport", new UartClass(MSG, UartType.ASK_RUNDATA_BYTE));
                app.sendBroadcast(intentData);
                sleep(app.appClass.getTdRecordTime());
                if(app.runningClass.getRunState()!=stopInt){
                    do{
                        /* 查询运行状态 */
                        app.sendBroadcast(intentData);
                        sleep(app.appClass.getTdRecordTime());

                    }while (app.runningClass.getRunState()!=stopInt);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    /* 串口心跳包 */
    private BroadcastReceiver recevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bin = intent.getByteArrayExtra("serialport");
            if (bin != null) {
                MyApplication app = MyApplication.getInstance();
                if (app.runningClass.analysis(bin)) {
                }
            }
        }
    };
    private  final int msg_refresh=1004;
    MyApplication app = MyApplication.getInstance();
    private android.os.Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msg_refresh:
                    tv_time.setText(app.runningClass.getStepSurplusStr()+"");
                    tv_Tamp.setText(app.runningClass.getDispTemp1A()+"°C");

                    handler.sendEmptyMessageDelayed(msg_refresh,1000);
                    break;

            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {


        if (recevier != null) {
            unregisterReceiver(recevier);
        }
        /* 停止线程 */
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if(handler!=null){
            handler.removeMessages(msg_refresh);
        }
        MyApplication.getInstance().isRunWork = false;
        }catch (Exception e){

        }
    }
}