package com.example.thermoshaker.util.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.example.thermoshaker.ui.adapter.MyAdapter;
import com.example.thermoshaker.util.dialog.base.CustomKeyEditDialog;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;
import com.flyco.tablayout.SlidingTabLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DebugDialog {

    Context context;

    public DebugDialog(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        ArrayList<View> viewList = new ArrayList<>();
        String[] title={context.getString(R.string.debugmodule)+"",context.getString(R.string.Temperaturecorrection)+"",context.getString(R.string.Localcorrelation)+""};



        CustomkeyDialog seniorDialog = new CustomkeyDialog.Builder(context)
                .view(R.layout.debug_dialog_layout)
                .style(R.style.CustomDialog)
                .build();
        seniorDialog.show();
        Button btnCancel = seniorDialog.findViewById(R.id.btnCancel);
        ViewPager vp_debug = seniorDialog.findViewById(R.id.vp_debug);
        SlidingTabLayout stb_Debug = seniorDialog.findViewById(R.id.stb_Debug);
        //添加view
        viewList.add(LayoutInflater.from(context).inflate(R.layout.debugmodule_dialog_vp_layout,null,false));
        viewList.add(LayoutInflater.from(context).inflate(R.layout.functionmodule_dialog_vp_layout,null,false));
        viewList.add(LayoutInflater.from(context).inflate(R.layout.localcorrelation_dialog_vp_layout,null,false));
        vp_debug.setAdapter(new MyAdapter(viewList));





        stb_Debug.setViewPager(vp_debug,title);
        seniorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seniorDialog.dismiss();
            }
        });

    }


}
