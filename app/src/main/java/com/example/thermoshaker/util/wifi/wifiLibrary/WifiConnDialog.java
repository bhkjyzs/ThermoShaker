package com.example.thermoshaker.util.wifi.wifiLibrary;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thermoshaker.MainActivity;
import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.ui.file.AddAndEditActivity;
import com.example.thermoshaker.ui.setting.SettingActivity;
import com.example.thermoshaker.util.dialog.base.CustomDialog;
import com.example.thermoshaker.util.key.FloatingKeyboard;


/**
 * Class Name: WifiConnDialog.java<br>
 * Function:Wifi连接对话框<br>
 * 
 */
public class WifiConnDialog {
	private ScanResult scanResult;
	public final static String MSG = WifiConnDialog.class.getName();

	private LinearLayout LinearLayout_conn;
	private TextView txtWifiName;
	private TextView txtSinglStrength;
	private TextView txtSecurityLevel;
	public EditText edtPassword;
	private CheckBox cbxShowPass;

	private boolean isConnected;

	private LinearLayout LinearLayout_connected;
	private TextView TextView_conn_status;
	private TextView TextView_signal_strength;
	private TextView TextView_security_level;
	private TextView TextView_ip_address;
	private Dialog customDialog;
	public WifiConnDialog(ScanResult scanResult, boolean isConnected,Dialog customDialog) {
		this.scanResult = scanResult;
		this.isConnected = isConnected;
		this.customDialog = customDialog;
		initView();
		/* 隐藏键盘 */
		customDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	}

	public void setip(String str) {
		TextView_ip_address.setText(str);
	}
	
	@SuppressLint("InflateParams")
	private void initView() {

		txtWifiName = (TextView) customDialog.findViewById(R.id.txt_wifi_name);

		LinearLayout_conn = (LinearLayout) customDialog.findViewById(R.id.LinearLayout_conn);

		LinearLayout_connected = (LinearLayout) customDialog.findViewById(R.id.LinearLayout_connected);
		FloatingKeyboard keyboardview = customDialog.findViewById(R.id.keyboardview);
		txtWifiName.setText(scanResult.SSID);
		if (isConnected == false) {
			LinearLayout_conn.setVisibility(View.VISIBLE);
			LinearLayout_connected.setVisibility(View.GONE);

			txtSinglStrength = (TextView) customDialog.findViewById(R.id.txt_signal_strength);
			txtSecurityLevel = (TextView) customDialog.findViewById(R.id.txt_security_level);
			edtPassword = (EditText) customDialog.findViewById(R.id.edt_password);
			cbxShowPass = (CheckBox) customDialog.findViewById(R.id.cbx_show_pass);
			keyboardview.setfocusCurrent(customDialog.getWindow());
			keyboardview.setKeyboardInt(FloatingKeyboard.KEYBOARD.number);
			keyboardview.setPreviewEnabled(false);
			keyboardview.setFinishAction(MSG);
			keyboardview.registerEditText(edtPassword);
			cbxShowPass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						// 文本正常显示
						edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						Editable etable = edtPassword.getText();
						Selection.setSelection(etable, etable.length());

					} else {
						// 文本以密码形式显示
						edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						// 下面两行代码实现: 输入框光标一直在输入文本后面
						Editable etable = edtPassword.getText();
						Selection.setSelection(etable, etable.length());

					}
				}
			});

			txtSinglStrength.setText(WifiAdmin.singlLevToStr(scanResult.level));
			txtSecurityLevel.setText(scanResult.capabilities);
		} else {
			LinearLayout_conn.setVisibility(View.GONE);
			LinearLayout_connected.setVisibility(View.VISIBLE);

			TextView_conn_status = (TextView) customDialog.findViewById(R.id.TextView_conn_status);
			TextView_signal_strength = (TextView) customDialog.findViewById(R.id.TextView_signal_strength);
			TextView_security_level = (TextView) customDialog.findViewById(R.id.TextView_security_level);
			TextView_ip_address = (TextView) customDialog.findViewById(R.id.TextView_ip_address);

			TextView_conn_status.setText(MyApplication.getInstance().getString(R.string.setting_wifi_ok));
			TextView_signal_strength.setText(WifiAdmin.singlLevToStr(scanResult.level));
			TextView_security_level.setText(scanResult.capabilities);
			// TextView_ip_address.setText(mWifiAdmin.ipIntToString(mWifiAdmin.getIpAddress()));
		}

	}

}
