package com.example.thermoshaker.ui.run;

import android.annotation.SuppressLint;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thermoshaker.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.example.thermoshaker.AAChartCoreLib.AAChartCreator.AAChartView;
import com.example.thermoshaker.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.example.thermoshaker.AAChartCoreLib.AAChartEnum.AAChartType;
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

import java.util.HashMap;
import java.util.Map;

public class RunActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RunActivity";
    public final static String MSG = RunActivity.class.getName();
    private fileRunThread mThread; // 会话线程
    private ConstraintLayout mCltiao,mCldetil;
    private AAChartView AAChartView;
    private TextView tv_detil,tv_StepRadiatorTemp;
    public ProgramInfo programInfo;
    private TextView tv_times, tv_ZSpeed, tv_Temps, tv_EndTime,tv_CurrentStep, tvNum,tv_state,tv_StepTime;
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
        RVStepListAdapter = new RVStepListAdapter(R.layout.gv_step_list_item, this);
        GetViews();
        rv_StepList.setAdapter(RVStepListAdapter);
        RVStepListAdapter.setList(programInfo.getStepList());
        refreshDate();
        handler.sendEmptyMessageDelayed(msg_refresh,1000);

    }

    private void GetViews() {
        mThread = new fileRunThread();
        mThread.start();
        /* 注册广播 */
        registerReceiver(recevier, new IntentFilter(MSG));
        tv_StepRadiatorTemp = findViewById(R.id.tv_StepRadiatorTemp);
        tv_detil = findViewById(R.id.tv_detil);
        tvNum = findViewById(R.id.tvNum);
        AAChartView = findViewById(R.id.AAChartView);
        mCltiao = findViewById(R.id.mCltiao);
        mCldetil =findViewById(R.id.mCldetil);
        tv_state = findViewById(R.id.tv_state);
        tv_StepTime = findViewById(R.id.tv_StepTime);
        tv_CurrentStep = findViewById(R.id.tv_CurrentStep);
        tv_ZSpeed = findViewById(R.id.tv_ZSpeed);
        tv_Temps = findViewById(R.id.tv_Temps);
        tv_EndTime = findViewById(R.id.tv_EndTime);
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
        ChartView();
        double v = Math.log10(375.14);
        Log.d(TAG,v+"");
    }

    private void ChartView() {
        AAChartModel aaChartModel = new AAChartModel().chartType(AAChartType.Line)
                .title("THIS IS ")
                .subtitle("Virtual Data")
                .backgroundColor("#4b2b7f")
                .categories(new String[]{"Java","Swift","Python","Ruby", "PHP","Go","C","C#","C++"})
                .dataLabelsEnabled(false)
                .yAxisGridLineWidth(0f)
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .name("Tokyo")
                                .data(new Object[]{7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6}),
                        new AASeriesElement()
                                .name("NewYork")
                                .data(new Object[]{0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5}),
                        new AASeriesElement()
                                .name("London")
                                .data(new Object[]{0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0}),
                        new AASeriesElement()
                                .name("Berlin")
                                .data(new Object[]{3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8})
                });

        AAChartView.aa_drawChartWithChartModel(aaChartModel);



    }

    @Override
    protected void initDate() {
        updateSystemTime(tv_times);
    }

    @SuppressLint("WrongConstant")
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
                    tv_state.setText(getString(R.string.Continue));
                }else {
                    sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_RESUME_BYTE)));
                    isPause = true;
                    tv_state.setText(getString(R.string.pause));
                }
                break;
            case R.id.ll_next:
                ToastUtil.show(this,"暂不可用");
                break;
            case R.id.ll_stop:
                TipsDialog tipsDialogStop = new TipsDialog(RunActivity.this,getString(R.string.dialog_info_program_stop));
                tipsDialogStop.show();
                tipsDialogStop.setOnDialogLister(new TipsDialog.onDialogLister() {
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
            case R.id.ll_deil:
                if(mCltiao.getVisibility()==8){
                    tv_detil.setText(getString(R.string.details));
                    mCltiao.setVisibility(View.VISIBLE);
                    mCldetil.setVisibility(View.GONE);
                }else {
                    tv_detil.setText(getString(R.string.Heatingprocess));
                    mCltiao.setVisibility(View.GONE);
                    mCldetil.setVisibility(View.VISIBLE);
                }
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
        //当前步骤
        int pos = app.runningClass.getRunStep();
        if(pos==0){
            return;
        }
        tv_EndTime.setText(app.runningClass.getCUREndTimeStr()+"");
        tv_Temps.setText(app.runningClass.getDispTemp1A()+"");
        tv_StepRadiatorTemp.setText(app.runningClass.getLidDispTemp()+"");
        tvNum.setText(app.runningClass.getRunCir()+"");
        tv_CurrentStep.setText(app.runningClass.getRunStep()+"");
        tv_StepTime.setText(app.runningClass.getStepSurplusStr()+"");
        Log.d(TAG,app.runningClass.getCUREndTimeStr()+""+app.runningClass.getRunCir()+"");

        if(RVStepListAdapter!=null){

        }
        RVStepListAdapter.setSelectPostion(pos-1);
        RVStepListAdapter.notifyDataSetChanged();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    }
}