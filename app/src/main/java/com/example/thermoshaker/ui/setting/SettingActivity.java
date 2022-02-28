package com.example.thermoshaker.ui.setting;

import static com.example.thermoshaker.util.usb.USBBroadCastReceiver.ACTION_USB_PERMISSION;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MainType;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.SettingListBean;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.upgrade.DialogHardUp;
import com.example.thermoshaker.ui.adapter.MyAdapter;
import com.example.thermoshaker.util.AppManager;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.custom.SlideButton;
import com.example.thermoshaker.util.dialog.DebugDialog;
import com.example.thermoshaker.util.dialog.FactoryDialog;
import com.example.thermoshaker.util.dialog.base.CustomDialog;
import com.example.thermoshaker.util.LanguageUtil;
import com.example.thermoshaker.util.Utils;
import com.example.thermoshaker.util.dialog.base.CustomKeyEditDialog;
import com.example.thermoshaker.util.dialog.DialogInout;
import com.example.thermoshaker.util.usb.USBBroadCastReceiver;
import com.example.thermoshaker.util.usb.UsbHelper;
import com.flyco.tablayout.SlidingTabLayout;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;
import com.github.mjdev.libaums.partition.Partition;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.licheedev.myutils.LogPlus;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
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
//        initUsb();
    }


    private void GetViews() {
        listNames.add(getString(R.string.changlanguage) + "");
        listNames.add(getString(R.string.voice_setting) + "");
        listNames.add(getString(R.string.firmwareupdate) + "");
        listNames.add(getString(R.string.softwareupgrade) + "");
        listNames.add(getString(R.string.nativeinformation) + "");
        listNames.add(getString(R.string.setting_factory));
        listNames.add(getString(R.string.ImportExport) + "");
        listNames.add(getString(R.string.Runsettings) + "");
        listNames.add(getString(R.string.inching) + "");
        listNames.add(getString(R.string.returnname) + "");

        listImgs.add(R.drawable.echangec);
        listImgs.add(R.drawable.voicesetting);
        listImgs.add(R.drawable.firmwareupdate);
        listImgs.add(R.drawable.softerwareupdate);
        listImgs.add(R.drawable.nativeinformation);
        listImgs.add(R.drawable.setting);
        listImgs.add(R.drawable.input);
        listImgs.add(R.drawable.runsetting_img);
        listImgs.add(R.drawable.inching);
        listImgs.add(R.drawable.return_img);

        tv_times = findViewById(R.id.tv_times);
        rv_list = findViewById(R.id.rv_list);
        updateSystemTime(tv_times);
        rv_list.setLayoutManager(new GridLayoutManager(this, 3));
        rvSettingListAdapter = new RVSettingListAdapter(R.layout.setting_list_item);
        rv_list.setAdapter(rvSettingListAdapter);
        for (int i = 0; i < listNames.size(); i++) {
            SettingListBean settingListBean = new SettingListBean(listNames.get(i), listImgs.get(i));
            mList.add(settingListBean);
        }
        rvSettingListAdapter.setList(mList);
        rvSettingListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                switch (position) {
                    case 0:
                        showLanguage();
                        break;
                    case 1:
                        setCompanyLogo("bio_gener/bootanimation.zip", "/td/bio_gener/bootanimation.zip", "biologo",
                                0);
//                        setCompanyLogo("Labyeah/bootanimation.zip", "/td/Labyeah/bootanimation.zip", "Labyeahlogo",
//                                 0);
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
                        inout();

                        break;
                    case 7:
                        runSetting();
                        break;
                    case 8:

                        break;
                    case 9:
                        finish();
                        overridePendingTransition(0, 0);
                        break;

                }
            }
        });

    }

    /**
     * 运行设置
     */
    private void runSetting() {
        ArrayList<View> viewList = new ArrayList<>();
        int[] runSetting = MyApplication.getInstance().appClass.getRunSetting();
        String[] title={this.getString(R.string.Temperaturerisesetting)+"",this.getString(R.string.Preheating)+"",this.getString(R.string.Heatingswitch)+""};
        CustomDialog runSettingDialog = new CustomDialog.Builder(this)
                .view(R.layout.run_setting_layout)
                .style(R.style.CustomDialog)
                .build();
        runSettingDialog.show();
        runSettingDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runSettingDialog.dismiss();
                MyApplication.getInstance().appClass.setRunSetting(runSetting);
            }
        });
        DialogDisMiss(runSettingDialog);
        SlidingTabLayout stb_RunSetting = runSettingDialog.findViewById(R.id.stb_RunSetting);
        ViewPager vp_RunSetting = runSettingDialog.findViewById(R.id.vp_RunSetting);
        viewList.add(LayoutInflater.from(runSettingDialog.getContext()).inflate(R.layout.run_setting_dialog_vp_layout,null,false));
        viewList.add(LayoutInflater.from(runSettingDialog.getContext()).inflate(R.layout.run_setting_dialog_vp_layout2,null,false));
        viewList.add(LayoutInflater.from(runSettingDialog.getContext()).inflate(R.layout.run_setting_dialog_vp_layout3,null,false));
        vp_RunSetting.setAdapter(new MyAdapter(viewList));
        //view1
        LinearLayout mll_top = viewList.get(0).findViewById(R.id.mll_top);
        CheckBox cb_top = viewList.get(0).findViewById(R.id.cb_top);
        LinearLayout mll_center = viewList.get(0).findViewById(R.id.mll_center);
        CheckBox cb_center = viewList.get(0).findViewById(R.id.cb_center);
        LinearLayout mll_btm = viewList.get(0).findViewById(R.id.mll_btm);
        CheckBox cb_btm = viewList.get(0).findViewById(R.id.cb_btm);
        LinearLayout ll_lid = viewList.get(0).findViewById(R.id.ll_lid);
        TextView tv_lidTm = viewList.get(0).findViewById(R.id.tv_lidTm);
        switch (runSetting[0]){
            case 0:
                cb_top.setChecked(true);
                cb_center.setChecked(false);
                cb_btm.setChecked(false);
                ll_lid.setEnabled(false);
                break;
            case 1:
                cb_top.setChecked(false);
                cb_center.setChecked(true);
                cb_btm.setChecked(false);
                ll_lid.setEnabled(false);
                break;
            default:
                cb_top.setChecked(false);
                cb_center.setChecked(false);
                cb_btm.setChecked(true);
                ll_lid.setEnabled(true);
                break;
        }
        mll_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_top.setChecked(true);
                cb_center.setChecked(false);
                cb_btm.setChecked(false);
                ll_lid.setEnabled(false);
                runSetting[0] = 0;
            }
        });
        mll_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_top.setChecked(false);
                cb_center.setChecked(true);
                cb_btm.setChecked(false);
                ll_lid.setEnabled(false);
                runSetting[0] = 1;

            }
        });
        mll_btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_top.setChecked(false);
                cb_center.setChecked(false);
                cb_btm.setChecked(true);
                ll_lid.setEnabled(true);
            }
        });
        ll_lid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(runSettingDialog.getContext());
                customKeyEditDialog.show();
                customKeyEditDialog.init(String.valueOf(0),CustomKeyEditDialog.TYPE.Temp,0);
                customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
                    @Override
                    public void onConfirm() {
                            if(Float.parseFloat(customKeyEditDialog.getOutStr())<50||Float.parseFloat(customKeyEditDialog.getOutStr())>2){
                                tv_lidTm.setText(customKeyEditDialog.getOutStr()+"");
                                runSetting[0] = Integer.parseInt(customKeyEditDialog.getOutStr());

                            }
                    }
                });
            }
        });

        //view2
        final String[] speeds=runSettingDialog.getContext().getResources().getStringArray(R.array.mix_speed);
        TextView keyboard_step_spinner_hole = viewList.get(1).findViewById(R.id.keyboard_step_spinner_hole);
        keyboard_step_spinner_hole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(runSettingDialog.getContext());
                customKeyEditDialog.show();
                customKeyEditDialog.init(String.valueOf(0),CustomKeyEditDialog.TYPE.Null,0);
                customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
                    @Override
                    public void onConfirm() {
                        if(Float.parseFloat(customKeyEditDialog.getOutStr())<4||Float.parseFloat(customKeyEditDialog.getOutStr())>0){
                            keyboard_step_spinner_hole.setText(customKeyEditDialog.getOutStr()+"");
                            runSetting[1] = Integer.parseInt(customKeyEditDialog.getOutStr());

                        }else {
                            Toast.makeText(runSettingDialog.getContext(), "输入不规范", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //view3
        LinearLayout mll_top3 = viewList.get(2).findViewById(R.id.mll_top);
        CheckBox cb_top3 = viewList.get(2).findViewById(R.id.cb_top);
        LinearLayout mll_center3 = viewList.get(2).findViewById(R.id.mll_center);
        CheckBox cb_center3 = viewList.get(2).findViewById(R.id.cb_center);
        if(runSetting[2]==0){
            cb_top3.setChecked(true);
            cb_center3.setChecked(false);

        }else {
            cb_top3.setChecked(false);
            cb_center3.setChecked(true);
        }
        mll_top3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_top3.setChecked(true);
                cb_center3.setChecked(false);
                runSetting[2]=0;
            }
        });
        mll_center3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_top3.setChecked(false);
                cb_center3.setChecked(true);
                runSetting[2]=1;

            }
        });


        stb_RunSetting.setViewPager(vp_RunSetting,title);



    }

    private void inout() {

        if (!Content.usb_state.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Toast.makeText(this, getText(R.string.setting_dialog_usb_no), Toast.LENGTH_SHORT).show();
            return;
        } else if (Content.usb_state.equals(Intent.ACTION_MEDIA_CHECKING)) {
            Toast.makeText(this, getText(R.string.In_preparation_USB), Toast.LENGTH_SHORT).show();

        } else if (Content.usb_state.equals(Intent.ACTION_MEDIA_MOUNTED)) {

        }

        DialogInout dialogInout = new DialogInout.Builder(this, MyApplication.getInstance()).create();
        dialogInout.show();


    }

    private void factoryDialog() {

        CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(this);
        customKeyEditDialog.show();
        customKeyEditDialog.init(getString(R.string.setting_factory) + "", CustomKeyEditDialog.TYPE.Null, 0);
        customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
            @Override
            public void onConfirm() {
                switch (customKeyEditDialog.getOutStr()) {
                    case "159357":
                        Utils.startDeskLaunch();
                        Toast.makeText(SettingActivity.this, "密码正确", Toast.LENGTH_SHORT).show();

                        break;
                    case "357159":
                        new FactoryDialog(SettingActivity.this,SettingActivity.this);

                        break;
                    case "88992477":
                        new DebugDialog(SettingActivity.this,SettingActivity.this);

                        break;
                    default:

                        Toast.makeText(SettingActivity.this, "密码错误", Toast.LENGTH_SHORT).show();

                        break;
                }

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
        TextView tv_version = nativeInformationDialog.findViewById(R.id.tv_version);
        try {
            tv_version.setText(getString(R.string.softer_version) + "  " + this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName + "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        nativeInformationDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeInformationDialog.dismiss();
            }
        });
        DialogDisMiss(nativeInformationDialog);
    }


    /**
     * 软件升级
     */
    private void software() {
        inspectUDisk();
        try {

            UsbFile root = currentFs.getRootDirectory();

            UsbFile[] files = root.listFiles();
            for (UsbFile file : files) {
                Log.d(TAG, file.getName());
                if (file.isDirectory()) {

                }
            }
            Log.d("===", "files：" + (null == files));
            if (files == null || files.length == 0) {
                Toast.makeText(this, getText(R.string.No_software_detected), Toast.LENGTH_SHORT).show();
            } else {
                final ArrayList<UsbFile> fileList = new ArrayList<UsbFile>();

                for (UsbFile n : files) {
                    if (n.getName().endsWith(".apk") && n.getName().contains("TDM_100_")) {
                        fileList.add(n);
                    }
                }

                if (fileList.size() < 1)
                    Toast.makeText(this, getText(R.string.No_software_detected), Toast.LENGTH_SHORT).show();
                else {
                    CustomDialog softwareDialogs = new CustomDialog.Builder(this)
                            .view(R.layout.update_layout_dialog)
                            .style(R.style.CustomDialog)
                            .build();
                    softwareDialogs.setTag(0);

                    softwareDialogs.show();

                    TextView tv_msg = softwareDialogs.findViewById(R.id.tv_msg);
                    tv_msg.setText(getString(R.string.ask_update_software_start) + fileList.get(0).getName() + "("
                            + ")" + getString(R.string.ask_update_software_end));
                    softwareDialogs.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                UsbFile file = fileList.get((Integer) softwareDialogs.getTag());
                                MyApplication app = MyApplication.getInstance();
                                ProgressBar pB_Level_up = softwareDialogs.findViewById(R.id.pB_Level_up);
                                softwareDialogs.findViewById(R.id.btn_sure).setVisibility(View.GONE);
                                softwareDialogs.findViewById(R.id.btn_next).setVisibility(View.GONE);
                                softwareDialogs.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                                tv_msg.setText(R.string.loadingupdate);
                                pB_Level_up.setVisibility(View.VISIBLE);
                                copyUSbFile(file, pB_Level_up);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                                softwareDialogs.dismiss();
                        }
                    });
                    softwareDialogs.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = (Integer) softwareDialogs.getTag() + 1;
                            if (pos < fileList.size()) {
                                MyApplication app = MyApplication.getInstance();
                                softwareDialogs.setTag(pos);

                                tv_msg.setText(getString(R.string.ask_update_software_start)
                                        + fileList.get(pos).getName() + "(" + ")"
                                        + getString(R.string.ask_update_software_end));
                            } else {
                                softwareDialogs.dismiss();
                            }
                        }
                    });
                    softwareDialogs.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            softwareDialogs.dismiss();
                        }
                    });

                }
            }
        } catch (Exception e) {
            Toast.makeText(SettingActivity.this, getText(R.string.update_failed), Toast.LENGTH_SHORT).show();
            LogPlus.d(TAG + "Software updates exception." + e.getMessage());
        }


    }

    private void inspectUDisk() {
        usbWrite();
        if (!Content.usb_state.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Toast.makeText(this, getText(R.string.setting_dialog_usb_no), Toast.LENGTH_SHORT).show();
            return;
        } else if (Content.usb_state.equals(Intent.ACTION_MEDIA_CHECKING)) {
            Toast.makeText(this, getText(R.string.In_preparation_USB), Toast.LENGTH_SHORT).show();

        } else if (Content.usb_state.equals(Intent.ACTION_MEDIA_MOUNTED)) {

        }
    }

    /**
     * 复制 USB 文件到本地并且升级软件
     *
     * @param file USB文件
     */

    private void copyUSbFile(final UsbFile file, ProgressBar pB_Level_up) {

        //复制到本地的文件路径
        final String filePath = DataUtil.data_path + File.separator + DataUtil.apk_data_name;
        boolean result = false;

        new Thread(new Runnable() {
            @Override
            public void run() {

                usbHelper.saveUSbFileToLocal(file, filePath, currentFs, new UsbHelper.DownloadProgressListener() {
                    @Override
                    public void downloadProgress(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String text = "Progress : " + progress;
                                Log.d(TAG, text + "");
                                pB_Level_up.setProgress(progress);

                            }
                        });
                    }
                });

                MyApplication app = MyApplication.getInstance();
                boolean bool = app
                        .exec("pm install -r " + filePath + ";" + MainType.CMD.StartApp.getValue());
                if (bool == false)
                    Toast.makeText(SettingActivity.this, getText(R.string.update_failed), Toast.LENGTH_SHORT).show();
            }
        }).start();


    }

    /**
     * 复制 USB 文件到本地并且升级固件
     *
     * @param file USB文件
     */

    private void copyUSbFileFar(final UsbFile file, ProgressBar pB_Level_up) {

        //复制到本地的文件路径
        final String filePath = DataUtil.data_path + File.separator + DataUtil.firmware_data_name;

        new Thread(new Runnable() {
            @Override
            public void run() {

                usbHelper.saveUSbFileToLocal(file, filePath, currentFs, new UsbHelper.DownloadProgressListener() {
                    @Override
                    public void downloadProgress(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String text = "Progress : " + progress;
                                Log.d(TAG, text + "");
                                pB_Level_up.setProgress(progress);
                                if(progress==100){
                                    DialogHardUp dialogHardUp = new DialogHardUp.Builder(SettingActivity.this,MyApplication.getInstance(),filePath).create();
                                    dialogHardUp.show();
                                }
                            }
                        });
                    }
                });


            }
        }).start();


    }
    /**
     * 固件升级
     */
    private void firmware() {
        inspectUDisk();
        try {
            if(currentFs==null){
                return;
            }
            UsbFile root = currentFs.getRootDirectory();
            UsbFile[] files = root.listFiles();
            for (UsbFile file : files) {
                Log.d(TAG, file.getName());
                if (file.isDirectory()) {

                }
            }
            Log.d("===", "files：" + (null == files));
            if (files == null || files.length == 0) {
                Toast.makeText(this, getText(R.string.No_software_detected), Toast.LENGTH_SHORT).show();
            } else {
                final ArrayList<UsbFile> fileList = new ArrayList<UsbFile>();

                for (UsbFile n : files) {
                    if (n.getName().endsWith(".bin") && n.getName().contains("TDM")) {
                        fileList.add(n);
                    }
                }
                if (fileList.size() < 1) {

                    Toast.makeText(this, getText(R.string.No_software_detected), Toast.LENGTH_SHORT).show();
                } else {
                    CustomDialog softwareDialogs = new CustomDialog.Builder(this)
                            .view(R.layout.update_layout_dialog)
                            .style(R.style.CustomDialog)
                            .build();
                    softwareDialogs.setTag(0);

                    softwareDialogs.show();
                    TextView title = softwareDialogs.findViewById(R.id.title);
                    title.setText(getString(R.string.firmwareupdate));
                    TextView tv_msg = softwareDialogs.findViewById(R.id.tv_msg);
                    tv_msg.setText(getString(R.string.ask_update_software_start) + fileList.get(0).getName() + "("
                            + ")" + getString(R.string.ask_update_software_end));
                    softwareDialogs.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                //TODO这里发送升级固件命令
                                UsbFile file = fileList.get((Integer) softwareDialogs.getTag());
                                MyApplication app = MyApplication.getInstance();
                                ProgressBar pB_Level_up = softwareDialogs.findViewById(R.id.pB_Level_up);
                                softwareDialogs.findViewById(R.id.btn_sure).setVisibility(View.GONE);
                                softwareDialogs.findViewById(R.id.btn_next).setVisibility(View.GONE);
                                softwareDialogs.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                                tv_msg.setText(R.string.loadingupdate);
                                pB_Level_up.setVisibility(View.VISIBLE);
                                copyUSbFileFar(file, pB_Level_up);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                                softwareDialogs.dismiss();
                        }
                    });
                    softwareDialogs.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = (Integer) softwareDialogs.getTag() + 1;
                            if (pos < fileList.size()) {
                                MyApplication app = MyApplication.getInstance();
                                softwareDialogs.setTag(pos);

                                tv_msg.setText(getString(R.string.ask_update_software_start)
                                        + fileList.get(pos).getName() + "(" + ")"
                                        + getString(R.string.ask_update_software_end));
                            } else {
                                softwareDialogs.dismiss();
                            }
                        }
                    });
                    softwareDialogs.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            softwareDialogs.dismiss();
                        }
                    });


                }


            }


        } catch (IOException e) {
//            Toast.makeText(SettingActivity.this, getText(R.string.update_failed), Toast.LENGTH_SHORT).show();
            LogPlus.d(TAG + "Software updates exception." + e.getMessage());
        }
//        CustomDialog firmwareDialog = new CustomDialog.Builder(this)
//                .view(R.layout.change_firmware_layout)
//                .style(R.style.CustomDialog)
//                .build();
//        firmwareDialog.show();
//        firmwareDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        DialogDisMiss(firmwareDialog);


    }

    /**
     * 声音设置
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void voice() {
        MyApplication app = MyApplication.getInstance();
        boolean[] bool;

        bool = app.appClass.getSettingViewSound().clone();

        CustomDialog VoiceDialog = new CustomDialog.Builder(this)
                .view(R.layout.change_voice_layout)
                .style(R.style.CustomDialog)
                .build();
        VoiceDialog.show();
        SlideButton dialog_voice_1 = VoiceDialog.findViewById(R.id.dialog_voice_1);
        SlideButton dialog_voice_2 = VoiceDialog.findViewById(R.id.dialog_voice_2);
        SlideButton dialog_voice_3 = VoiceDialog.findViewById(R.id.dialog_voice_3);
        SlideButton dialog_voice_4 = VoiceDialog.findViewById(R.id.dialog_voice_4);
        dialog_voice_1.setOnCheckedListener(new SlideButton.SlideButtonOnCheckedListener() {
            @Override
            public void onCheckedChangeListener(boolean isChecked) {
                bool[0] = isChecked;
            }
        });
        dialog_voice_2.setOnCheckedListener(new SlideButton.SlideButtonOnCheckedListener() {
            @Override
            public void onCheckedChangeListener(boolean isChecked) {
                bool[1] = isChecked;

            }
        });
        dialog_voice_3.setOnCheckedListener(new SlideButton.SlideButtonOnCheckedListener() {
            @Override
            public void onCheckedChangeListener(boolean isChecked) {
                bool[2] = isChecked;

            }
        });
        dialog_voice_4.setOnCheckedListener(new SlideButton.SlideButtonOnCheckedListener() {
            @Override
            public void onCheckedChangeListener(boolean isChecked) {
                bool[3] = isChecked;

            }
        });

        VoiceDialog.findViewById(R.id.dialog_language_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.appClass.setSettingViewSound(bool.clone());
                app.systemClass.setSoundSetting(app.appClass.getSettingViewSoundByte());

                Intent intent = new Intent(UartServer.MSG);
                intent.putExtra("serialport", new UartClass(null, app.systemClass.output()));
                app.sendBroadcast(intent);
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
                overridePendingTransition(0, 0);

            }
        });
        DialogDisMiss(LanguageDialog);


    }

    /* 设置公司logo,动画源路径，动画目标路径，公司名称，公司枚举，公司图标 */
    private void setCompanyLogo(String stris, String strpath, String name, int companyRes) {
        MyApplication app = MyApplication.getInstance();
        try {
            InputStream is = app.getAssets().open(stris);
            String path = app.getFilesDir().getPath() + strpath;
            File file = new File(path);

            /* 判断文件是否存在或是否为空 */
            if (!file.exists() || file.length() < 100)
                FileUtils.copyInputStreamToFile(is, file);

            boolean bool = app.exec("mount -o remount,rw /system;cp " + path
                    + " /system/media/bootanimation.zip;mount -o remount,ro /system");
            Log.d(TAG, bool + "    bool");

            boolean exec = app.exec("mount -o remount,rw /system;chmod 777 /system/media/bootanimation.zip");
            Log.d(TAG, exec + "    exec");


        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage() + "");
        }

    }


    public void DialogDisMiss(CustomDialog customDialog) {
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

    class RVSettingListAdapter extends BaseQuickAdapter<SettingListBean, BaseViewHolder> {

        public RVSettingListAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, SettingListBean s) {
            baseViewHolder.setText(R.id.tv_Name, s.getName());
            LinearLayout mll_settingTab = baseViewHolder.getView(R.id.mll_settingTab);
            ImageView iv_img = baseViewHolder.getView(R.id.iv_img);
            iv_img.setBackgroundResource(s.getImgId());
            if(baseViewHolder.getAdapterPosition()==8){
                mll_settingTab.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        int action = event.getAction();
                        if (action == MotionEvent.ACTION_DOWN) {
                            // 按下 处理相关逻辑 发送点动命令

                        } else if (action == MotionEvent.ACTION_UP) {
                            // 松开 todo 处理相关逻辑 发送点动停止命令

                        }
                        return false;

                    }
                });
            }

        }
    }


}