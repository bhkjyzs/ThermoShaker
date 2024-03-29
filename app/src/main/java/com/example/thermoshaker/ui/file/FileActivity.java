package com.example.thermoshaker.ui.file;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.FileRunProgram;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.SystemProgram;
import com.example.thermoshaker.serial.CommandDateUtil;
import com.example.thermoshaker.serial.ControlParam;
import com.example.thermoshaker.serial.DataUtils;
import com.example.thermoshaker.util.BroadcastManager;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.MutilBtnUtil;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;
import com.example.thermoshaker.util.dialog.TipsDialog;
import com.example.thermoshaker.util.key.KeyBoardUtil;
import com.example.thermoshaker.util.key.Util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
public class FileActivity extends BaseActivity implements View.OnClickListener {
    public final static String MSG = FileActivity.class.getName();
    private static final String TAG = "FileActivity";

    private TextView tv_times,tv_number;
    private RecyclerView rv_list;
    private RVListFileAdapter rvListFileAdapter;
    private LinearLayout ll_add,ll_saveAs,ll_edit,ll_run,ll_del,ll_return;
    private int ChooseFilePos = -1;
    private boolean isNew = true;
    @Override
    protected int getLayout() {
        return R.layout.activity_file;
    }

    @Override
    protected void initView() {
        GetViews();
    }

    private void GetViews() {
        tv_number = findViewById(R.id.tv_number);
        rv_list = findViewById(R.id.rv_list);
        tv_times = findViewById(R.id.tv_times);
        rv_list.setLayoutManager(new GridLayoutManager(this,6));
        ll_add = findViewById(R.id.ll_add);
        ll_saveAs = findViewById(R.id.ll_saveAs);
        ll_edit = findViewById(R.id.ll_edit);
        ll_run = findViewById(R.id.ll_run);
        ll_del = findViewById(R.id.ll_del);
        ll_return = findViewById(R.id.ll_return);
        ll_add.setOnClickListener(this);
        ll_saveAs.setOnClickListener(this);
        ll_edit.setOnClickListener(this);
        ll_run.setOnClickListener(this);
        ll_del.setOnClickListener(this);
        ll_return.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(rv_list!=null){
            List<ProgramInfo> dataRefre = MyApplication.getInstance().getDataRefre();
            rvListFileAdapter.setNewData(dataRefre);
            tv_number.setText(rvListFileAdapter.getData().size()+"/"+ Content.FileNumberNum);
        }
    }

    @Override
    public void onClick(View v) {
        if (!MutilBtnUtil.isFastClick()) {
            return;
        }
        switch (v.getId()){
            case R.id.ll_add:
                if(rvListFileAdapter.getData().size()>50){
                    return;
                }
                isNew = true;
                showAddFileNameDialog();
                break;
            case R.id.ll_saveAs:
                if(ChooseFilePos==-1){
                    ToastUtil.show(this,getString(R.string.pleasechoosefile));
                    return;
                }
                isNew = false;
                showAddFileNameDialog();
                break;
            case R.id.ll_edit:
                if(ChooseFilePos==-1){
                    ToastUtil.show(this,getString(R.string.pleasechoosefile));
                    return;
                }
                GoEditPage(rvListFileAdapter.getData().get(ChooseFilePos),true);
                break;
            case R.id.ll_run:
                if(ChooseFilePos==-1){
                    ToastUtil.show(this,getString(R.string.pleasechoosefile));

                    return;
                }
                TipsDialog runDialog = new TipsDialog(FileActivity.this,getString(R.string.isRun));
                runDialog.show();
                runDialog.setOnDialogLister(new TipsDialog.onDialogLister() {
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onConfirm() {
                        runFile(rvListFileAdapter.getData().get(ChooseFilePos));
                    }
                });



                break;
            case R.id.ll_del:
                if(ChooseFilePos==-1){
                    ToastUtil.show(this,getString(R.string.pleasechoosefile));
                    return;
                }
                TipsDialog tipsDialog = new TipsDialog(FileActivity.this,getString(R.string.isdelete));
                tipsDialog.show();
                tipsDialog.setOnDialogLister(new TipsDialog.onDialogLister() {
                    @Override
                    public void onCancel() {
                    }
                    @Override
                    public void onConfirm() {
                        List<ProgramInfo> temp1 = rvListFileAdapter.getData();
                        ProgramInfo programInfo = temp1.get(ChooseFilePos);
                        String path = programInfo.getFilePath();

                        if (null != path) {//已经存在本地的文件进行删除，路径不为空
                            File file = new File(path);
                            boolean exists = file.exists();
                            boolean isSuccess = file.delete();
                            if (isSuccess) {
                                rvListFileAdapter.getData().remove(ChooseFilePos);
                                rvListFileAdapter.notifyDataSetChanged();
                            }
                        }
                        ChooseFilePos = -1;
                    }
                });
                break;
            case R.id.ll_return:
                finish();
                overridePendingTransition(0, 0);

                break;
        }
    }


    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }



    public void showAddFileNameDialog(){
        CustomkeyDialog AddFileDialog = new CustomkeyDialog.Builder(this)
                .view(R.layout.add_name_layout)
                .style(R.style.CustomDialog)
                .build();
        AddFileDialog.show();
        EditText ed_name = AddFileDialog.findViewById(R.id.ed_name);
        KeyboardView keyboardView = AddFileDialog.findViewById(R.id.keyboardView);
        final KeyBoardUtil keyBoardUtil = new KeyBoardUtil(this, keyboardView, AddFileDialog, ed_name);
        keyBoardUtil.setEditText(ed_name);

        ed_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    keyBoardUtil.setEditText(ed_name);
                }
            }
        });
        ed_name.postDelayed(new Runnable() {
            @Override
            public void run() {
                ed_name.setFocusable(true);
                ed_name.requestFocus();
                ed_name.setFocusableInTouchMode(true);
            }
        }, 100);
        Util.disableCopyAndPaste(ed_name);
    }
    @Override
    protected void initDate() {
        updateSystemTime(tv_times);
        rvListFileAdapter = new RVListFileAdapter(R.layout.file_list_item);
        rv_list.setAdapter(rvListFileAdapter);
        View view = LayoutInflater.from(this).inflate(R.layout.empty_layout,null,false);
        rvListFileAdapter.setEmptyView(view);
        rvListFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ChooseFilePos = position;
                rvListFileAdapter.notifyDataSetChanged();
            }
        });

        BroadCast();
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
                        if(!TextUtils.isEmpty(json)){
                            if(isNew){
                                ProgramInfo programInfo = new ProgramInfo(json);
                                GoEditPage(programInfo,false);
                            }else {
                                try {
                                    DataUtil.fileCopy(rvListFileAdapter.getData().get(ChooseFilePos).getFilePath(),DataUtil.data_path+ DataUtil.data_name+json+".Tso");
                                    List<ProgramInfo> dataRefre = MyApplication.getInstance().getDataRefre();
                                    rvListFileAdapter.setNewData(dataRefre);
                                    tv_number.setText(rvListFileAdapter.getData().size()+"/"+ Content.FileNumberNum);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(this).destroy(MSG);
    }

    public void GoEditPage(ProgramInfo programInfo,boolean isEdit){
        MyApplication.programsSteps = programInfo;
        Intent intent = new Intent(this,AddAndEditActivity.class);
        intent.putExtra("ProgramInfo",programInfo);
        intent.putExtra("isEdit",isEdit);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }



    class RVListFileAdapter extends BaseQuickAdapter<ProgramInfo, BaseViewHolder>{


        public RVListFileAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ProgramInfo s) {
            LinearLayout mll_ = baseViewHolder.getView(R.id.mll_);
            baseViewHolder.setText(R.id.tv_fileName,s.getFileName()+"");
            if(ChooseFilePos == baseViewHolder.getAdapterPosition()){
                mll_.setBackground(getResources().getDrawable(R.drawable.file_bg_shape_true));

            }else {
                mll_.setBackground(getResources().getDrawable(R.drawable.file_bg_shape));
            }


        }
    }

}