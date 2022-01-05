package com.example.thermoshaker.ui.file;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.model.TabEntity;
import com.example.thermoshaker.ui.adapter.MyAdapter;
import com.example.thermoshaker.util.BroadcastManager;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.dialog.CustomKeyEditDialog;
import com.example.thermoshaker.util.dialog.CustomkeyDialog;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.dialog.SeniorDialog;
import com.example.thermoshaker.util.dialog.TipsDialog;
import com.example.thermoshaker.util.key.FloatingKeyboard;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddAndEditActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddAndEditActivity";
    public final static String MSG = AddAndEditActivity.class.getName();

    private TextView tv_times,tv_lidTm;
    private RecyclerView rv_Senior_list;
    private RVListSeniorAdapter rvListSeniorAdapter;

    private int ChoosePos = 0;
    private LinearLayout ll_add,ll_save,ll_run,ll_del,ll_return,ll_lid;
    private boolean isEdit = false;
    //当前正在操作的文件
    private ProgramInfo programInfo;

    private CommonTabLayout Tb_step;
    private List<String> mlistSenior = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private EditText edLoorBegin,edLoorOver,edLoorNum;
    private TextView tvTime,tvFileNmae;
    private LinearLayout mll_Revolution,mll_temp;
    private TextView tvTemperatures,tvRevolution;
    private FloatingKeyboard keyboardview;
    private View mViewSenior;
    private CheckBox ckLoorSwitch;

    private ArrayList<View> viewList = new ArrayList<>();
    @Override
    protected int getLayout() {
        return R.layout.activity_add_and_edit;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        programInfo = (ProgramInfo) intent.getSerializableExtra("ProgramInfo");
        isEdit = intent.getBooleanExtra("isEdit",false);
        BroadCast();
        GetViews();
    }
    private void BroadCast() {
        BroadcastManager.getInstance(this).addAction(MSG, new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String command = intent.getAction();

                if(!TextUtils.isEmpty(command)){
                    if((MSG).equals(command)){
                        //获取json结果
                        String json = intent.getStringExtra("result");
                        if(!TextUtils.isEmpty(json)&&json.equals("[keyboard]")){
                            //更新当前步骤数据数据
                            updateCurrentStep();
                        }
                    }
                }
            }
        });
    }




    private void GetViews() {
        tv_lidTm = findViewById(R.id.tv_lidTm);
        ll_lid = findViewById(R.id.ll_lid);
        Tb_step = findViewById(R.id.Tb_step);
        tv_times = findViewById(R.id.tv_times);
        ll_add = findViewById(R.id.ll_add);
        ll_save = findViewById(R.id.ll_save);
        ll_run = findViewById(R.id.ll_run);
        ll_del = findViewById(R.id.ll_del);
        mll_temp = findViewById(R.id.mll_temp);
        mll_Revolution = findViewById(R.id.mll_Revolution);
        ckLoorSwitch = findViewById(R.id.ckLoorSwitch);
        ll_return = findViewById(R.id.ll_return);
        edLoorBegin = findViewById(R.id.edLoorBegin);
        edLoorOver = findViewById(R.id.edLoorOver);
        edLoorNum = findViewById(R.id.edLoorNum);
        tvTemperatures = findViewById(R.id.tvTemperatures);
        tvRevolution = findViewById(R.id.tvRevolution);
        tvTime = findViewById(R.id.tvTime);
        rv_Senior_list = findViewById(R.id.rv_Senior_list);
        mViewSenior = findViewById(R.id.mViewSenior);
        tvFileNmae = findViewById(R.id.tvFileNmae);
        mViewSenior.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        ll_save.setOnClickListener(this);
        ll_run.setOnClickListener(this);
        ll_del.setOnClickListener(this);
        mll_temp.setOnClickListener(this);
        mll_Revolution.setOnClickListener(this);
        ll_return.setOnClickListener(this);
        ll_lid.setOnClickListener(this);
        tvTemperatures.setOnClickListener(this);
        tvRevolution.setOnClickListener(this);
        keyboardview = findViewById(R.id.keyboardview);
        keyboardview.registerEditText(edLoorBegin);
        keyboardview.registerEditText(edLoorOver);
        keyboardview.registerEditText(edLoorNum);
        keyboardview.setfocusCurrent(this.getWindow());
        keyboardview.setFinishAction(MSG);
        keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
        keyboardview.setPreviewEnabled(false);
        setonFocus();
        ckLoorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edLoorBegin.setEnabled(isChecked);
                edLoorOver.setEnabled(isChecked);
                edLoorNum.setEnabled(isChecked);
                programInfo.setLoopEnable(ckLoorSwitch.isChecked());
            }
        });
        tvFileNmae.setText(getString(R.string.file)+":"+programInfo.getFileName()+"");
        ckLoorSwitch.setChecked(programInfo.isLoopEnable());
        edLoorBegin.setText(programInfo.getLoopStart()+"");
        edLoorBegin.setEnabled(programInfo.isLoopEnable());
        edLoorOver.setText(programInfo.getLoopEnd()+"");
        edLoorOver.setEnabled(programInfo.isLoopEnable());
        edLoorNum.setText(programInfo.getLoopNum()+"");
        edLoorNum.setEnabled(programInfo.isLoopEnable());


        rv_Senior_list.setLayoutManager(new GridLayoutManager(this,2));
        rvListSeniorAdapter = new RVListSeniorAdapter(R.layout.layout_senior_list_item);
        rv_Senior_list.setAdapter(rvListSeniorAdapter);
        if(isEdit){
            for (int i = 0; i < programInfo.getStepList().size(); i++) {
                mTabEntities.add(new TabEntity(getString(R.string.step)+(i+1)));
            }
            showView(programInfo.getStepList().get(0));

//            for (int i = 0; i < programInfo.getStepList().size(); i++) {
//                viewList.add(LayoutInflater.from(this).inflate(R.layout.step_view_list_item,null,false));
//            }

        }else {
            mTabEntities.add(new TabEntity(getString(R.string.step1)));
            ProgramStep programStep = new ProgramStep();
            programInfo.getStepList().add(programStep);
            showView(programStep);
        }

        Tb_step.setTabData(mTabEntities);
        Tb_step.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                ChoosePos = position;
                showView(programInfo.getStepList().get(position));

            }

            @Override
            public void onTabReselect(int position) {

            }
        });


    }

    private void setonFocus() {
        edLoorNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
                    keyboardview.show(arg0);

                } else {
                    keyboardview.hide();

                    String str = edLoorNum.getText().toString().trim();
                    programInfo.setLoopNumStr(str);
                    edLoorNum.setText(programInfo.getLoopNum() + "");
                }
            }
        });
        edLoorOver.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
                    keyboardview.show(arg0);

                } else {
                    keyboardview.hide();

                    String str = edLoorOver.getText().toString().trim();
                    programInfo.setLoopEndStr(str);
                    edLoorOver.setText(programInfo.getLoopEnd() + "");
                }
            }
        });
        edLoorBegin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
                    keyboardview.show(arg0);

                } else {
                    keyboardview.hide();

                    String str = edLoorBegin.getText().toString().trim();
                    programInfo.setLoopStartStr(str);
                    edLoorBegin.setText(programInfo.getLoopStart() + "");
                }
            }
        });

    }

    private void showView(ProgramStep programStep) {

        tvTemperatures.setText(programStep.getTemperature()+"");
        tvRevolution.setText(programStep.getZSpeed()+"");
        tvTime.setText(MyApplication.getInstance().dateFormat.format(programStep.getTime())+"");


        mlistSenior.clear();
        mlistSenior.add(getString(R.string.up_speed)+""+programStep.getUpSpeedStr());
        mlistSenior.add(getString(R.string.down_speed)+""+programStep.getDownSpeedStr());
        mlistSenior.add(getString(R.string.motordirection)+""+programStep.getDirectionStr());
        mlistSenior.add(getString(R.string.mixingmode)+""+programStep.getMixingModeStr());
        mlistSenior.add(getString(R.string.blendstart)+":"+programStep.getBlendStartStr());
        rvListSeniorAdapter.setList(mlistSenior);

    }

    @Override
    protected void initDate() {
        updateSystemTime(tv_times);
        initLinViews();

    }

    @SuppressLint("SetTextI18n")
    private void initLinViews() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_lid:
                showLidkeyDialog(String.valueOf(programInfo.getLidTm()));

                break;
            case R.id.mll_temp:
                showkeyDialog(CustomKeyEditDialog.TYPE.Temp,String.valueOf(programInfo.getStepList().get(ChoosePos).getTemperature()));
                break;
            case R.id.mll_Revolution:
                showkeyDialog(CustomKeyEditDialog.TYPE.RPM,String.valueOf(programInfo.getStepList().get(ChoosePos).getZSpeed()));
                break;
            case R.id.tvTime:
                showkeyDialog(CustomKeyEditDialog.TYPE.Time,String.valueOf(MyApplication.getInstance().dateFormat.format(programInfo.getStepList().get(ChoosePos).getTime())));
                break;
            case R.id.mViewSenior:
                showSenior();

                break;
            case R.id.ll_add:
                AddStep();
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
                if(programInfo.getStepList().size()<=1){
                    ToastUtil.show(this,getString(R.string.noDelete));
                    return;
                }
                if(ChoosePos==programInfo.getStepList().size()-1) {
                    Tb_step.setCurrentTab(0);
                    programInfo.getStepList().remove(ChoosePos);
                    mTabEntities.clear();
                    for (int i = 0; i < programInfo.getStepList().size(); i++) {
                        mTabEntities.add(new TabEntity(getString(R.string.step)+(i+1)));
                    }
                    Tb_step.setTabData(mTabEntities);
                    showView(programInfo.getStepList().get(0));
                    return;
                }

                    programInfo.getStepList().remove(ChoosePos);
                    mTabEntities.clear();
                    for (int i = 0; i < programInfo.getStepList().size(); i++) {
                        mTabEntities.add(new TabEntity(getString(R.string.step)+(i+1)));
                    }
                    Tb_step.setTabData(mTabEntities);
                    showView(programInfo.getStepList().get(ChoosePos));
                break;
            case R.id.ll_return:
                Log.d(TAG,programInfo.equals(MyApplication.programsSteps)+"");
                if(!programInfo.equals(MyApplication.programsSteps)){
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

    private void showSenior() {

        SeniorDialog seniorDialog = new SeniorDialog(programInfo.getStepList().get(ChoosePos), this);
        seniorDialog.setProgramStepListener(new SeniorDialog.dismissProgramStepListener() {
            @Override
            public void disStep(boolean isSave, ProgramStep programStep) {
                if(true){
                    programInfo.getStepList().set(ChoosePos,programStep);
                    showView(programStep);
                }
            }
        });

    }
    private void showLidkeyDialog(String name) {
        CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(this);
        customKeyEditDialog.show();
        customKeyEditDialog.init(name,CustomKeyEditDialog.TYPE.Temp,ChoosePos);
        customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
            @Override
            public void onConfirm() {
                Log.d(TAG,customKeyEditDialog.getOutStr()+"    "+customKeyEditDialog.getOutTime());
                        programInfo.setLidTm(Float.parseFloat(customKeyEditDialog.getOutStr()));
                        tv_lidTm.setText(customKeyEditDialog.getOutStr()+"°C");
            }
        });

    }

    private void showkeyDialog(CustomKeyEditDialog.TYPE type,String name) {
        CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(this);
        customKeyEditDialog.show();
        customKeyEditDialog.init(name,type,ChoosePos);
        customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
            @Override
            public void onConfirm() {
                Log.d(TAG,customKeyEditDialog.getOutStr()+"    "+customKeyEditDialog.getOutTime());
                switch (type){
                    case RPM:
                        programInfo.getStepList().get(ChoosePos).setZSpeed(Integer.parseInt(customKeyEditDialog.getOutStr()));
                        tvRevolution.setText(customKeyEditDialog.getOutStr()+"");
                        break;
                    case Temp:
                        programInfo.getStepList().get(ChoosePos).setTemperature(Float.parseFloat(customKeyEditDialog.getOutStr()));
                        tvTemperatures.setText(customKeyEditDialog.getOutStr()+"");
                        break;
                    case Time:
                        try {
                            programInfo.getStepList().get(ChoosePos).setTime(MyApplication.getInstance().dateFormat.parse(customKeyEditDialog.getOutTime()).getTime());
                            tvTime.setText(customKeyEditDialog.getOutTime()+"");

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;

                }

            }
        });

    }

    /**
     * 更新当前步骤
     */
    private void updateCurrentStep() {
//        programInfo.getStepList().get(ChoosePos).setTemperature(Float.parseFloat(tvTemperatures.getText().toString().trim()));
//        programInfo.getStepList().get(ChoosePos).setZSpeed(Integer.parseInt(tvRevolution.getText().toString().trim()));
//        programInfo.getStepList().get(ChoosePos).setTime(Long.parseLong(tvTime.getText().toString().trim()));

        programInfo.setLoopEnable(ckLoorSwitch.isChecked());
        programInfo.setLoopStart(Integer.parseInt(edLoorBegin.getText().toString().trim()));
        programInfo.setLoopEnd(Integer.parseInt(edLoorOver.getText().toString().trim()));
        programInfo.setLoopNum(Integer.parseInt(edLoorNum.getText().toString().trim()));

    }



    private void AddStep() {
        if(programInfo.getStepList().size()>=5){
            tips();
            return;
        }
        ProgramStep programStep = new ProgramStep();
        programInfo.getStepList().add(programStep);
        mTabEntities.add(new TabEntity(getString(R.string.step)+(programInfo.getStepList().size())));
        Tb_step.setTabData(mTabEntities);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(this).destroy(MSG);

        if(programInfo.equals(MyApplication.programsSteps)&& programInfo.getStepList().size()==0){
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
        if (!list1.equals(list2)) {
            return false;
        }
//        for (int i = 0; i < list1.size(); i++) {
//            if(!list1.get(i).equals(list2.get(i))){
//                return false;
//            }
//        }
        return true;
    }




    private void tips() {
        TipsDialog tipsDialog = new TipsDialog(AddAndEditActivity.this,getString(R.string.upperlimit));
        tipsDialog.show();
    }

    class RVListSeniorAdapter extends BaseQuickAdapter<String,BaseViewHolder>{

        public RVListSeniorAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
            baseViewHolder.setText(R.id.tv_content,s+"");


        }
    }






}