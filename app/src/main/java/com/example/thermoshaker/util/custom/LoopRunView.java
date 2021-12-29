package com.example.thermoshaker.util.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.thermoshaker.R;
import com.example.thermoshaker.util.dialog.CustomKeyEditDialog;

public class LoopRunView extends View {
    public int mwidth = 111;
    private Loop loopType = Loop.Center;
    Path path = new Path();
    Paint paint = new Paint();
    public enum Loop{
        Begin, Over, Center
    }
    public LoopRunView(Context context) {
        this(context, null, 0);
    }



    public LoopRunView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopRunView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(getResources().getColor(R.color.color_line_yuan));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2); // 线宽
        paint.setPathEffect(new CornerPathEffect(10.0f));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();

        switch (loopType){
            case Begin:
                //开始
                path.moveTo(mwidth, 0);
                path.lineTo(30, 0);
                path.lineTo(25, 15);
                path.lineTo(30, 25);
                path.lineTo(mwidth, 25);
                path.lineTo(mwidth, 25);
                break;
            case Over:
                //结束
                path.moveTo(0, 0);
                path.lineTo(80, 0);
                path.lineTo(85, 15);
                path.lineTo(80, 25);
                path.lineTo(0, 25);
                path.lineTo(0, 25);
                break;
            case Center:
                path.lineTo(mwidth, 0);
                path.lineTo(mwidth, 0);
                path.moveTo(0, 25);
                path.lineTo(mwidth, 25);
                path.lineTo(mwidth, 25);
                break;
        }

        canvas.drawPath(path,paint);

    }
    public void setStyle(Loop loop){
        loopType = loop;
        postInvalidate();
    }

}
