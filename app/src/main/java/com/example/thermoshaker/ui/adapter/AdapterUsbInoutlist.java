package com.example.thermoshaker.ui.adapter;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramInfo;
import com.github.mjdev.libaums.fs.UsbFile;

import java.util.ArrayList;
import java.util.List;

public class AdapterUsbInoutlist extends BaseQuickAdapter<UsbFile, BaseViewHolder> {
    private List<String> positionList = new ArrayList<>();

    public AdapterUsbInoutlist(int layoutResId) {
        super(layoutResId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, UsbFile usbFile) {

        LinearLayout view = baseViewHolder.getView(R.id.mll);
        TextView num = baseViewHolder.getView(R.id.item_inout_number);
        num.setText(baseViewHolder.getAdapterPosition()+1+"");

        TextView name = baseViewHolder.getView(R.id.item_inout_name);
        name.setText(usbFile.getName()+"");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!positionList.contains(String.valueOf(baseViewHolder.getAdapterPosition()))) {
                    positionList.add(String.valueOf(baseViewHolder.getAdapterPosition()));
                } else {
                    positionList.remove(String.valueOf(baseViewHolder.getAdapterPosition()));
                }
                notifyItemChanged(baseViewHolder.getAdapterPosition());
            }
        });
        if (positionList.contains(String.valueOf(baseViewHolder.getAdapterPosition()))) {
            num.setTextColor(Color.parseColor("#FFFFFF"));
            name.setTextColor(Color.parseColor("#FFFFFF"));
            view.setBackground(MyApplication.getInstance().getResources().getDrawable(R.drawable.shape_background_focus));
        } else {
            num.setTextColor(Color.parseColor("#000000"));
            name.setTextColor(Color.parseColor("#000000"));
            view.setBackground(MyApplication.getInstance().getResources().getDrawable(R.drawable.shape_background_no2));
        }

    }

    public void selectAll() {
        for (int i = 0; i < getData().size(); i++) {
            if (!positionList.contains(String.valueOf(i))) {
                positionList.add(String.valueOf(i));
            }
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedPositionList() {
        return positionList;
    }
    public void  clearSelectedPositionList() {
        positionList.clear();
        notifyDataSetChanged();
    }
}

