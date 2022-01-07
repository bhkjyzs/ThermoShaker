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

public class SeniorDialog {

    ProgramStep programStep;
    Context context;
    boolean isSave = false;
    dismissProgramStepListener ProgramStepListener;
    Boolean isFull =true;
    int upPostion=0,downPostion=0,directionPostion=0;
    public SeniorDialog(ProgramStep programStep, Context context) {
        this.programStep = programStep;
        this.context = context;
        init();

    }

    public interface dismissProgramStepListener{
        void disStep(boolean isSave,ProgramStep programStep);
    }

    public dismissProgramStepListener getProgramStepListener() {
        return ProgramStepListener;
    }

    public void setProgramStepListener(dismissProgramStepListener programStepListener) {
        ProgramStepListener = programStepListener;
    }

    private void init() {
        ArrayList<View> viewList = new ArrayList<>();
        String[] title={context.getString(R.string.rate)+"",context.getString(R.string.mixedmode)+"",context.getString(R.string.blendstartpoint)+""};
        double[] upArray = {3.0,2.0,1.0,0.1};
        double[] downArray = {1.0,0.5,0.1};

        List<String> listUp = new ArrayList<>();
        listUp.add("3.0°C/min");
        listUp.add("2.0°C/min");
        listUp.add("1.0°C/min");
        listUp.add("0.1°C/min");

        List<String> listDown = new ArrayList<>();
        listDown.add("1.0°C/min");
        listDown.add("0.5°C/min");
        listDown.add("0.1°C/min");

        List<String> listDirection = new ArrayList<>();
        listDirection.add(context.getString(R.string.forwardmixing));
        listDirection.add(context.getString(R.string.rotarymixing));
        listDirection.add(context.getString(R.string.Forwardandreversemixing));



        CustomkeyDialog seniorDialog = new CustomkeyDialog.Builder(context)
                .view(R.layout.senior_step_layout)
                .style(R.style.CustomDialog)
                .build();
        seniorDialog.show();
        Button btnSure = seniorDialog.findViewById(R.id.btnSure);
        Button btnCancel = seniorDialog.findViewById(R.id.btnCancel);
        ViewPager vp_senior = seniorDialog.findViewById(R.id.vp_senior);
        SlidingTabLayout stb_Senior = seniorDialog.findViewById(R.id.stb_Senior);
        //添加view
        viewList.add(LayoutInflater.from(context).inflate(R.layout.senior_dialog_vp_layout,null,false));
        viewList.add(LayoutInflater.from(context).inflate(R.layout.senior_dialog_vp_layout_mix_module,null,false));
        viewList.add(LayoutInflater.from(context).inflate(R.layout.senior_dialog_vp_layout_stand_point,null,false));

        vp_senior.setAdapter(new MyAdapter(viewList));
        //view1
        RecyclerView rv_uplist = viewList.get(0).findViewById(R.id.rv_uplist);
        RecyclerView rv_downlist = viewList.get(0).findViewById(R.id.rv_downlist);
        rv_uplist.setLayoutManager(new GridLayoutManager(context,3));
        rv_downlist.setLayoutManager(new GridLayoutManager(context,3));
         RVListSeniorAdapter rvListSeniorAdapter = new  RVListSeniorAdapter(R.layout.senior_set_layout_list_item,true);
        rvListSeniorAdapter.setList(listUp);
        rv_uplist.setAdapter(rvListSeniorAdapter);
         RVListSeniorAdapter rvListSeniordownAdapter = new  RVListSeniorAdapter(R.layout.senior_set_layout_list_item,false);
        rvListSeniordownAdapter.setList(listDown);
        rv_downlist.setAdapter(rvListSeniordownAdapter);
        LinearLayout mll_full = viewList.get(0).findViewById(R.id.mll_full);
        LinearLayout mll_custom = viewList.get(0).findViewById(R.id.mll_custom);
        CheckBox cb_full = viewList.get(0).findViewById(R.id.cb_full);
        CheckBox cb_custom = viewList.get(0).findViewById(R.id.cb_custom);
        if(programStep.getUpSpeedStr().equals(context.getString(R.string.FullPower))&&programStep.getDownSpeedStr().equals(context.getString(R.string.FullPower))){
            cb_full.setChecked(true);
            cb_custom.setChecked(false);
            isFull = true;
            upPostion=0;
            downPostion=0;


        }else {
            cb_full.setChecked(false);
            cb_custom.setChecked(true);
            for (int i = 0; i < upArray.length; i++) {
                if(upArray[i]==programStep.getUpSpeed()){
                    upPostion=i;
                    rvListSeniorAdapter.notifyDataSetChanged();

                }
            }
            for (int i = 0; i < downArray.length; i++) {
                if(downArray[i]==programStep.getDownSpeed()){
                    downPostion=i;
                    rvListSeniordownAdapter.notifyDataSetChanged();

                }
            }
            isFull = false;
        }
        mll_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_full.setChecked(true);
                cb_custom.setChecked(false);
                isFull = true;
                upPostion=0;
                downPostion=0;
                programStep.setUpSpeed(upArray[upPostion]);
                programStep.setDownSpeed(downArray[downPostion]);
                rvListSeniorAdapter.notifyDataSetChanged();
                rvListSeniordownAdapter.notifyDataSetChanged();
            }
        });

        mll_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_full.setChecked(false);
                cb_custom.setChecked(true);
                isFull = false;
            }
        });
        rvListSeniorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if(!isFull){
                    upPostion=position;
                    rvListSeniorAdapter.notifyDataSetChanged();
                    programStep.setUpSpeed(upArray[position]);
                }
            }
        });
        rvListSeniordownAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if(!isFull){
                    downPostion=position;
                    rvListSeniordownAdapter.notifyDataSetChanged();
                    programStep.setDownSpeed(downArray[position]);
                }
            }
        });

        //view2
        RecyclerView rv_directionlist = viewList.get(1).findViewById(R.id.rv_directionlist);
        LinearLayout mll_continuity = viewList.get(1).findViewById(R.id.mll_continuity);
        LinearLayout mll_intermission = viewList.get(1).findViewById(R.id.mll_intermission);
        CheckBox cb_continuity = viewList.get(1).findViewById(R.id.cb_continuity);
        CheckBox cb_intermission = viewList.get(1).findViewById(R.id.cb_intermission);
        TextView tvConTime =viewList.get(1).findViewById(R.id.tvConTime);
        TextView tvintTime =viewList.get(1).findViewById(R.id.tvintTime);
        tvConTime.setText(MyApplication.getInstance().dateFormat.format(programStep.getContinued())+"");
        tvintTime.setText(MyApplication.getInstance().dateFormat.format(programStep.getIntermission())+"");
        rv_directionlist.setLayoutManager(new GridLayoutManager(context,2));
        RVListSeniorDicAdapter rvListSeniorAdapterDirection = new  RVListSeniorDicAdapter(R.layout.senior_set_layout_list_item);
        rvListSeniorAdapterDirection.setList(listDirection);
        rv_directionlist.setAdapter(rvListSeniorAdapterDirection);
        rvListSeniorAdapterDirection.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                directionPostion =position;
                rvListSeniorAdapterDirection.notifyDataSetChanged();
                switch (position){
                    case 0:
                        programStep.setDirection(MainType.DirectionType.Forward);
                        break;
                    case 1:
                        programStep.setDirection(MainType.DirectionType.Reversal);
                        break;
                    case 2:
                        programStep.setDirection(MainType.DirectionType.ForwardAndReverse);
                        cb_continuity.setChecked(false);
                        cb_intermission.setChecked(true);
                        break;
                }
            }
        });
        mll_continuity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(programStep.getDirection()!= MainType.DirectionType.ForwardAndReverse){
                    cb_continuity.setChecked(true);
                    cb_intermission.setChecked(false);
                    programStep.setMixingMode(0);
                    programStep.setIntermission(0);
                    tvintTime.setText(MyApplication.getInstance().dateFormat.format(programStep.getIntermission())+"");
                }
            }
        });
        mll_intermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_continuity.setChecked(false);
                cb_intermission.setChecked(true);
                programStep.setMixingMode(1);

            }
        });
        tvConTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(context);
                customKeyEditDialog.show();
                customKeyEditDialog.init(String.valueOf(MyApplication.getInstance().dateFormat.format(programStep.getContinued())),CustomKeyEditDialog.TYPE.Time,0);
                customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
                    @Override
                    public void onConfirm() {
                        try {
                            programStep.setContinued(MyApplication.getInstance().dateFormat.parse(customKeyEditDialog.getOutTime()).getTime());
                            tvConTime.setText(customKeyEditDialog.getOutTime()+"");

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        tvintTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(programStep.getMixingMode()==1){
                    CustomKeyEditDialog customKeyEditDialog = new CustomKeyEditDialog(context);
                    customKeyEditDialog.show();
                    customKeyEditDialog.init(String.valueOf(MyApplication.getInstance().dateFormat.format(programStep.getIntermission())),CustomKeyEditDialog.TYPE.Time,0);
                    customKeyEditDialog.setOnDialogLister(new CustomKeyEditDialog.onDialogLister() {
                        @Override
                        public void onConfirm() {
                            try {
                                programStep.setIntermission(MyApplication.getInstance().dateFormat.parse(customKeyEditDialog.getOutTime()).getTime());
                                tvintTime.setText(customKeyEditDialog.getOutTime()+"");

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });




        //view3
        LinearLayout mll_sync = viewList.get(2).findViewById(R.id.mll_sync);
        LinearLayout mll_hot = viewList.get(2).findViewById(R.id.mll_hot);
        CheckBox ck_sync = viewList.get(2).findViewById(R.id.ck_sync);
        CheckBox ck_hot = viewList.get(2).findViewById(R.id.ck_hot);
        if(programStep.getBlendStart()==0){
            ck_sync.setChecked(true);
            ck_hot.setChecked(false);
        }else {
            ck_sync.setChecked(false);
            ck_hot.setChecked(true);
        }
        mll_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ck_sync.setChecked(true);
                ck_hot.setChecked(false);
                programStep.setBlendStart(0);
            }
        });
        mll_hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ck_sync.setChecked(false);
                ck_hot.setChecked(true);
                programStep.setBlendStart(1);
            }
        });



        stb_Senior.setViewPager(vp_senior,title);




        seniorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(ProgramStepListener!=null){
                    ProgramStepListener.disStep(isSave,programStep);
                }

            }
        });

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSave = true;
                seniorDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSave = false;

                seniorDialog.dismiss();
            }
        });

    }


    class RVListSeniorAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        boolean isup;
        public RVListSeniorAdapter(int layoutResId,boolean isUp) {
            super(layoutResId);
            this.isup = isUp;
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
            baseViewHolder.setText(R.id.tv_content,s+"");
            CheckBox mCheckBox = baseViewHolder.getView(R.id.mCheckBox);
            if(isup){
                if(baseViewHolder.getAdapterPosition()==upPostion){
                    mCheckBox.setChecked(true);
                }else {
                    mCheckBox.setChecked(false);

                }
            }else {
                if(baseViewHolder.getAdapterPosition()==downPostion){
                    mCheckBox.setChecked(true);
                }else {
                    mCheckBox.setChecked(false);

                }
            }
        }
    }
    class RVListSeniorDicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public RVListSeniorDicAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
            baseViewHolder.setText(R.id.tv_content,s+"");
            CheckBox mCheckBox = baseViewHolder.getView(R.id.mCheckBox);
                if(baseViewHolder.getAdapterPosition()==directionPostion){
                    mCheckBox.setChecked(true);
                }else {
                    mCheckBox.setChecked(false);

                }
        }
    }

}
