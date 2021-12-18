package com.example.thermoshaker.ui.file;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.dialog.CustomkeyDialog;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.MyTableTextView;
import com.example.thermoshaker.util.dialog.TipsDialog;
import com.example.thermoshaker.util.key.KeyBoardActionListener;
import com.example.thermoshaker.util.key.SystemKeyboard;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class AddAndEditActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = "AddAndEditActivity";
    private TextView tv_times;
    private LinearLayout MyTable;
    private LinearLayout linearLayout;
    private RecyclerView rv_list;
    private RVListAdapter rvListAdapter;
    private int ChoosePos = -1;
    private String Direction="正转";
    private int DirectionType = 0;
    private LinearLayout ll_add,ll_save,ll_run,ll_del,ll_return;
    private boolean isEdit = false;
    //当前正在操作的文件
    private ProgramInfo programInfo;

    //key
    private EditText ediTemperature,ediSpeed,editTime,editRevolution,editShockTime,editPauseTime;
    private SystemKeyboard systemKeyboard;

    @Override
    protected int getLayout() {
        return R.layout.activity_add_and_edit;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        programInfo = (ProgramInfo) intent.getSerializableExtra("ProgramInfo");
        isEdit = intent.getBooleanExtra("isEdit",false);
        GetViews();
    }

    private void GetViews() {
        tv_times = findViewById(R.id.tv_times);
        MyTable = findViewById(R.id.MyTable);
        rv_list = findViewById(R.id.rv_list);
        ll_add = findViewById(R.id.ll_add);
        ll_save = findViewById(R.id.ll_save);
        ll_run = findViewById(R.id.ll_run);
        ll_del = findViewById(R.id.ll_del);
        ll_return = findViewById(R.id.ll_return);
        ll_add.setOnClickListener(this);
        ll_save.setOnClickListener(this);
        ll_run.setOnClickListener(this);
        ll_del.setOnClickListener(this);
        ll_return.setOnClickListener(this);

        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rvListAdapter = new RVListAdapter(R.layout.file_step_list_item);

        rvListAdapter.setList(programInfo.getStepList());
        rv_list.setAdapter(rvListAdapter);
        rvListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ChoosePos = position;
                rvListAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void initDate() {
        updateSystemTime(tv_times);
        initLinViews();

    }

    @SuppressLint("SetTextI18n")
    private void initLinViews() {
        //初始化标题
         linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.table_item, null);
        MyTableTextView title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_1);
        title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_2);
        title.setText(R.string.temperature);
        title.setTextColor(Color.BLACK);
        title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_3);
        title.setText(R.string.speed);
        title.setTextColor(Color.BLACK);
        title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_4);
        title.setText(R.string.time);
        title.setTextColor(Color.BLACK);
        title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_5);
        title.setText(R.string.revolution);
        title.setTextColor(Color.BLACK);
        title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_6);
        title.setText(R.string.direction);
        title.setTextColor(Color.BLACK);
        title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_7);
        title.setText(R.string.shock);
        title.setTextColor(Color.BLACK);
        title = (MyTableTextView) linearLayout.findViewById(R.id.list_1_8);
        title.setText(R.string.blank);
        title.setTextColor(Color.BLACK);
        MyTable.addView(linearLayout);

        //初始化内容
        for (int i=1;i<6;i++) {
            linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.table_item, null);
            MyTableTextView txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_1);
            txt.setText(i+"");
            txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_2);
            txt.setText("");
            txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_3);
            txt.setText("");
            txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_4);
            txt.setText("");
            txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_5);
            txt.setText("");
            txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_6);
            txt.setText("");
            txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_7);
            txt.setText("");
            txt = (MyTableTextView) linearLayout.findViewById(R.id.list_1_8);
            txt.setText("");
            MyTable.addView(linearLayout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_add:
                showAdd();
                break;
            case R.id.ll_save:

                TipsDialog tipsDialog = new TipsDialog(AddAndEditActivity.this,getString(R.string.savefilesdialog));
                tipsDialog.show();
                tipsDialog.setOnDialogLister(new TipsDialog.onDialogLister() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onConfirm() {
                        if(!isEdit){
                            if(programInfo.getStepList().size()!=0){

                            Log.d(TAG,JSON.toJSON(programInfo)+"");

                            DataUtil.writeData(JSON.toJSON(programInfo)+"", DataUtil.data_path+ DataUtil.data_name, programInfo.getFileName() + ".Tso", false);
                            finish();
                            overridePendingTransition(0, 0);

                            }else {
                                finish();
                            }

                            return;
                        }
                        String path = programInfo.getFilePath();

                        if (null != path) {//已经存在本地的文件进行删除，路径不为空
                            File file = new File(path);
                            boolean exists = file.exists();
                            boolean isSuccess = file.delete();
                            if (isSuccess) {
                                Log.d(TAG,JSON.toJSON(programInfo)+"");
                                DataUtil.writeData(JSON.toJSON(programInfo)+"", DataUtil.data_path+ DataUtil.data_name, programInfo.getFileName() + "", false);

                            }
                        }
                        finish();
                        overridePendingTransition(0, 0);

                    }
                });

                break;
            case R.id.ll_run:

                break;
            case R.id.ll_del:
                if(ChoosePos==-1){
                    ToastUtil.show(this,getString(R.string.pleasechoosefile));
                    return;
                }
                programInfo.getStepList().remove(ChoosePos);
                rvListAdapter.getData().remove(ChoosePos);
                rvListAdapter.notifyDataSetChanged();
                ChoosePos = -1;
                break;
            case R.id.ll_return:

                if(!compareList(MyApplication.programsSteps,programInfo.getStepList())){
                    TipsDialog tipsDialogReturn = new TipsDialog(AddAndEditActivity.this,getString(R.string.issave));
                    tipsDialogReturn.show();
                    tipsDialogReturn.setOnDialogLister(new TipsDialog.onDialogLister() {
                        @Override
                        public void onCancel() {
                            finish();
                            overridePendingTransition(0, 0);

                        }

                        @Override
                        public void onConfirm() {
                            if(!isEdit){
                                Log.d(TAG,JSON.toJSON(programInfo)+"");

                                DataUtil.writeData(JSON.toJSON(programInfo)+"", DataUtil.data_path+ DataUtil.data_name, programInfo.getFileName() + ".Tso", false);
                                finish();
                                overridePendingTransition(0, 0);

                                return;
                            }
                            String path = programInfo.getFilePath();

                            if (null != path) {//已经存在本地的文件进行删除，路径不为空
                                File file = new File(path);
                                boolean exists = file.exists();
                                boolean isSuccess = file.delete();
                                if (isSuccess) {
                                    Log.d(TAG,JSON.toJSON(programInfo)+"");
                                    DataUtil.writeData(JSON.toJSON(programInfo)+"", DataUtil.data_path+ DataUtil.data_name, programInfo.getFileName() + "", false);

                                }
                            }
                            finish();
                            overridePendingTransition(0, 0);


                        }
                    });


                    return;
                }
                finish();
                overridePendingTransition(0, 0);

                break;

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(compareList(MyApplication.programsSteps,programInfo.getStepList()) && programInfo.getStepList().size()==0){
            String path = programInfo.getFilePath();
            if (null != path) {//已经存在本地的文件进行删除，路径不为空
                File file = new File(path);
                boolean exists = file.exists();
                boolean isSuccess = file.delete();
                if (isSuccess) {
                    Log.d(TAG,JSON.toJSON(programInfo)+"");
                }
            }
            Toast.makeText(this, getString(R.string.nostepdelfile)+"", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(0, 0);
            return;
        }
    }

    //判断实体类的数据是否一致，需要重写equals方法
    public static boolean compareList(List<?> list1, List<?> list2) {
        if (list1 == null) {
            if (list2 == null) {
                return true;
            }
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }
//        if (!list1.equals(list2)) {
//            return false;
//        }
        for (int i = 0; i < list1.size(); i++) {
            if(!list1.get(i).equals(list1.get(i))){
                return false;
            }
        }
        return true;
    }




    public void showAdd(){
        CustomkeyDialog AddFileDialog = new CustomkeyDialog.Builder(this)
                .view(R.layout.add_step_layout)
                .style(R.style.CustomDialog)
                .build();
        AddFileDialog.show();
        RadioGroup rg_v = AddFileDialog.findViewById(R.id.rg_v);
        RadioGroup rg_btm = AddFileDialog.findViewById(R.id.rg_btm);
        RadioButton rbTopOne = AddFileDialog.findViewById(R.id.rbTopOne);
        RadioButton rbTopTwo = AddFileDialog.findViewById(R.id.rbTopTwo);
        RadioButton rbBtmOne = AddFileDialog.findViewById(R.id.rbBtmOne);
        RadioButton rbBtmTwo = AddFileDialog.findViewById(R.id.rbBtmTwo);
        Button btnSure = AddFileDialog.findViewById(R.id.btnSure);
        ConstraintLayout cl_shock = AddFileDialog.findViewById(R.id.cl_shock);
        ConstraintLayout clPause = AddFileDialog.findViewById(R.id.clPause);
//        cl_shock.setVisibility(View.GONE);
//        clPause.setVisibility(View.GONE);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAll();
                AddFileDialog.dismiss();

            }
        });
        rg_v.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbTopOne:
                        if(rbTopOne.isChecked())
                        rg_btm.clearCheck();
                        Direction = getString(R.string.ForwardRotation);
                        DirectionType=0;
//                        cl_shock.setVisibility(View.GONE);
//                        clPause.setVisibility(View.GONE);
                        editShockTime.setEnabled(false);
                        editPauseTime.setEnabled(false);
                        break;
                    case R.id.rbTopTwo:
                        if(rbTopTwo.isChecked())

                        rg_btm.clearCheck();
                        Direction = getString(R.string.reversal);
                        DirectionType=0;
//                        cl_shock.setVisibility(View.GONE);
//                        clPause.setVisibility(View.GONE);
                        editShockTime.setEnabled(false);
                        editPauseTime.setEnabled(false);
                        break;
                }
            }
        });
        rg_btm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbBtmOne:
                        if(rbBtmOne.isChecked())
                        rg_v.clearCheck();
                        Direction = getString(R.string.Intermittent);
                        DirectionType=1;
//                        cl_shock.setVisibility(View.VISIBLE);
//                        clPause.setVisibility(View.VISIBLE);
                        editShockTime.setEnabled(true);
                        editPauseTime.setEnabled(true);
                        break;
                    case R.id.rbBtmTwo:
                        if(rbBtmTwo.isChecked())
                        rg_v.clearCheck();
                        Direction = getString(R.string.Intermittentinversion);
                        DirectionType=1;
//                        cl_shock.setVisibility(View.VISIBLE);
//                        clPause.setVisibility(View.VISIBLE);
                        editShockTime.setEnabled(true);
                        editPauseTime.setEnabled(true);
                        break;
                }
            }
        });
        ediTemperature = AddFileDialog.findViewById(R.id.ediTemperature);
         ediSpeed = AddFileDialog.findViewById(R.id.ediSpeed);
         editTime = AddFileDialog.findViewById(R.id.editTime);
         editRevolution = AddFileDialog.findViewById(R.id.editRevolution);
         editShockTime = AddFileDialog.findViewById(R.id.editShockTime);
         editPauseTime = AddFileDialog.findViewById(R.id.editPauseTime);
        ediTemperature.setOnFocusChangeListener(this);
        ediSpeed.setOnFocusChangeListener(this);
        editTime.setOnFocusChangeListener(this);
        editRevolution.setOnFocusChangeListener(this);
        editShockTime.setOnFocusChangeListener(this);
        editPauseTime.setOnFocusChangeListener(this);

        systemKeyboard = AddFileDialog.findViewById(R.id.customNumKeyView);
        systemKeyboard.setEditText(ediTemperature);
        systemKeyboard.setOnKeyboardActionListener(new KeyBoardActionListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onTextChange(Editable editable) {

            }

            @Override
            public void onClear() {

            }

            @Override
            public void onClearAll() {

            }
        });


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.ediTemperature: //绑定EditText并显示自定义键盘
                systemKeyboard.setEditText((EditText) v);
                break;
            case R.id.ediSpeed:
                systemKeyboard.setEditText((EditText) v);
                break;
            case R.id.editTime:
                systemKeyboard.setEditText((EditText) v);
                break;
            case R.id.editRevolution:
                systemKeyboard.setEditText((EditText) v);
                break;
            case R.id.editShockTime:
                systemKeyboard.setEditText((EditText) v);
                break;
            case R.id.editPauseTime:
                systemKeyboard.setEditText((EditText) v);
                break;
        }
    }

    private void saveAll(){
        if(programInfo.getStepList().size()>=5){
            tips();
            return;
        }
        //温度
        String ediTempera =  ediTemperature.getText().toString();
        if(ediTempera.equals("")){
            ediTempera="37";
        }
        Float tt = Float.parseFloat(ediTempera);

        float str = Float.parseFloat(String.format("%.2f", tt));
        //速度
        String ediS =  ediSpeed.getText().toString();
        if(ediS.equals("")){
            ediS="4";
        }
        Float ttSpeed = Float.parseFloat(ediS);

        float strSpeed = Float.parseFloat(String.format("%.2f", ttSpeed));
        //时间
        String timeString = editTime.getText().toString();
        if(timeString.equals("")){
            timeString = "1";
        }
        int Time = Integer.parseInt(timeString);
        //转速
        String RevolutionString = editRevolution.getText().toString();
        if(RevolutionString.equals("")){
            RevolutionString = "1500";
        }
        int Revolution = Integer.parseInt(RevolutionString);

        //震荡时间和暂停时间
        int Shock = 0;
        int Pause = 0;
        if(DirectionType!=0){
            String ShockTimeString = editShockTime.getText().toString();
            if(ShockTimeString.equals("")){
                ShockTimeString = "60";
            }
            Shock = Integer.parseInt(ShockTimeString);

            String PauseTimeString = editPauseTime.getText().toString();
            if(PauseTimeString.equals("")){
                PauseTimeString = "60";
            }
            Pause = Integer.parseInt(PauseTimeString);
        }


        ProgramStep programStep = new ProgramStep(str,strSpeed,Time,
                Revolution,Direction,Shock,
                Pause);
        programInfo.getStepList().add(programStep);
        Log.d(TAG,MyApplication.programsSteps.toString()+"");
        rvListAdapter.setList(programInfo.getStepList());
    }

    private void tips() {
        TipsDialog tipsDialog = new TipsDialog(AddAndEditActivity.this,getString(R.string.upperlimit));
        tipsDialog.show();
    }

    class RVListAdapter extends BaseQuickAdapter<ProgramStep, BaseViewHolder>{

        public RVListAdapter(int layoutResId) {
            super(layoutResId);
        }
        @SuppressLint("ResourceAsColor")
        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ProgramStep programStep) {
            LinearLayout ll_ = baseViewHolder.getView(R.id.ll_);
            if(ChoosePos == baseViewHolder.getAdapterPosition()){
                ll_.setBackground(getResources().getDrawable(R.drawable.bg_app_bg));

            }else {
                ll_.setBackground(getResources().getDrawable(R.drawable.bg_white));

            }
            baseViewHolder.setText(R.id.list_1_1,baseViewHolder.getAdapterPosition()+1+"");

            baseViewHolder.setText(R.id.list_1_2,programStep.getTemperature()+"")
            .setText(R.id.list_1_3,programStep.getSpeed()+"").setText(R.id.list_1_4,programStep.getTime()+"")
            .setText(R.id.list_1_5,programStep.getZSpeed()+"").setText(R.id.list_1_6,programStep.getDirection()+"")
            .setText(R.id.list_1_7,programStep.getShock()+"").setText(R.id.list_1_8,programStep.getIntermission()+"");
        }
    }
}