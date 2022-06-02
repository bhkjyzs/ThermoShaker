package com.example.thermoshaker.util.dialog;

import static org.greenrobot.eventbus.EventBus.TAG;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MainType;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.serial.uart.debug.DebugInterface;
import com.example.thermoshaker.ui.adapter.MyAdapter;
import com.example.thermoshaker.ui.file.AddAndEditActivity;
import com.example.thermoshaker.util.dialog.base.CustomKeyEditDialog;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;
import com.example.thermoshaker.util.key.FloatingKeyboard;
import com.flyco.tablayout.SlidingTabLayout;
import com.licheedev.myutils.LogPlus;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DebugDialog {
    public final static String MSG = DebugDialog.class.getName();
    private static final String TAG = "DebugDialog";

    Context context;
    Activity activity;
    private RadioGroup rg1_model1,rg2_model2,rg3_model3,rg4_model4,rg_fan,rg_motor,rg_led,rg_lid;
    private final int hanMsg = 1;
    MyApplication app = MyApplication.getInstance();
    private TextView factory_temperature_text1,factory_temperature_text2;
    public DebugDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        MyApplication.getInstance().sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DEBUG_RUN_BYTE)));
        init();
        QueryAdjust();
    }
    /* 时间定时器 */
    public Handler han = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Intent intent = new Intent(UartServer.MSG);
            intent.putExtra("serialport", new UartClass(MSG, UartType.ASK_TEMPDATA_BYTE));
            MyApplication app = MyApplication.getInstance();
            app.sendBroadcast(intent);

            han.sendEmptyMessageDelayed(hanMsg, app.appClass.getTdRecordTime());
            return false;
        }
    });

    private void QueryAdjust() {
        MyApplication.getInstance().sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(MSG, UartType.ASK_TEMPDATA_BYTE)));
        han.sendEmptyMessageDelayed(hanMsg, app.appClass.getTdRecordTime());
    }
    /* 接收广播 */
    private BroadcastReceiver recevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /* 如果打开串口则接收数据 */
            try {
                byte[] bin = intent.getByteArrayExtra("serialport");
                if (bin == null) {
                } else {
                    DebugInterface debugClass = app.debugClass;
                    if (debugClass.analysis(bin)) {
                        factory_temperature_text1.setText(debugClass.getTrueTempBlock1() + " ℃");
                        //factory_temperature_text2      lid 暂无赋值
                    }
                }
            } catch (Exception e) {
                LogPlus.d(TAG, "recevier! " + e.getMessage());
            }
        }
    };
    protected void finalizeHan() throws Throwable {
        if (han != null) {
            han.removeMessages(hanMsg);
            han = null;
        }
        if (recevier != null) {
            app.unregisterReceiver(recevier);
            recevier = null;
        }
    }
    private void init() {
        /* 注册广播 */
        app.registerReceiver(recevier, new IntentFilter(MSG));
        CustomkeyDialog seniorDialog = new CustomkeyDialog.Builder(context)
                .view(R.layout.debugmodule_dialog_vp_layout)
                .style(R.style.CustomDialog)
                .build();
        seniorDialog.show();
        /* 屏蔽键盘 */
        seniorDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        Button btnCancel = seniorDialog.findViewById(R.id.btnCancel);
        rg1_model1 = seniorDialog.findViewById(R.id.rg1_model1);
        rg2_model2 = seniorDialog.findViewById(R.id.rg2_model2);
        rg3_model3 = seniorDialog.findViewById(R.id.rg3_model3);
        rg4_model4 = seniorDialog.findViewById(R.id.rg4_model4);
        rg_fan = seniorDialog.findViewById(R.id.rg_fan);
        rg_motor = seniorDialog.findViewById(R.id.rg_motor);
        rg_led = seniorDialog.findViewById(R.id.rg_led);
        rg_lid = seniorDialog.findViewById(R.id.rg_lid);
        factory_temperature_text1 = seniorDialog.findViewById(R.id.factory_temperature_text1);
        factory_temperature_text2 = seniorDialog.findViewById(R.id.factory_temperature_text2);
         setRG();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication myapp = MyApplication.getInstance();
                myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_ALL_OFF_BYTE)));
                myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DEBUG_STOP_BYTE)));

                seniorDialog.dismiss();
                try {
                    finalizeHan();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setRG() {
        MyApplication myapp = MyApplication.getInstance();
        rg1_model1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_model1:
                        LogPlus.d("rb1_model1"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_TE1_HT_BYTE)));
                        break;
                    case R.id.rb2_model1:
                        LogPlus.d("rb2_model1"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_TE1_CL_BYTE)));
                        break;
                    case R.id.rb3_model1:
                        LogPlus.d("rb3_model1"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_TE1_OFF_BYTE)));
                        break;
                }

            }
        });
        rg2_model2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_model2:
                        LogPlus.d("rb1_model2"+"");
                        break;
                    case R.id.rb2_model2:
                        LogPlus.d("rb2_model2"+"");
                        break;
                    case R.id.rb3_model2:
                        LogPlus.d("rb3_model2"+"");
                        break;
                }
            }
        });
        rg3_model3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_model3:
                        LogPlus.d("rb1_model3"+"");
                        break;
                    case R.id.rb2_model3:
                        LogPlus.d("rb2_model3"+"");
                        break;
                    case R.id.rb3_model3:
                        LogPlus.d("rb3_model3"+"");
                        break;
                }
            }
        });
        rg4_model4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_model4:
                        LogPlus.d("rb1_model4"+"");
                        break;
                    case R.id.rb2_model4:
                        LogPlus.d("rb2_model4"+"");
                        break;
                    case R.id.rb3_model4:
                        LogPlus.d("rb3_model4"+"");
                        break;
                }
            }
        });
        rg_fan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_open_fan:
                        LogPlus.d("rb1_open_fan"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_FAN_ON_BYTE)));

                        break;
                    case R.id.rb2_close_fan:
                        LogPlus.d("rb2_close_fan"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_FAN_OFF_BYTE)));

                        break;
                }
            }
        });
        rg_motor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_open_Motor:
                        LogPlus.d("rb1_open_Motor"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_MOTOR_ON_BYTE)));

                        break;
                    case R.id.rb2_close_Motor:
                        LogPlus.d("rb2_close_Motor"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_MOTOR_OFF_BYTE)));

                        break;
                }
            }
        });
        rg_led.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_open_led:
                        LogPlus.d("rb1_open_led"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_LED_ON_BYTE)));

                        break;
                    case R.id.rb2_close_led:
                        LogPlus.d("rb2_close_led"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_LED_OFF_BYTE)));

                        break;
                }
            }
        });

        rg_lid.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1_open_lid:
                        LogPlus.d("rb1_open_lid"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_LIDA_ON_BYTE)));

                        break;
                    case R.id.rb2_close_lid:
                        LogPlus.d("rb2_close_lid"+"");
                        myapp.sendBroadcast(new Intent(UartServer.MSG).putExtra("serialport", new UartClass(null, UartType.OT_DB_LIDA_OFF_BYTE)));

                        break;
                }
            }
        });


    }


}
