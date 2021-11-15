package com.example.thermoshaker.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * lyf on 2016/06/27
 * 自定义TextView
 */
public class MyTableTextView extends androidx.appcompat.widget.AppCompatTextView {

	Paint paint = new Paint();

	public MyTableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int color = Color.parseColor("#008FD7");
		// 为边框设置颜色
		paint.setColor(color);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 画TextView的4个边
		canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
		canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, paint);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
	}
}