/**
 * Copyright 2017 George Argyrakis
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 */

package com.example.thermoshaker.util.key;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.thermoshaker.MainActivity;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.util.BroadcastManager;


/**
 * A Floating and Draggable KeyboardView. Several EditText's can register for
 * it.based on the work of Maarten Pennings
 * (http://www.fampennings.nl/maarten/android/09keyboard/)
 *
 * @author George Argyrakis
 * @date 2017 January 31
 */

@SuppressLint("ClickableViewAccessibility")
public class FloatingKeyboard extends KeyboardView {
    private static final int MOVE_THRESHOLD = 0;
    private static final int TOP_PADDING_DP = 50;
    private static final String TAG = "FloatingKeyboard";
    public static boolean inEditMode = true;

    // private static final int HANDLE_COLOR = Color.parseColor("#AAD1D6D9");
    private static final int HANDLE_COLOR = Color.parseColor("#008FD7");
    // private static final int HANDLE_PRESSED_COLOR = Color.parseColor("#D1D6D9");
    private static final int HANDLE_PRESSED_COLOR = Color.parseColor("#008FD7");

    // 以将路径的转角变得圆滑
    private static final float HANDLE_ROUND_RADIOUS = 20.0f;
    private static final CornerPathEffect HANDLE_CORNER_EFFECT = new CornerPathEffect(HANDLE_ROUND_RADIOUS);

    private static Keyboard keyboardNum;
    private static Keyboard keyboardEng;
    private static Keyboard keyboardEngUpper;
    private static Keyboard keyboardSym;

    private static int topPaddingPx;
    private static int topPaddingPxGap;
    private static int width;
    private static Path mHandlePath;
    private static Paint mHandlePaint;
    private static boolean allignBottomCenter = true;
    private boolean isNoMove = false;
    private String action = "";
    private Window mWindow = null;

    public enum KEYBOARD {
        number(-11), symbols(-21), english(-31), english_up(-41);
        private final int value;

        KEYBOARD(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public FloatingKeyboard(Context context, AttributeSet attrs) {
        super(new ContextWrapperFix(context, inEditMode), attrs);

        /* 单元转换成等效像素 */
        topPaddingPx = (int) convertDpToPixel((float) TOP_PADDING_DP, context);
        topPaddingPxGap = (int) convertDpToPixel(1.0f, context);

        /* 键盘事件 */
        this.setOnKeyboardActionListener(mOnKeyboardActionListener);

        /* 隐藏标准键盘 */
//		MainActivity.getInstance().getWindow()
//				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.setOnTouchListener(mKeyboardOntTouchListener);
        this.setPadding(0, topPaddingPx,0, 0);

        mHandlePaint = new Paint();
        mHandlePaint.setColor(HANDLE_COLOR);
        mHandlePaint.setStyle(Paint.Style.FILL);
        mHandlePaint.setPathEffect(HANDLE_CORNER_EFFECT);

        mHandlePath = new Path();

        keyboardNum = new Keyboard(context, R.xml.gs_keyboard_number);
        keyboardEng = new Keyboard(context, R.xml.gs_keyboard_english);
        keyboardEngUpper = new Keyboard(context, R.xml.gs_keyboard_english_upper);
        keyboardSym = new Keyboard(context, R.xml.gs_keyboard_symbols_shift);

    }

    public void setFinishAction(String str) {
        action = str;
    }

    /* 是否底部中间对齐 */
    public static boolean isAllignBottomCenter() {
        return allignBottomCenter;
    }

    public static void setAllignBottomCenter(boolean allignBottomCenter) {
        FloatingKeyboard.allignBottomCenter = allignBottomCenter;
    }

    public void setNoMove(boolean bool) {
        isNoMove = bool;
    }

    public void setKeyboardInt(KEYBOARD val) {
        switch (val) {
            case number:
                setKeyboard(keyboardNum);

                break;
            case symbols:
                setKeyboard(keyboardSym);

                break;
            case english:
                setKeyboard(keyboardEng);
                break;
            case english_up:
                setKeyboard(keyboardEngUpper);

                break;
            default:
                break;
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isNoMove)
            return;
        if (isAllignBottomCenter()) {
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            setLayoutParams(relativeLayoutParams);
        }
    }

    private void drawHandle() {
        mHandlePath.rewind();
        mHandlePath.moveTo(0, topPaddingPx - topPaddingPxGap);
        mHandlePath.lineTo(0, 0);
        mHandlePath.lineTo(width- 5, 0);
        mHandlePath.lineTo(width- 5, topPaddingPx - topPaddingPxGap);
        // Draw this line twice to fix strange artifact in API21
        mHandlePath.lineTo(width- 5, topPaddingPx - topPaddingPxGap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getWidth();
        drawHandle();

        Paint paint = mHandlePaint;
        Path path = mHandlePath;
        canvas.drawPath(path, paint);
    }

    /**
     * Returns whether the FloatingKeyboardView is visible.
     */
    public boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the FloatingKeyboardView visible, and hide the system keyboard for view.
     */
    public void show(View v) {
        this.setVisibility(View.VISIBLE);
        this.setEnabled(true);

//		if (v != null)
//			((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE))
//					.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Make the FloatingKeyboardView invisible.
     */
    public void hide() {
        this.setVisibility(View.GONE);
        this.setEnabled(false);
    }

    /* 注册编辑框 */
    public void registerEditText(EditText resid) {

        EditText edittext = resid;

        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    show(v);
                } else {
                    hide();
                }
            }
        });

        /* 单击事件事件 */
        edittext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show(v);
            }
        });

        /* 覆盖双击全选菜单 */
        edittext.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

        });
    }

    /**
     * TouchListener to handle the drag of keyboard
     */
    private OnTouchListener mKeyboardOntTouchListener = new OnTouchListener() {
        float dx;
        float dy;
        int moveToY;
        int moveToX;
        int distY;
        int distX;
        Rect inScreenCoordinates;
        boolean handleTouched = false;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // Use ViewGroup.MarginLayoutParams so as to work inside any layout
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            boolean performClick = false;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (isNoMove)
                        break;
                    if (handleTouched) {
                        moveToY = (int) (event.getRawY() - dy);
                        moveToX = (int) (event.getRawX() - dx);
                        distY = moveToY - params.topMargin;
                        distX = moveToX - params.leftMargin;

                        if (Math.abs(distY) > MOVE_THRESHOLD || Math.abs(distX) > MOVE_THRESHOLD) {
                            // Ignore any distance before threshold reached
                            moveToY = moveToY - Integer.signum(distY) * Math.min(MOVE_THRESHOLD, Math.abs(distY));
                            moveToX = moveToX - Integer.signum(distX) * Math.min(MOVE_THRESHOLD, Math.abs(distX));

                            inScreenCoordinates = keepInScreen(moveToY, moveToX);
                            view.setY(inScreenCoordinates.top);
                            view.setX(inScreenCoordinates.left);
                        }
                        performClick = false;
                    } else {
                        performClick = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (handleTouched) {
                        // reset handle color
                        mHandlePaint.setColor(HANDLE_COLOR);
                        mHandlePaint.setStyle(Paint.Style.FILL);
                        invalidate();
                        performClick = false;
                    } else {
                        performClick = true;
                    }

                    break;

                case MotionEvent.ACTION_DOWN:
                    handleTouched = event.getY() <= getPaddingTop(); // Allow move only wher touch on top
                    // padding
                    dy = event.getRawY() - view.getY();
                    dx = event.getRawX() - view.getX();

                    // change handle color on tap
                    if (handleTouched) {
                        mHandlePaint.setColor(HANDLE_PRESSED_COLOR);
                        mHandlePaint.setStyle(Paint.Style.FILL);
                        invalidate();
                        performClick = false;
                    } else {
                        performClick = true;
                    }
                    break;
            }
            return !performClick;
        }

    };

    private void moveTo(int y, int x) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        params.topMargin = y;
        params.leftMargin = x;
        setLayoutParams(params);
    }

    /**
     * Position keyboard to specific point. Caution do not move it outside screen.
     *
     * @param x
     * @param y
     */
    public void positionTo(int x, int y) {
        moveTo(y, x);
    }

    /**
     * @param topMargin  of desired position
     * @param leftMargin of desired position
     * @return a Rect with corrected positions so the whole view to stay in screen
     */
    private Rect keepInScreen(int topMargin, int leftMargin) {
        int top = topMargin;
        int left = leftMargin;
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int rightCorrection = ((View) getParent()).getPaddingRight();
        int botomCorrection = ((View) getParent()).getPaddingBottom();
        int leftCorrection = ((View) getParent()).getPaddingLeft();
        int topCorrection = ((View) getParent()).getPaddingTop();

        Rect rootBounds = new Rect();
        ((View) getParent()).getHitRect(rootBounds);
        rootBounds.set(rootBounds.left + leftCorrection, rootBounds.top + topCorrection,
                rootBounds.right - rightCorrection, rootBounds.bottom - botomCorrection);

        if (top <= rootBounds.top)
            top = rootBounds.top;
        else if (top + height > rootBounds.bottom)
            top = rootBounds.bottom - height;

        if (left <= rootBounds.left)
            left = rootBounds.left;
        else if (left + width > rootBounds.right)
            left = rootBounds.right - width;

//      Log.e("x0:"+rootBounds.left+" y0:"+rootBounds.top+" Sx:"+rootBounds.right+" Sy:"+rootBounds.bottom, "INPUT:left:"+leftMargin+" top:"+topMargin+
//                    " OUTPUT:left:"+left+" top:"+top+" right:"+(left + getWidth())+" bottom:"+(top + getHeight()));
        return new Rect(left, top, left + width, top + height);
//        return new Rect(leftMargin, topMargin, leftMargin + width, topMargin + height);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device
     * density
     */
    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public void setfocusCurrent(Window v) {
        mWindow = v;
    }

    /**
     * The key (code) handler.
     */
    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {
        public final static int empty = -911;
        public final static int CodeGrab = -10;
        public final static int CodeDelete = -5;
        public final static int CodeCancel = -3;
        public final static int CodeClear = -2;

        public final static int CodePrev = 55000;
        public final static int CodeAllLeft = 55001;
        public final static int CodeLeft = 55002;
        public final static int CodeRight = 55003;
        public final static int CodeAllRight = 55004;
        public final static int CodeNext = 55005;

        public final static int CodeCellUp = 1001;
        public final static int CodeCellDown = 1002;
        public final static int CodeCellLeft = 1003;
        public final static int CodeCellRight = 1004;

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (mWindow == null)
                mWindow = MainActivity.getInstance().getWindow();
            View focusCurrent = mWindow.getCurrentFocus();
            if (focusCurrent == null || (focusCurrent.getClass() != EditText.class
                    && focusCurrent.getClass().getSuperclass() != EditText.class))
                return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            if (editable == null)
                return;
            int start = edittext.getSelectionStart();
            int end = edittext.getSelectionEnd();

            // Apply the key to the edittext
            if (primaryCode == CodeCancel) {
                hide();
                /* 完成后发出通知 */
                if (!action.equals(""))
//                MyApplication.getInstance().sendBroadcast(new Intent(action).putExtra("data", "[keyboard]"));
                BroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(action,"[keyboard]");

            } else if (primaryCode == empty) {
                return;
            } else if (primaryCode == KEYBOARD.number.getValue()) {
                setKeyboard(keyboardNum);
            } else if (primaryCode == KEYBOARD.symbols.getValue()) {
                setKeyboard(keyboardSym);
            } else if (primaryCode == KEYBOARD.english.getValue()) {
                setKeyboard(keyboardEng);
            } else if (primaryCode == KEYBOARD.english_up.getValue()) {
                setKeyboard(keyboardEngUpper);
            } else if (primaryCode == CodeDelete) {
                if (start != end) {
                    // delete selection
                    editable.delete(start, end);
                } else if (start > 0) {
                    // delete char
                    editable.delete(start - 1, start);
                }
            } else if (primaryCode == CodeClear) {
                editable.clear();
            } else if (primaryCode == CodeLeft) {
                if (start > 0)
                    edittext.setSelection(start - 1);
            } else if (primaryCode == CodeRight) {
                if (start < edittext.length())
                    edittext.setSelection(start + 1);
            } else if (primaryCode == CodeAllLeft) {
                edittext.setSelection(0);
            } else if (primaryCode == CodeAllRight) {
                edittext.setSelection(edittext.length());
            } else if (primaryCode == CodePrev) {
                View focusNew = edittext.focusSearch(View.FOCUS_BACKWARD);
                if (focusNew != null)
                    focusNew.requestFocus();
            } else if (primaryCode == CodeNext) {
                View focusNew = edittext.focusSearch(View.FOCUS_FORWARD);
                if (focusNew != null)
                    focusNew.requestFocus();
                else if (primaryCode == CodeCellUp || primaryCode == CodeCellDown || primaryCode == CodeCellLeft
                        || primaryCode == CodeCellRight) {

                } else if (primaryCode == CodeGrab) {

                }
            } else {
                // insert character
                if (start != end) {
                    editable.delete(start, end);
                }
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeUp() {
        }
    };

}
