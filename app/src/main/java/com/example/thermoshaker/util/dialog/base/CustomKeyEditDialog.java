package com.example.thermoshaker.util.dialog.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.StepDefault;

import java.math.BigDecimal;


public class CustomKeyEditDialog extends Dialog implements View.OnClickListener {
    private onDialogLister onDialogLister;


    private static final String TAG = "CustomKeyEditDialog";
    private Button[] Buts; // 数字按钮
    private TextView textView_time_input; // 用于暂时存放数据
    private ImageButton backspaceBut; // 退格按钮
    private TextView tv_title;
    private LinearLayout LinearLayout_time; // 输入时间时需要切换
    private Boolean once; // 第一次输入
    private TextView textView_hh, textView_mm, textView_ss, textView_input;
    private String startString, endString;
    private String retString;
    private StepDefault stepDefault;
    private Button btn_jian;
    private CustomKeyEditDialog.TYPE type; // 四种输入类型,1温度2时间3次数4目标
    private int pos; // 当输入位置时需要限制其不要超过自身



    public enum TYPE {
        Null, Temp, Time, Num, Pos,RPM
    }



    public CustomKeyEditDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }
    public interface onDialogLister{
        void onConfirm();
    }

    public void setOnDialogLister(onDialogLister onDialogLister){
        this.onDialogLister = onDialogLister;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.create_win_keyboard, null);
        setContentView(view);
        setCanceledOnTouchOutside(true);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;

        win.setAttributes(lp);
        initView(view);
//

        TextView tv_msg = findViewById(R.id.tv_msg);
        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDialogLister!=null){
                    onDialogLister.onConfirm();
                }
                dismiss();
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDialogLister!=null){

                }
                dismiss();

            }
        });
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        this.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });

    }

    @Override
    public void show() {
//        if (this.getWindow() != null) {
//
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            super.show();
//            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }


    private void initView(View view) {
        stepDefault = MyApplication.getInstance().stepDefault;
        /* 关联事件 */
        int[] ButIds = new int[] { R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
                R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11 };
        Buts = new Button[ButIds.length];
        for (int i = 0; i < ButIds.length; i++) {
            Buts[i] = (Button) view.findViewById(ButIds[i]);
            Buts[i].setOnClickListener(this);
        }

        LinearLayout_time = (LinearLayout) view.findViewById(R.id.LinearLayout_time);
        textView_input = (TextView) view.findViewById(R.id.textView_input);
        tv_title = view.findViewById(R.id.tv_title);
        textView_hh = (TextView) view.findViewById(R.id.textView_hh);
        textView_hh.setOnClickListener(this);
        textView_mm = (TextView) view.findViewById(R.id.textView_mm);
        textView_mm.setOnClickListener(this);
        textView_ss = (TextView) view.findViewById(R.id.textView_ss);

        textView_ss.setOnClickListener(this);
        btn_jian = view.findViewById(R.id.btn_jian);
        btn_jian.setOnClickListener(this);
        backspaceBut = (ImageButton) view.findViewById(R.id.imageButton_back);
        backspaceBut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.imageButton_back:
                        setChar("-1");

                    break;
                case R.id.textView_hh:
                    setTimeTextView(1);
                    break;
                case R.id.textView_mm:
                    setTimeTextView(2);
                    break;
                case R.id.textView_ss:
                    setTimeTextView(3);
                    break;
                case R.id.button1:
                    setChar("1");
                    break;
                case R.id.button2:
                    setChar("2");
                    break;
                case R.id.button3:
                    setChar("3");
                    break;
                case R.id.button4:
                    setChar("4");
                    break;
                case R.id.button5:
                    setChar("5");
                    break;
                case R.id.button6:
                    setChar("6");
                    break;
                case R.id.button7:
                    setChar("7");
                    break;
                case R.id.button8:
                    setChar("8");
                    break;
                case R.id.button9:
                    setChar("9");
                    break;
                case R.id.button10:
                    setChar("0");
                    break;
                case R.id.button11:
                    setChar(".");
                    break;
                case R.id.btn_jian:
                    setChar("-");
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG,e.getMessage()+"");
//			MainApp.getInstance().sendBroadcast(new Intent(AppActivity.MSG).putExtra("message", e.getMessage()));
        }
    }
    /* 初始化 */
    public void init(String string, CustomKeyEditDialog.TYPE type, int pos) {
        once = true;
        this.type = type;
        this.pos = pos;

        startString = "";
        retString = "";
        endString = "";
        switch (type) {
            case RPM:
                LinearLayout_time.setVisibility(View.GONE);
                textView_input.setVisibility(View.VISIBLE);
                btn_jian.setVisibility(View.GONE);
                startString = "";
                retString = string;
                endString = " RPM";
                setInputText(retString);
                tv_title.setText("( "+MyApplication.getInstance().getString(R.string.revolutions)+""+string+" RPM )");

                break;
            case Temp: // 温度
                LinearLayout_time.setVisibility(View.GONE);
                textView_input.setVisibility(View.VISIBLE);
                btn_jian.setVisibility(View.VISIBLE);
                startString = "";
                retString = string;
                endString = " ℃";
                setInputText(retString);
                tv_title.setText("( "+MyApplication.getInstance().getString(R.string.temperatures)+""+string+"℃ )");

                break;
            case Time:// 时间
                LinearLayout_time.setVisibility(View.VISIBLE);
                textView_input.setVisibility(View.GONE);
                btn_jian.setVisibility(View.GONE);

                String[] str2 = string.split(":");
                if (str2.length == 3) {
                    textView_hh.setText(str2[0]);
                    textView_mm.setText(str2[1]);
                    textView_ss.setText(str2[2]);
                } else {
                    textView_hh.setText("00");
                    textView_mm.setText("00");
                    textView_ss.setText("00");
                }
                setTimeTextView(3);
                tv_title.setText("( "+MyApplication.getInstance().getString(R.string.times)+""+string+" )");

                break;
            case Num:// 次数
                btn_jian.setVisibility(View.GONE);
                LinearLayout_time.setVisibility(View.GONE);
                textView_input.setVisibility(View.VISIBLE);
                startString = "x ";
                retString = string;
                endString = "";
                setInputText(retString);
                break;
            case Pos:// 目标
                LinearLayout_time.setVisibility(View.GONE);
                textView_input.setVisibility(View.VISIBLE);
                btn_jian.setVisibility(View.GONE);
                startString = "Step ";
                retString = string;
                endString = "";
                setInputText(retString);
                break;
            case Null:
                LinearLayout_time.setVisibility(View.GONE);
                textView_input.setVisibility(View.VISIBLE);
                textView_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                break;
        }
    }

    public String getOutStr() {
        return retString;
    }

    public String getOutTime() {
        return textView_hh.getText().toString() + ":" + textView_mm.getText().toString() + ":"
                + textView_ss.getText().toString();
    }

    /* 设置选中时间背景 */
    public int getTimeAll(int num) {
        int hh = 0, mm = 0, ss = 0;
        int pos = (Integer) textView_time_input.getTag();
        switch (pos) {
            case 1:
                hh = new BigDecimal(num).multiply(new BigDecimal("3600")).intValue();
                mm = new BigDecimal(textView_mm.getText().toString()).multiply(new BigDecimal("60")).intValue();
                ss = new BigDecimal(textView_ss.getText().toString()).intValue();
                break;
            case 2:
                hh = new BigDecimal(textView_hh.getText().toString()).multiply(new BigDecimal("3600")).intValue();
                mm = new BigDecimal(num).multiply(new BigDecimal("60")).intValue();
                ss = new BigDecimal(textView_ss.getText().toString()).intValue();
                break;
            case 3:
                hh = new BigDecimal(textView_hh.getText().toString()).multiply(new BigDecimal("3600")).intValue();
                mm = new BigDecimal(textView_mm.getText().toString()).multiply(new BigDecimal("60")).intValue();
                ss = new BigDecimal(num).intValue();
                break;
        }
        return hh + mm + ss;
    }

    /* 设置选中时间背景 */
    public void setTimeTextView(int num) {
        switch (num) {
            case 1: // 小时
                textView_time_input = textView_hh;
                retString = textView_hh.getText().toString();
                textView_time_input.setTag(1);
                textView_hh.setBackgroundResource(R.drawable.bio_create_text_border_nor);
                textView_mm.setBackgroundResource(R.drawable.bio_create_text_border);
                textView_ss.setBackgroundResource(R.drawable.bio_create_text_border);
                once = true;
                break;
            case 2: // 分钟
                textView_time_input = textView_mm;
                retString = textView_hh.getText().toString();
                textView_time_input.setTag(2);
                textView_hh.setBackgroundResource(R.drawable.bio_create_text_border);
                textView_mm.setBackgroundResource(R.drawable.bio_create_text_border_nor);
                textView_ss.setBackgroundResource(R.drawable.bio_create_text_border);
                once = true;
                break;
            case 3: // 秒
                textView_time_input = textView_ss;
                retString = textView_hh.getText().toString();
                textView_time_input.setTag(3);
                textView_hh.setBackgroundResource(R.drawable.bio_create_text_border);
                textView_mm.setBackgroundResource(R.drawable.bio_create_text_border);
                textView_ss.setBackgroundResource(R.drawable.bio_create_text_border_nor);
                once = true;
                break;
        }
    }


    /* 文本框填入内容 */
    public void setInputText(String string) {
        if (!startString.equals("")) // 如果前缀不为空
            textView_input.setText(startString + string);
        else if (!endString.equals("")) // 如果后缀不为空
            textView_input.setText(string + endString);
        else
            textView_input.setText(string);
        retString = string;
    }

    /* 文本框输入一个字符,第二参数表示负数符号 */
    @SuppressLint("LongLogTag")
    private void setChar(String str) {

        if (type == CustomKeyEditDialog.TYPE.Null) {
            String string = "";
            if (str.equals("-1")) {
                if (retString.length() > 0)
                    string = retString.substring(0, retString.length() - 1);
            } else
                string = retString + str;
            textView_input.setText(string);
            retString = string;
            return;
        }

        try {
            String valStr = ""; // 这一次

            /* 第一次输入表示先清空后输入 */
            if (once) {
                /* 输入.返回 */
                if (str.equals("."))
                    return;
                /* 输入-1或空则输入0 */
                if ( str.equals("") ) {
                    valStr = "0";
                } else {
                    valStr = str;
                }
                once = false;
            } else {
                /* 取回上一次的值 */
                if (str.equals("-1")) {
                    /* 退格 */
                    if (retString.length() > 0)
                        valStr = retString.substring(0, retString.length() - 1);
                    /* 删除完毕 */
                    if (valStr.equals("") || valStr.equals(".")) {
                        valStr = "0";
                    }
                } else if (retString.equals("0") && str.equals(".")) {
                    /* 允许0.通过 */
                    valStr = "0.";
                } else if (retString.endsWith(".") && str.equals(".")) {
                    /* 两个点取消操作 */
                    return;
                } else if (retString.equals("0")) {
                    /* 屏蔽第一个0,避免出现01、02的情况 */
                    valStr = str;
                } else {
                    valStr = retString + str;
                }
            }

            /* 使用BigDecimal保证乘100精度， 使用NumberFormat保证有无小数时置整数 */
            switch (type) {
                case RPM://转速1000-3000
                   	int valRPM = new BigDecimal(valStr).intValue();
                    if(valRPM <= 1200){
                        setInputText(valStr);

                    }
                    if(valStr.endsWith("."))
					return;

				if (valRPM == 0) {
					setInputText("0");
					once = true;
				} else if (valRPM >= 1000 && valRPM <= 1200) {
					setInputText(valStr);
				}
                    break;
                case Temp: // 温度0~100 100倍
                    if(valStr.endsWith("-")){
                        setInputText(valStr);
                        return;
                    }

                    float valTemp = new BigDecimal(valStr).floatValue();
                    if (valTemp >= stepDefault.getMinTemp() && valTemp <= stepDefault.getMaxTemp()) {
                        setInputText(valStr);
                    }
                    break;
                case Time: // 时间0~5999
                    if(valStr.endsWith("."))
                        return;
                    int valTime = new BigDecimal(valStr).intValue();
                    if (valTime >= 0 && valTime <= 59 && getTimeAll(valTime) <= stepDefault.getMaxTime().getTime() / 1000) {
                        textView_time_input.setText(valStr);
                        retString = valStr;
                    }
                    break;
                case Pos: // 位置1~pos
                    if(valStr.endsWith("."))
                        return;
                    int valStep = new BigDecimal(valStr).intValue();
                    if (valStep == 0) {
                        setInputText("1");
                        once = true;
                    } else if (valStep >= 1 && valStep <= pos) {
                        setInputText(valStr);
                    }
                    if (pos < 10) {
                        once = true;
                    }
                    break;
                case Num: // 次数1~99
				if(valStr.endsWith("."))
					return;
				int valNum = new BigDecimal(valStr).intValue();
				if (valNum == 0) {
					setInputText("1");
					once = true;
				} else if (valNum >= 1 && valNum <= 99) {
					setInputText(valStr);
				}
                    break;
                case Null:

                    break;
            }
        } catch (Exception e) {
            /* 出现异常则置零 */
            Log.d(TAG,e.toString());
        }
    }

}
