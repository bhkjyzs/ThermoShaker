package com.example.thermoshaker.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.MainActivity;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.SettingListBean;
import com.example.thermoshaker.util.AppManager;
import com.example.thermoshaker.util.CustomDialog;
import com.example.thermoshaker.util.LanguageUtil;
import com.example.thermoshaker.util.Utils;
import com.example.thermoshaker.util.key.KeyBoardActionListener;
import com.example.thermoshaker.util.key.SystemKeyboard;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingActivity extends BaseActivity {
    private RecyclerView rv_list;
    private TextView tv_times;
    private RVSettingListAdapter rvSettingListAdapter;
    private List<String> listNames = new ArrayList<>();
    private List<Integer> listImgs = new ArrayList<>();

    private List<SettingListBean> mList = new ArrayList<>();

    private Locale locale;

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        GetViews();
    }

    private void GetViews() {
        listNames.add(getString(R.string.changlanguage)+"");
        listNames.add(getString(R.string.voice_setting)+"");
        listNames.add(getString(R.string.firmwareupdate)+"");
        listNames.add(getString(R.string.softwareupgrade)+"");
        listNames.add(getString(R.string.nativeinformation)+"");
        listNames.add(getString(R.string.setting_factory));

        listNames.add(getString(R.string.returnname)+"");

        listImgs.add(R.drawable.echangec);
        listImgs.add(R.drawable.voicesetting);
        listImgs.add(R.drawable.firmwareupdate);
        listImgs.add(R.drawable.softerwareupdate);
        listImgs.add(R.drawable.nativeinformation);
        listImgs.add(R.drawable.factory_img);
        listImgs.add(R.drawable.return_img);
        tv_times = findViewById(R.id.tv_times);
        rv_list = findViewById(R.id.rv_list);
        updateSystemTime(tv_times);
        rv_list.setLayoutManager(new GridLayoutManager(this,3));
        rvSettingListAdapter = new RVSettingListAdapter(R.layout.setting_list_item);
        rv_list.setAdapter(rvSettingListAdapter);
        for (int i = 0; i < listNames.size(); i++) {
            SettingListBean settingListBean = new SettingListBean(listNames.get(i),listImgs.get(i));
            mList.add(settingListBean);
        }
        rvSettingListAdapter.setList(mList);
        rvSettingListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                switch (position){
                    case 0:
                        showLanguage();
                        break;
                    case 1:
                        voice();
                        break;
                    case 2:
                        firmware();
                        break;
                    case 3:
                        software();
                        break;
                    case 4:
                        nativeInformation();
                        break;

                    case 5:
                        factoryDialog();
                        break;

                    case 6:

                        finish();
                        break;

                }
            }
        });

    }

    private void factoryDialog() {
        CustomDialog factoryDialog = new CustomDialog.Builder(this)
                .view(R.layout.factory_layout)
                .style(R.style.CustomDialog)
                .cancelTouchout(true)
                .build();
        factoryDialog.show();
        SystemKeyboard customNumKeyView  = factoryDialog.findViewById(R.id.customNumKeyView);
        EditText dialog_keyboard_textview = factoryDialog.findViewById(R.id.dialog_keyboard_textview);

        customNumKeyView.setEditText(dialog_keyboard_textview);
        customNumKeyView.setOnKeyboardActionListener(new KeyBoardActionListener() {
            @Override
            public void onComplete() {
                switch (dialog_keyboard_textview.getText().toString()){
                    case "159357":
                        Utils.startDeskLaunch();
                        Toast.makeText(SettingActivity.this, "密码正确", Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        Toast.makeText(SettingActivity.this, "密码错误", Toast.LENGTH_SHORT).show();

                        break;
                }
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

    /**
     * 本机信息
     */
    private void nativeInformation() {
        CustomDialog nativeInformationDialog = new CustomDialog.Builder(this)
                .view(R.layout.nativeinformation_layout)
                .style(R.style.CustomDialog)
                .build();
        nativeInformationDialog.show();
        nativeInformationDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        DialogDisMiss(nativeInformationDialog);
    }

    /**
     * 软件升级
     */
    private void software() {
        CustomDialog softwareDialog = new CustomDialog.Builder(this)
                .view(R.layout.software_layout)
                .style(R.style.CustomDialog)
                .build();
        softwareDialog.show();
        softwareDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        DialogDisMiss(softwareDialog);

    }

    /**
     * 固件升级
     */
    private void firmware() {
        CustomDialog firmwareDialog = new CustomDialog.Builder(this)
                .view(R.layout.change_firmware_layout)
                .style(R.style.CustomDialog)
                .build();
        firmwareDialog.show();
        firmwareDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        DialogDisMiss(firmwareDialog);


    }

    /**
     * 声音设置
     */
    private void voice() {
        CustomDialog VoiceDialog = new CustomDialog.Builder(this)
                .view(R.layout.change_voice_layout)
                .style(R.style.CustomDialog)
                .build();
        VoiceDialog.show();
        VoiceDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        DialogDisMiss(VoiceDialog);


    }

    /**
     * 切换语言
     */
    private void showLanguage() {
        CustomDialog LanguageDialog = new CustomDialog.Builder(this)
                .view(R.layout.change_language_layout)
                .style(R.style.CustomDialog)
                .build();
        locale = LanguageUtil.getCurrentLocale(SettingActivity.this);
        LanguageDialog.show();
        RadioGroup radioGroup = LanguageDialog.findViewById(R.id.dialog_language_group);
        if (locale.toString().equals(Locale.CHINA.toString())) {
            radioGroup.check(R.id.dialog_language_btn1);
        } else {
            radioGroup.check(R.id.dialog_language_btn2);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                switch (i) {
                    case R.id.dialog_language_btn1: {
                        locale = Locale.CHINA;
                        break;
                    }
                    case R.id.dialog_language_btn2: {
                        locale = Locale.ENGLISH;
                        break;
                    }
                }
            }
        });
        LanguageDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageUtil.updateLocale(SettingActivity.this, locale);
                AppManager.getAppManager().finishActivity();
                startActivity(new Intent(SettingActivity.this, SettingActivity.class));
                LanguageDialog.cancel();
            }
        });
        DialogDisMiss(LanguageDialog);


    }





    public void DialogDisMiss(CustomDialog customDialog){
        customDialog.findViewById(R.id.dialog_language_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
    }


    @Override
    protected void initDate() {

    }

    class RVSettingListAdapter extends BaseQuickAdapter<SettingListBean, BaseViewHolder>{

        public RVSettingListAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, SettingListBean s) {
            baseViewHolder.setText(R.id.tv_Name,s.getName());
            ImageView iv_img = baseViewHolder.getView(R.id.iv_img);
            iv_img.setBackgroundResource(s.getImgId());

        }
    }

}