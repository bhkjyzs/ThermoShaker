package com.example.thermoshaker.util;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.thermoshaker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/11/15.
 *  自定义支付密码组件
 */

public class PayPassView extends RelativeLayout {
    private Activity mContext;//上下文
    private GridView mGridView; //支付键盘
    private String strPass="";//保存密码
    private TextView[]  mTvPass;//密码数字控件
    private List<Integer> listNumber;//1,2,3---0
    private View mPassLayout;//布局
    private boolean isRandom;

    /**
     * 按钮对外接口
     */
    public static interface OnPassInputClickListener {
        void onPassFinish(String password);
        void onPayClose();
    }
    private OnPassInputClickListener mPassInputClickListener;
    public void setPassInpntClickListener(OnPassInputClickListener listener) {
        mPassInputClickListener = listener;
    }

    public PayPassView(Context context) {
        super(context);
    }
    //在布局文件中使用的时候调用,多个样式文件
    public PayPassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //在布局文件中使用的时候调用
    public PayPassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (Activity) context;

        initView();//初始化
        this.addView(mPassLayout); //将子布局添加到父容器,才显示控件
    }

    /**
     * 初始化
     */
    private void initView() {
        mPassLayout = LayoutInflater.from(mContext).inflate( R.layout.view_paypass_layout, null);

        mTvPass     = new TextView[6];                                  //密码控件
        mTvPass[0]  = (TextView) mPassLayout.findViewById(R.id.tv_pass1);
        mTvPass[1]  = (TextView) mPassLayout.findViewById(R.id.tv_pass2);
        mTvPass[2]  = (TextView) mPassLayout.findViewById(R.id.tv_pass3);
        mTvPass[3]  = (TextView) mPassLayout.findViewById(R.id.tv_pass4);
        mTvPass[4]  = (TextView) mPassLayout.findViewById(R.id.tv_pass5);
        mTvPass[5]  = (TextView) mPassLayout.findViewById(R.id.tv_pass6);
        mGridView   = (GridView) mPassLayout.findViewById(R.id.gv_pass);




        //初始化数据
        initData();
    }

    /**
     * isRandom是否开启随机数
     */
    private void initData(){
        if(isRandom){
            listNumber = new ArrayList<>();
            listNumber.clear();
            for (int i = 0; i <= 10; i++) {
                listNumber.add(i);
            }
            //此方法是打乱顺序
            Collections.shuffle(listNumber);
            for(int i=0;i<=10;i++){
                if(listNumber.get(i)==10){
                    listNumber.remove(i);
                    listNumber.add(9,10);
                }
            }
            listNumber.add(R.drawable.ic_pay_del0);
        }else {
            listNumber = new ArrayList<>();
            listNumber.clear();
            for (int i = 1; i <= 9; i++) {
                listNumber.add(i);
            }
            listNumber.add(10);
            listNumber.add(0);
            listNumber.add(R.drawable.ic_pay_del0);
        }
        mGridView.setAdapter(adapter);
    }


    /**
     *   GridView的适配器
     */

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return listNumber.size();
        }
        @Override
        public Object getItem(int position) {
            return listNumber.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.view_paypass_gridview_item, null);
                holder = new ViewHolder();
                holder.btnNumber = (TextView) convertView.findViewById(R.id.btNumber);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //-------------设置数据----------------
            holder.btnNumber.setText(listNumber.get(position)+"");
            if (position == 9) {
                holder.btnNumber.setText("");
                holder.btnNumber.setBackgroundColor(mContext.getResources().getColor(R.color.graye3));
            }
            if (position == 11) {
                holder.btnNumber.setText("");
                holder.btnNumber.setBackgroundResource(listNumber.get(position));
            }
            //监听事件----------------------------
            if(position==11) {
                holder.btnNumber.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (position == 11) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    holder.btnNumber.setBackgroundResource(R.drawable.ic_pay_del1);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    holder.btnNumber.setBackgroundResource(R.drawable.ic_pay_del1);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    holder.btnNumber.setBackgroundResource(R.drawable.ic_pay_del0);
                                    break;
                            }
                        }
                        return false;
                    }
                });
            }
            holder.btnNumber.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < 11 &&position!=9) {//0-9按钮
                        if(strPass.length()==6){
                            return;
                        }
                        else {
                            strPass=strPass+listNumber.get(position);//得到当前数字并累加
                            mTvPass[strPass.length()-1].setText("*"); //设置界面*
                            //输入完成
                            if(strPass.length()==6){
                                mPassInputClickListener.onPassFinish(strPass);//请求服务器验证密码
                            }
                        }
                    }
                    else if(position == 11) {//删除
                        if(strPass.length()>0){
                            mTvPass[strPass.length()-1].setText("");//去掉界面*
                            strPass=strPass.substring(0,strPass.length()-1);//删除一位
                        }
                    }
                    if(position==9){//空按钮
                    }
                }
            });

            return convertView;
        }
    };
    static class ViewHolder {
        public TextView btnNumber;
    }

    /***
     * 设置随机数
     * @param israndom
     */
     public  PayPassView setRandomNumber(boolean israndom){
        isRandom=israndom;
        initData();
        adapter.notifyDataSetChanged();
        return this;
     }


    /**
     * 清楚所有密码TextView
     */
    public  PayPassView cleanAllTv() {
        strPass="";
        for(int i=0;i<6;i++){
            mTvPass[i].setText("");
        }
        return this;
    }
}
