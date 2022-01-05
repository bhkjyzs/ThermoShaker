package com.example.thermoshaker.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.thermoshaker.R;


public class TipsDialog extends Dialog {
    private onDialogLister onDialogLister;
    private String text;
    public TipsDialog(@NonNull Context context, String text) {
        super(context, R.style.CustomDialog);
        this.text = text;
    }
    public interface onDialogLister{
        void onCancel();

        void onConfirm();
    }

    public void setOnDialogLister(onDialogLister onDialogLister){
        this.onDialogLister = onDialogLister;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.error_layout_dialog, null);
        setContentView(view);
        setCanceledOnTouchOutside(true);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);

        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        TextView tv_msg = findViewById(R.id.tv_msg);
        tv_msg.setText(text);
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
                    onDialogLister.onCancel();
                }
                dismiss();

            }
        });


    }

    @Override
    public void show() {
        super.show();
        if (this.getWindow() != null) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            super.show();
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

    }


}
