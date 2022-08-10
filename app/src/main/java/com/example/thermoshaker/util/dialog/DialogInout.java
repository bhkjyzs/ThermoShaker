package com.example.thermoshaker.util.dialog;

import static com.example.thermoshaker.base.BaseActivity.currentFs;
import static com.example.thermoshaker.base.BaseActivity.usbHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.BaseActivity;
import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.ui.adapter.AdapterInout;
import com.example.thermoshaker.ui.adapter.AdapterUsbInoutlist;
import com.example.thermoshaker.ui.file.AddAndEditActivity;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.ToastUtil;
import com.example.thermoshaker.util.dialog.base.CustomDialog;
import com.example.thermoshaker.util.usb.UsbHelper;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DialogInout extends Dialog {
    private static final String TAG = "DialogInout";

    private DialogInout(Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
    public static class Builder {
        private DialogInout mdialog;
        private View view;
        private Context context;
        private MyApplication myApp;
        private ImageButton button_left;
        private ImageButton button_right;
        private RecyclerView local_view;
        private RecyclerView usb_view;
        private AdapterInout localAdapter;
        private AdapterUsbInoutlist usbAdapter;
        private Button button;
        private View localAllSelectBtn;
        private View usbAllSelectBtn;

        private List<ProgramInfo> local_data;
        private List<UsbFile> usb_data;

        public Builder(final Context context, final MyApplication myApp) {
            this.context = context;
            this.myApp = myApp;
            mdialog = new DialogInout(context);
            view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_inout, null);

            Window win = mdialog.getWindow();
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.gravity = Gravity.CENTER;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.width = 1000;
            win.setAttributes(lp);
            mdialog.setContentView(view);
            UsbMassStorageDevice[] usbMassStorageDevices = usbHelper.getDeviceList();
            usbHelper.readDevice(usbMassStorageDevices[0]);
            //初始化数据
            local_data = myApp.getDataRefre();
            readUsbData();

//            localAllSelectBtn = mdialog.findViewById(R.id.local_all_select);
//            usbAllSelectBtn = mdialog.findViewById(R.id.usb_all_select);
//
//            localAllSelectBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    localAdapter.selectAll();
//                }
//            });
//
//            usbAllSelectBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    usbAdapter.selectAll();
//                }
//            });

            button_left = mdialog.findViewById(R.id.dialog_inout_btn_left);
            button_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    importFile(local_data);
                }
            });
            button_right = mdialog.findViewById(R.id.dialog_inout_btn_right);
            button_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    export();
                }
            });
            button = mdialog.findViewById(R.id.dialog_inout_return);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mdialog.cancel();
                }
            });
            local_view = mdialog.findViewById(R.id.dialog_inout_local_view);
            usb_view = mdialog.findViewById(R.id.dialog_inout_usb_view);
            initRecycleView();

        }


        private void importFile(List<ProgramInfo> infoList) {
            CustomDialog exportDialog = new CustomDialog.Builder(context)
                    .view(R.layout.inoutput_layout_dialog)
                    .style(R.style.CustomDialog)
                    .build();
            TextView tv_msg = null;

            List<String> stringList = usbAdapter.getSelectedPositionList();
            if (stringList != null && stringList.size() > 0) {
                for (int i = 0; i < stringList.size(); i++) {

                    exportDialog.show();
                    tv_msg = exportDialog.findViewById(R.id.tv_msg);
                    tv_msg.setText(R.string.Transmitting);
                    exportDialog.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exportDialog.dismiss();
                        }
                    });
                    exportDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exportDialog.dismiss();
                        }
                    });

                    String selectedPosition = stringList.get(i);
                    UsbFile usbFile = usb_data.get(Integer.parseInt(selectedPosition));

                    usbHelper.saveUSbFileToLocal(usbFile,DataUtil.data_path + DataUtil.data_name+usbFile.getName(), currentFs, new UsbHelper.DownloadProgressListener() {
                        @Override
                        public void downloadProgress(int progress) {
                            Log.d(TAG,progress+"  ");
                        }
                    });

                }
                local_data = myApp.getDataRefre();
                tv_msg.setText(R.string.Transmissioncomplete);

                localAdapter.setNewData(local_data);
                usbAdapter.clearSelectedPositionList();
            }else {
                Toast.makeText(context, context.getResources().getString(R.string.dialog_inout_info_1), Toast.LENGTH_SHORT).show();

            }

        }


        //导出
        private void export() {
            CustomDialog exportDialog = new CustomDialog.Builder(context)
                    .view(R.layout.inoutput_layout_dialog)
                    .style(R.style.CustomDialog)
                    .build();
            TextView tv_msg = null;
            ProgressBar pB_Level_up = exportDialog.findViewById(R.id.pB_Level_up);
            List<String> stringList = localAdapter.getSelectedPositionList();
            if (stringList != null && stringList.size() > 0) {
                for (int i = 0; i < stringList.size(); i++) {
                    exportDialog.show();
                    tv_msg = exportDialog.findViewById(R.id.tv_msg);
                    tv_msg.setText(R.string.Transmitting);

                    exportDialog.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exportDialog.dismiss();
                        }
                    });
                    exportDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exportDialog.dismiss();
                        }
                    });

                    String selectedPosition = stringList.get(i);
                    ProgramInfo programInfo = (ProgramInfo) local_data.get(Integer.parseInt(selectedPosition));
                    File file = new File(programInfo.getFilePath());
                    usbHelper.saveSDFileToUsb(file, currentFs, new UsbHelper.DownloadProgressListener() {
                        @Override
                        public void downloadProgress(int progress) {
                            Log.d(TAG,progress+"  ");
                            pB_Level_up.setProgress(progress);
                        }
                    });

                }

                readUsbData();
                tv_msg.setText(R.string.Transmissioncomplete);
                usbAdapter.setNewData(usb_data);
                localAdapter.clearSelectedPositionList();

            }else {
                Toast.makeText(context, context.getResources().getString(R.string.dialog_inout_info_2), Toast.LENGTH_SHORT).show();

            }





        }

        private void initRecycleView() {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            local_view.setLayoutManager(linearLayoutManager);
            // 获取数据，向适配器传数据，绑定适配器
            localAdapter = new AdapterInout(R.layout.dialog_input_list_item);
            localAdapter.setNewData(local_data);
            local_view.setAdapter(localAdapter);

            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
            usb_view.setLayoutManager(linearLayoutManager2);
            // 获取数据，向适配器传数据，绑定适配器
            usbAdapter = new AdapterUsbInoutlist(R.layout.dialog_input_list_item);
            usbAdapter.setNewData(usb_data);
            usb_view.setAdapter(usbAdapter);
        }

        public DialogInout create() {
            mdialog.setCancelable(false);                //用户可以点击后退键关闭 Dialog
            mdialog.setCanceledOnTouchOutside(false);   //用户可以点击外部来关闭 Dialog
            mdialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Log.i(TAG, "dialog is exit");
                }
            });
            return mdialog;
        }

        public void readUsbData() {
            BaseActivity.usbWrite();
            try {
            UsbFile root = BaseActivity.currentFs.getRootDirectory();
            usb_data = new ArrayList<>();

            for (UsbFile file :  root.listFiles()) {
                Log.i(TAG,file.getName());

                    if (file.getName().endsWith(".Tso")) {


                        usb_data.add(file);
                }
            }
//            root = null;
            Log.i(TAG,usb_data.toString()+"   ");
            }catch (Exception e){
                Log.d(TAG,e.getMessage()+"");
            }

        }

        public void saveUsbData() {
//            UsbMassStorageDevice[] usbMassStorageDevices = usbHelper.getDeviceList();
//            usbHelper.readDevice(usbMassStorageDevices[0]);
//
//            for (ProgramInfo programInfo : usb_data) {
//                String jsonOutput = JSON.toJSONString(programInfo);
//                File file = new File(programInfo.getFilePath());
//                usbHelper.saveSDFileToUsb(file, usbHelper.getCurrentFolder(), new UsbHelper.DownloadProgressListener() {
//                    @Override
//                    public void downloadProgress(int progress) {
//
//                    }
//                });
//
//            }
        }
    }



}
