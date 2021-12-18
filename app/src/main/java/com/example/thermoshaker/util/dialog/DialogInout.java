package com.example.thermoshaker.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.ui.adapter.AdapterInout;
import com.example.thermoshaker.ui.file.AddAndEditActivity;
import com.example.thermoshaker.util.DataUtil;
import com.example.thermoshaker.util.ToastUtil;

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
        private AdapterInout usbAdapter;
        private Button button;
        private View localAllSelectBtn;
        private View usbAllSelectBtn;

        private List<ProgramInfo> local_data;
        private List<ProgramInfo> usb_data;

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

            //初始化数据
            local_data = myApp.getData();
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
                    export(usb_data);
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
            List<String> nameList = new ArrayList<>();
            for (int i = 0; i < infoList.size(); i++) {
                nameList.add(infoList.get(i).getFileName());
            }

            StringBuilder stringBuilder = new StringBuilder();
            List<String> stringList = usbAdapter.getSelectedPositionList();
            if (stringList != null && stringList.size() > 0) {
                for (int i = 0; i < stringList.size(); i++) {
                    String selectedPosition = stringList.get(i);
                    ProgramInfo programInfo = (ProgramInfo) usb_data.get(Integer.parseInt(selectedPosition));
                    if (!nameList.contains(programInfo.getFileName())) {
                        infoList.add(programInfo);
                    } else {
                        stringBuilder.append(programInfo.getFileName() + ",");
                        continue;
                    }
                }
                if (stringBuilder.length() > 0) {
                    String content = stringBuilder.toString();
                    if (content.endsWith(",")) {
                        content = content.substring(0,content.length() - 1)+ context.getResources().getString(R.string.file_existed);
                        ToastUtil.show(context, content);
                    }
                }
                //先注释
//                myApp.saveData();
                localAdapter.setList(infoList);
                Log.d(TAG,stringList.size()+"");
                int time = 0;
                for (int i = 0; i < stringList.size(); i++) {
                    time+=4000;
                }


                try {
                    Thread.sleep(time);
                    TipsDialog tipsDialog = new TipsDialog(context,context.getString(R.string.import_succeeded));
                    tipsDialog.show();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.dialog_inout_info_1), Toast.LENGTH_SHORT).show();
            }
        }


        //导出
        private void export(List<ProgramInfo> infoList) {
            List<String> nameList = new ArrayList<>();
            for (int i = 0; i < infoList.size(); i++) {
                nameList.add(infoList.get(i).getFileName());
            }


            StringBuilder stringBuilder = new StringBuilder();
            List<String> stringList = localAdapter.getSelectedPositionList();
            if (stringList != null && stringList.size() > 0) {
                for (int i = 0; i < stringList.size(); i++) {
                    String selectedPosition = stringList.get(i);
                    ProgramInfo programInfo = (ProgramInfo) local_data.get(Integer.parseInt(selectedPosition));
                    if (!nameList.contains(programInfo.getFileName())) {
                        infoList.add(programInfo);
                    } else {
                        stringBuilder.append(programInfo.getFileName() + ",");
                        continue;
                    }
                }
                if (stringBuilder.length() > 0) {
                    String content = stringBuilder.toString();
                    if (content.endsWith(",")) {
                        content = content.substring(0, content.length() - 1)+ context.getResources().getString(R.string.file_existed);
                        ToastUtil.show(context, content);
                    }
                }
                saveUsbData();
                usbAdapter.setList(infoList);
                int time = 0;
                for (int i = 0; i < stringList.size(); i++) {
                    time+=4000;
                }


                try {
                    Log.d(TAG,time+"");
                    Thread.sleep(time);
                    TipsDialog tipsDialog = new TipsDialog(context,context.getString(R.string.export_succeeded));
                    tipsDialog.show();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.dialog_inout_info_2), Toast.LENGTH_SHORT).show();
            }
        }

        private void initRecycleView() {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            local_view.setLayoutManager(linearLayoutManager);
            // 获取数据，向适配器传数据，绑定适配器
            localAdapter = new AdapterInout(R.layout.dialog_input_list_item);
            localAdapter.setList(myApp.getData());
            local_view.setAdapter(localAdapter);

            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
            usb_view.setLayoutManager(linearLayoutManager2);
            // 获取数据，向适配器传数据，绑定适配器
            usbAdapter = new AdapterInout(R.layout.dialog_input_list_item);
            usbAdapter.setList(usb_data);
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
            try {


            File Files = new File(Content.usb_path+"");
            String absolutePath = Files.getAbsolutePath();
            File rootFile = new File(absolutePath);
            usb_data = new ArrayList<>();
            if (!rootFile.exists()) {
                return;
            }
            for (File file : rootFile.listFiles()) {
                Log.i(TAG,file.getAbsolutePath());
                Log.i(TAG,file.getName());

//                if (file.isFile() && file.getName().endsWith(".Tso")) {
                    if ( file.getAbsoluteFile().getName().endsWith(".Tso")) {

                        String temp = DataUtil.readData(file.getAbsolutePath());
                    ProgramInfo info = JSON.parseObject(temp, ProgramInfo.class);
                    if (info != null) {
                        usb_data.add(info);
                    }
                }
            }
            rootFile = null;
            Log.i(TAG,usb_data.toString()+"   "+Content.usb_path);
            Log.i(TAG,Content.usb_path);
            }catch (Exception e){
                Log.d(TAG,e.getMessage()+"");
            }

        }

        public void saveUsbData() {
            for (ProgramInfo programInfo : usb_data) {
                String jsonOutput = JSON.toJSONString(programInfo);
                File rootFile = new File(Content.usb_path);

                DataUtil.writeData(jsonOutput,  rootFile.getAbsolutePath()+"/", programInfo.getFileName() + "", false);
            }
        }
    }



}
