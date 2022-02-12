package com.example.thermoshaker.ui.run;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.ControlParam;
import com.example.thermoshaker.serial.DataUtils;
import com.example.thermoshaker.serial.SerialPortUtil;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.serial.uart.running.TdfileRunType;
import com.example.thermoshaker.ui.adapter.RVStepListAdapter;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.dialog.TipsDialog;
import com.example.thermoshaker.util.key.Util;
import com.licheedev.hwutils.ByteUtil;
import com.licheedev.myutils.LogPlus;

public class RunActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RunActivity";
    public final static String MSG = RunActivity.class.getName();

    public ProgramInfo programInfo;
    private TextView tv_times, tv_ZSpeed, tv_Temp, tv_STime, tvNum;
    private LinearLayout ll_return, ll_pause, ll_next, ll_stop, ll_deil;
    private RecyclerView rv_StepList;
    private RVStepListAdapter RVStepListAdapter;

    private boolean isPause = false;
    private  final int msg_refresh=1002;
    private android.os.Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msg_refresh:
                    refreshDate();
                    handler.sendEmptyMessageDelayed(msg_refresh,1000);
                    break;

            }

        }
    };



    @Override
    protected int getLayout() {
        return R.layout.activity_run;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();

        programInfo = (ProgramInfo) intent.getSerializableExtra("programInfo");
        GetViews();
        refreshDate();
        handler.sendEmptyMessageDelayed(msg_refresh,1000);

    }

    private void GetViews() {
        new fileRunThread().start();
        /* 注册广播 */
        registerReceiver(recevier, new IntentFilter(MSG));
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
                TipsDialog tipsDialog = new TipsDialog(RunActivity.this,getString(R.string.RunisExit));
                tipsDialog.show();
                tipsDialog.setOnDialogLister(new TipsDialog.onDialogLister() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm() {
                       sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_STOP_BYTE)));
                       finish();
                    }
                });

                break;
            case R.id.ll_pause:
                if(isPause){
                    sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_PAUSE_BYTE)));
                    isPause = false;


                }else {
                    sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_RESUME_BYTE)));
                    isPause = true;


                }

                break;
            case R.id.ll_next:
                ToastUtil.show(this,"暂无命令");
                break;
            case R.id.ll_stop:
                String cmd = "AA";
                byte[] bytes = DataUtils.HexToByteArr(cmd);
                byte[] bytes1 = SerialPortUtil.sendSerialPort(bytes);
//                Log.d(TAG,DataUtils.ByteArrToHex(bytes1).toString() );
//                if(CommandDateUtil.SendCommand(ControlParam.OT_STOP)){
//                    finish();
//                }

                break;
            case R.id.ll_deil:

                break;
        }

    }
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
    private void refreshDate() {
        MyApplication app = MyApplication.getInstance();
        app.runningClass.getRunCir();
        tv_STime.setText(app.runningClass.getCUREndTimeStr()+"");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recevier != null) {
            unregisterReceiver(recevier);
        }
        if(handler!=null){
            handler.removeMessages(msg_refresh);
        }
        MyApplication.getInstance().isRunWork = false;

    }
}