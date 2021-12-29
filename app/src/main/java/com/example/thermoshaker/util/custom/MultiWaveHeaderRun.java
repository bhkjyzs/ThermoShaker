package com.example.thermoshaker.util.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.example.thermoshaker.R;
import com.example.thermoshaker.util.WindowUtil;

import java.lang.ref.WeakReference;

/**
 * 多重水波纹 Created by SCWANG on 2017/12/11.
 */
@SuppressWarnings("unused")
public class MultiWaveHeaderRun extends View {

	private static final String TAG = "MultiWaveHeaderRun";
	private Path path = new Path(); // 路径
	private Path StrokePath = new Path();
	private Path StrokeUpDownPath = new Path();

	private Paint Paint = new Paint();
	private Paint StrokePaint = new Paint();
	private Paint StrokeUpDownPaint = new Paint();

	// private Matrix mMatrix = new Matrix();

	public int mhight =  WindowUtil.getHeight() * 3 / 4;
	public int mwidth = 111;

	private boolean isStraight = false;
	private boolean isRun = false;

//	private BigDecimal mHight = new BigDecimal("562");
//	private BigDecimal height0 = mHight.multiply(new BigDecimal("0.3"));
//	private BigDecimal height100 = mHight.multiply(new BigDecimal("0.9"));
//	private BigDecimal heightVal = height100.subtract(height0).divide(new BigDecimal("100"));

	private float height0 = mhight * 0.3f;
	private float height100 = mhight * 0.8f;
	private float heightVal = (height100 - height0) / 105f;
	private changeHandler changeHandler;
	private float HeightStart;
	public float HeightEnd;

	private int mStartColor; // 开始颜色
	private int mCloseColor; // 结束颜色
	private int mCloseColorSelected; // 选中时结束的颜色
	private int mCloseColorNoSelected; // 选中时结束的颜色
	private int mGradientAngle; // 颜色角度
	private float mColorAlpha; // 颜色透明度

	private float startVal; // 开始值
	private float endVal;// 结束值
	private boolean isChange = false;
	private static final class  changeHandler extends Handler{
		private WeakReference<MultiWaveHeaderRun> clockViewWeakReference;

		private  changeHandler(MultiWaveHeaderRun clockView) {
			clockViewWeakReference = new WeakReference<>(clockView);
		}
		@Override
		public void handleMessage(@NonNull Message msg) {
			super.handleMessage(msg);
			MultiWaveHeaderRun view = clockViewWeakReference.get();
			if(view!=null){
			if(view.isChange){
				if(msg.what==1){
					Log.d("HANDLER",1+"handler");
					view.twinkleLine();
					view.invalidate();
					sendEmptyMessageDelayed(2,1000);
				}else if(msg.what==2){
					Log.d("HANDLER",2+"handler");
					view.StrokePaint.setColor(view.getResources().getColor(R.color.color_line_yuan));
					view.StrokeUpDownPaint.setColor(view.getResources().getColor(R.color.color_line_yuan));
					view.invalidate();
					sendEmptyMessageDelayed(1,1000);

				}

			}
			}
		}
	}

	public MultiWaveHeaderRun(Context context) {
		this(context, null, 0);
	}

	public MultiWaveHeaderRun(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}


	public MultiWaveHeaderRun(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		changeHandler = new changeHandler(this);
		Paint.setAntiAlias(true);
		Paint.setPathEffect(new CornerPathEffect(2.0f));

		StrokePaint.setColor(getResources().getColor(R.color.color_line_yuan));
		StrokePaint.setAntiAlias(true);
		StrokePaint.setStyle(Style.STROKE);
		StrokePaint.setStrokeWidth(2); // 线宽
		StrokePaint.setPathEffect(new CornerPathEffect(10.0f));


		StrokeUpDownPaint.setColor(getResources().getColor(R.color.color_line_green));
		StrokeUpDownPaint.setAntiAlias(true);
		StrokeUpDownPaint.setStyle(Style.STROKE);
		StrokeUpDownPaint.setStrokeWidth(2); // 线宽
		StrokeUpDownPaint.setPathEffect(new CornerPathEffect(10.0f));



		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultiWaveHeader);
		mStartColor = ta.getColor(R.styleable.MultiWaveHeader_mwhStartColor, 0xFF131525);
		mCloseColor = ta.getColor(R.styleable.MultiWaveHeader_mwhCloseColor, 0xFF251A8C);
		mCloseColorNoSelected = mCloseColor;
		mCloseColorSelected = ta.getColor(R.styleable.MultiWaveHeader_mwhCloseColorSelected, 0xFF00c5ff);
		mColorAlpha = ta.getFloat(R.styleable.MultiWaveHeader_mwhColorAlpha, 1f);
		mGradientAngle = ta.getInt(R.styleable.MultiWaveHeader_mwhGradientAngle, 90);
		ta.recycle();
	}

	public void selected(boolean is) {
		Log.d(TAG,"执行了selected");
		mCloseColor = mCloseColorSelected;
		isChange = true;
		isRun = is;
		changeHandler.sendEmptyMessageDelayed(1,1000);

		twinkleLine();

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.d("HANDLER","onDetachedFromWindow");
		changeHandler.removeMessages(1);
		changeHandler.removeMessages(2);
		changeHandler.removeCallbacksAndMessages(null);

	}

	@Override
	public void destroyDrawingCache() {
		super.destroyDrawingCache();


	}

	public void twinkleLine(){

		if(HeightStart!=HeightEnd){
			if(isRun){
				StrokePaint.setColor(getResources().getColor(R.color.color_line_straight));
				StrokeUpDownPaint.setColor(getResources().getColor(R.color.color_line_yuan));
			}else {

				StrokePaint.setColor(getResources().getColor(R.color.color_line_yuan));
				if(HeightStart<HeightEnd){
					StrokeUpDownPaint.setColor(getResources().getColor(R.color.color_line_red));

				}else {
					StrokeUpDownPaint.setColor(getResources().getColor(R.color.color_line_green));
				}
			}


		}else {
			if(isRun){


				StrokePaint.setColor(getResources().getColor(R.color.color_line_straight));
				StrokeUpDownPaint.setColor(getResources().getColor(R.color.color_line_yuan));
			}else {

				StrokePaint.setColor(getResources().getColor(R.color.color_line_yuan));
				StrokeUpDownPaint.setColor(getResources().getColor(R.color.color_line_yuan));
			}


		}


	}

	public void noSelected() {
		mCloseColor = mCloseColorNoSelected;
			isChange = false;

			StrokePaint.setColor(getResources().getColor(R.color.color_line_yuan));
			StrokeUpDownPaint.setColor(getResources().getColor(R.color.color_line_yuan));


	}

	public boolean isColorSelected() {
		if (mCloseColor == mCloseColorSelected) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		View thisView = this;
		int h = thisView.getHeight();
		int w = thisView.getWidth();
		
		path.reset();
		StrokePath.reset();
		StrokeUpDownPath.reset();
		updateLinearGradient(w, h);
		updateWavePath();

		/* mltWave中有几条,就画几条 */
		// mMatrix.reset();
		// canvas.save();
		// 像素点相对画布向右offsetX,向下移动200sp
		// mMatrix.setTranslate(tempLine.offsetX, (1 - mProgress) * height);
		// 画布平移,向右和向下各移动了100px
		// canvas.translate(-tempLine.offsetX, -tempLine.offsetY - (1 - mProgress) *
		// height);
		// mPaint.getShader().setLocalMatrix(mMatrix);
		canvas.drawPath(path, Paint);
		canvas.drawPath(StrokePath, StrokePaint);
		canvas.drawPath(StrokeUpDownPath,StrokeUpDownPaint);
		// canvas.restore();

	}

	private void updateLinearGradient(int width, int height) {
		int startColor = ColorUtils.setAlphaComponent(mStartColor, (int) (mColorAlpha * 255));
		int closeColor = ColorUtils.setAlphaComponent(mCloseColor, (int) (mColorAlpha * 255));
		// noinspection UnnecessaryLocalVariable 不必要的局部变量
		double w = width;
		double h = height;
		// double h = height * mProgress;
		double r = Math.sqrt(w * w + h * h) / 2;
		double y = r * Math.sin(2 * Math.PI * mGradientAngle / 360);
		double x = r * Math.cos(2 * Math.PI * mGradientAngle / 360);
		/* 渐变 */
		Paint.setShader(new LinearGradient((int) (w / 2 - x), (int) (h / 2 - y), (int) (w / 2 + x), (int) (h / 2 + y),
				startColor, closeColor, Shader.TileMode.CLAMP));
	}

	private void updateWavePath() {
		/* 比例分布情况,上边10%,下边40%,取中间50% */
		/* 0度对应高度乘以0.4,100度对应高度乘以0.9 */

		path.moveTo(0, 0);
		path.lineTo(0, HeightStart); // 移动到上一层
		path.lineTo(5, HeightStart);
		path.lineTo(20, HeightEnd); // 移动到当前层
		path.lineTo(mwidth, HeightEnd);
		path.lineTo(mwidth, 0); // 移动到末尾
		path.close();
		StrokePath.moveTo(0, HeightStart);
		StrokePath.lineTo(5, HeightStart);
		StrokePath.lineTo(20, HeightEnd);
		StrokePath.lineTo(mwidth, HeightEnd);
		StrokePath.lineTo(mwidth, HeightEnd);
		Log.d(TAG,HeightStart+"    "+HeightEnd);

		if(HeightStart!=HeightEnd){
			StrokeUpDownPath.moveTo(0, HeightStart);
			StrokeUpDownPath.lineTo(5, HeightStart);
			StrokeUpDownPath.lineTo(20, HeightEnd);
			StrokeUpDownPath.lineTo(35, HeightEnd);

		}

	}

	public void setTempVal(float startVal, float endVal) {
		this.startVal = startVal;
		this.endVal = endVal;
		HeightStart = height0 + heightVal * startVal;
		HeightEnd = height0 + heightVal * endVal;
	}

	// 颜色角度
	public int getGradientAngle() {
		return mGradientAngle;
	}

	public void setGradientAngle(int angle) {
		this.mGradientAngle = angle;
	}

	// 开始颜色
	public int getStartColor() {
		return mStartColor;
	}

	public void setStartColor(int color) {
		this.mStartColor = color;
	}

	// 结束颜色
	public int getCloseColor() {
		return mCloseColor;
	}

	public void setCloseColor(int color) {
		this.mCloseColor = color;
	}

	// 颜色透明度
	public float getColorAlpha() {
		return mColorAlpha;
	}

	public void setColorAlpha(float alpha) {
		this.mColorAlpha = alpha;
	}

}
