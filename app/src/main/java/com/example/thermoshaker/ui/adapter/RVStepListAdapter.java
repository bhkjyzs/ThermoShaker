package com.example.thermoshaker.ui.adapter;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.ui.run.RunActivity;
import com.example.thermoshaker.util.custom.LoopRunView;
import com.example.thermoshaker.util.custom.MultiWaveHeaderRun;

public class RVStepListAdapter extends BaseQuickAdapter<ProgramStep, BaseViewHolder> {

    private RunActivity runActivity;

    public RVStepListAdapter(int layoutResId,RunActivity runActivity) {
        super(layoutResId);
        this.runActivity = runActivity;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ProgramStep programStep) {
        /* 关联控件 */
        MultiWaveHeaderRun multiWaveHeader = (MultiWaveHeaderRun) baseViewHolder.getView(R.id.multiWaveHeader);
        LoopRunView mLoop = baseViewHolder.getView(R.id.mLoop);
        TextView textView_title = baseViewHolder.getView(R.id.textView_title);
        TextView textView_run_inc = baseViewHolder.getView(R.id.textView_run_inc);
        Button Button_temp = baseViewHolder.getView(R.id.Button_temp);
        Button Button_time = baseViewHolder.getView(R.id.Button_time);

        textView_title.setText(getContext().getString(R.string.step)+""+(baseViewHolder.getAdapterPosition()+1));
        textView_run_inc.setText(programStep.getTemperature()+"°C");
        multiWaveHeader.setScaleY(-1f);
        if(runActivity.programInfo.isLoopEnable()){
            if(baseViewHolder.getAdapterPosition()+1<runActivity.programInfo.getLoopStart()||baseViewHolder.getAdapterPosition()+1>runActivity.programInfo.getLoopEnd()){
                mLoop.setVisibility(View.GONE);

            }else {

            if(baseViewHolder.getAdapterPosition()+1==runActivity.programInfo.getLoopStart()){
                mLoop.setStyle(LoopRunView.Loop.Begin);
            }
            if(baseViewHolder.getAdapterPosition()+1==runActivity.programInfo.getLoopEnd()){
                mLoop.setStyle(LoopRunView.Loop.Over);
            }
            }

        }else {
            mLoop.setVisibility(View.GONE);
        }


        if (baseViewHolder.getAdapterPosition() > 0) {
            /* 取上一个步骤 */
            ProgramStep upStep = runActivity.programInfo.getStepList().get(baseViewHolder.getAdapterPosition() - 1);
//            if (programStep1.getfTempCycSel() == tempCycTYPE.LOOPitem)
//                multiWaveHeader.setTempVal(itemClass.getfSetTemp0(), itemClass.getfSetTemp0());
//            else
                multiWaveHeader.setTempVal(upStep.getTemperature(), programStep.getTemperature());
        } else
            multiWaveHeader.setTempVal(programStep.getTemperature(), programStep.getTemperature());

        float pos = multiWaveHeader.mhight - multiWaveHeader.HeightEnd;

        Button_temp.setText(programStep.getTemperature() + " ℃");
        Button_temp.setTag(baseViewHolder.getAdapterPosition());
        Button_temp.setTranslationY(pos - 50);
        /* 显示时间 */
        Button_time.setText(MyApplication.getInstance().dateFormat.format(programStep.getTime())+"");
        Button_time.setTag(baseViewHolder.getAdapterPosition());
        Button_time.setTranslationY(pos + 2);


    }
}