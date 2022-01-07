package com.example.thermoshaker.util.key;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;

import com.example.thermoshaker.R;
import com.example.thermoshaker.ui.file.FileActivity;
import com.example.thermoshaker.util.BroadcastManager;
import com.example.thermoshaker.util.dialog.base.CustomkeyDialog;

import java.util.List;

public class KeyBoardUtil {
    private static final String TAG = "KeyBoardUtil";

    private KeyboardView keyboardView;
    private Keyboard k1; //小写键盘
    private EditText editText;
    private CustomkeyDialog dialogInfo;
    private CustomkeyDialog.Builder Layout;
    private Context context;
    public boolean isUpper = false;// 是否大写
    public boolean isShift = false;// 是否切换

    @SuppressLint("ResourceAsColor")
    public KeyBoardUtil(Context context, KeyboardView view, CustomkeyDialog dialogInfo, EditText edit) {
        this.editText = edit;
        keyboardView = view;
        this.context = context;
        this.dialogInfo=dialogInfo;
        if (editText != null) {
            edit.selectAll();
        }
        k1 = new Keyboard(context, R.xml.keyboard);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(keyboardActionListener);
    }

    private KeyboardView.OnKeyboardActionListener keyboardActionListener=new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
            //Log.i(TAG,"***** "+primaryCode+" ******");
        }

        @Override
        public void onRelease(int primaryCode) {
            //Log.i(TAG,"**** onRelease ****");
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if(editText==null){
                Log.e(TAG,"**** EditText is not setten ****");
                return;
            }
            if(!editText.hasFocus()){
                return;
            }
            //Log.i(TAG,"**** onKey ****");
            Editable editable = editText.getText();
            editText.setSelection(editText.length());
            int start = editText.getSelectionStart();
            switch (primaryCode){
                case Keyboard.KEYCODE_DONE:
                    if(!editText.getText().toString().trim().equals("")){
                        BroadcastManager.getInstance(context).sendBroadcast(FileActivity.MSG,editText.getText().toString().trim()+"");
                        Log.d(TAG,"KEYCODE_DONEin"+editText.getText().toString().trim()+"");

                    }
//                    Layout.infoDone();
                    hideKeyboard();
                    Log.d(TAG,"KEYCODE_DONE"+editText.getText().toString().trim()+"");

                    break;
                case Keyboard.KEYCODE_DELETE:
                    if(editable!=null && editable.length()>0){
                        if(start>0){
                            editable.delete(start-1,start);
                        }
                    }
                    Log.d(TAG,"KEYCODE_DELETE");

                    break;
                case Keyboard.KEYCODE_MODE_CHANGE:
                    changeKey();
                    keyboardView.setKeyboard(k1);
                    Log.d(TAG,"KEYCODE_MODE_CHANGE");
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    hideKeyboard();
                    Log.d(TAG,"KEYCODE_SHIFT");

                    break;
                default:
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && isUpper) {
                        code = Character.toUpperCase(code);
                    }
                    //mLog.i(TAG, Character.toString(code));
                    editable.insert(start, Character.toString(code));
                    break;
            }
        }

        @Override
        public void onText(CharSequence text) {
            editText.setText(text);
        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    public void showKeyboard() {
        //Log.i(TAG,"KeyBoard is show");
        dialogInfo.show();
    }

    public void hideKeyboard() {
        //Log.i(TAG,"KeyBoard is hidden");
        dialogInfo.cancel();
    }

    public void setEditText(EditText editText){
        //Log.i(TAG,"KeyBoard editText is setted");
        this.editText=editText;
        this.editText.setTextIsSelectable(true);
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Key> keylist = k1.getKeys();
        if (isUpper) {//大写切换小写
            isUpper = false;
            for(Key key:keylist){
                if (key.label!=null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0]+32;
                }
            }
        } else {//小写切换大写
            isUpper = true;
            for(Key key:keylist){
                if (key.label!=null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0]-32;
                }
            }
        }
    }

    private boolean isword(String str){
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase())>-1) {
            return true;
        }
        return false;
    }
}
