package com.example.thermoshaker.util.wifi.wifiLibrary;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;

import java.net.Inet4Address;
import java.util.List;

/**
 * 连接管理工具类
 */
public class WifiAdmin {

	// 管理器
	public WifiManager mWifiManager;

	// 扫描结果
	public List<ScanResult> mWifiList;

	// 连接信息
	public WifiInfo mWifiInfo;

	// 配置列表
	public List<WifiConfiguration> mWifiConfigList;

	public WifiAdmin(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 扫描到的周围热点信息
		mWifiList = mWifiManager.getScanResults();
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
		// 取得mWifiConfigList对象
		mWifiConfigList = mWifiManager.getConfiguredNetworks();
	}

	public String getIp() {
		return ipIntToString(mWifiInfo.getIpAddress());
	}

	// 打开wifi
	public boolean openWifi() {
		boolean bRet = true;
		if (!mWifiManager.isWifiEnabled()) {
			bRet = mWifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	// 关闭wifi
	public boolean closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			return mWifiManager.setWifiEnabled(false);
		}
		return false;
	}

	public void startScan() {
		mWifiManager.startScan();
		mWifiList = mWifiManager.getScanResults();
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	// 添加一个网络并连接
	public boolean addNetWork(WifiConfiguration configuration) {
		mWifiManager.disableNetwork(mWifiInfo.getNetworkId());
		boolean bool = mWifiManager.enableNetwork(mWifiManager.addNetwork(configuration), true);
		if (bool) {
			// 取得WifiInfo对象
			mWifiInfo = mWifiManager.getConnectionInfo();
			// 取得mWifiConfigList对象
			mWifiConfigList = mWifiManager.getConfiguredNetworks();
		}
		return bool;
	}

	// 断开网络
	public void disConnectionWifi() {
		disConnectionWifi(mWifiInfo.getNetworkId());
	}

	public boolean disConnectionWifi(int netId) {
		boolean bool = mWifiManager.disableNetwork(netId);
		if (bool) {
			// 取得WifiInfo对象
			mWifiInfo = mWifiManager.getConnectionInfo();
			// 取得mWifiConfigList对象
			mWifiConfigList = mWifiManager.getConfiguredNetworks();
		}
		return bool;
	}

	/**
	 * Function: 提供一个外部接口，传入要连接的无线网 <br>
	 */
	public boolean connect(String SSID, String Password, WifiCipherType Type) {

		WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
		if (wifiConfig == null) {
			return false;
		}

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		return addNetWork(wifiConfig);
	}

	// 查看以前是否也配置过这个网络
	public WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID != null && existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			config.hiddenSSID = true;
			// config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			// config.wepTxKeyIndex = 0;
		} else if (Type == WifiCipherType.WIFICIPHER_WEP) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (Type == WifiCipherType.WIFICIPHER_WPA) {

			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	/**
	 * Function:判断扫描结果是否连接上<br>
	 */
	public boolean isConnect(ScanResult result) {
		if (result == null) {
			return false;
		}
		String g2 = "\"" + result.SSID + "\"";
		if (mWifiInfo.getSSID() != null && mWifiInfo.getSSID().endsWith(g2)) {
			return true;
		}
		return false;
	}

	/**
	 * Function: 将int类型的IP转换成字符串形式的IP<br>
	 */
	public String ipIntToString(int ip) {

		try {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (0xff & ip);
			bytes[1] = (byte) ((0xff00 & ip) >> 8);
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);
			return Inet4Address.getByAddress(bytes).getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Function:信号强度转换为字符串
	 */
	public static String singlLevToStr(int level) {

		String resuString = "无信号";

		if (Math.abs(level) > 100) {
		} else if (Math.abs(level) > 80) {
			resuString = MyApplication.getInstance().getString(R.string.weak);
		} else if (Math.abs(level) > 70) {
			resuString = MyApplication.getInstance().getString(R.string.strong);
		} else if (Math.abs(level) > 60) {
			resuString = MyApplication.getInstance().getString(R.string.strong);
		} else if (Math.abs(level) > 50) {
			resuString = MyApplication.getInstance().getString(R.string.stronger);
		} else {
			resuString = MyApplication.getInstance().getString(R.string.strongest);
		}
		return resuString;
	}

	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

}
