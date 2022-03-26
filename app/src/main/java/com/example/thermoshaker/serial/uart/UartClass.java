package com.example.thermoshaker.serial.uart;

import android.util.Log;

import com.example.thermoshaker.serial.ByteUtil;

import java.io.Serializable;

/**
 * @每次通信时需要将该类发送给串口通信服务
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月24日 下午12:55:45
 */
public class UartClass implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "UartClass";
	private String address;
	private byte[] data;
	private int time;

	public UartClass(String address, byte[] data) {
		this.address = address;
		this.data = data;
		this.time = 1000;
		if (data != null && data.length > 3) {
			short crc16 = Crc16JiaoYanMa.CRC16Code(data, 1, data.length - 4);
			data[data.length - 3] = (byte) (crc16 >> 8);
			data[data.length - 2] = (byte) crc16;
			data[data.length - 1] = (byte) 0x55;
		}
		Log.d(TAG, ByteUtil.bytesToHexString(data)+"");
	}

	public UartClass(String address, byte[] data, boolean bool) {
		this.address = address;
		this.data = data;
		this.time = 1000;
		if (data != null && data.length > 3 && bool) {
			short crc16 = Crc16JiaoYanMa.CRC16Code(data, 1, data.length - 4);
			data[data.length - 3] = (byte) (crc16 >> 8);
			data[data.length - 2] = (byte) crc16;
			data[data.length - 1] = (byte) 0x55;
		}
		Log.d(TAG, ByteUtil.bytesToHexString(data)+"");

	}

	public UartClass(String address, byte[] data, boolean bool, int time) {
		this.address = address;
		this.data = data;
		this.time = time;
		if (data != null && data.length > 3 && bool) {
			short crc16 = Crc16JiaoYanMa.CRC16Code(data, 1, data.length - 4);
			data[data.length - 3] = (byte) (crc16 >> 8);
			data[data.length - 2] = (byte) crc16;
			data[data.length - 1] = (byte) 0x55;
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
		if (data != null && data.length > 3) {
			short crc16 = Crc16JiaoYanMa.CRC16Code(data, 1, data.length - 4);
			data[data.length - 3] = (byte) (crc16 >> 8);
			data[data.length - 2] = (byte) crc16;
			data[data.length - 1] = (byte) 0x55;
		}
	}

	public void setData(byte[] data, boolean bool) {
		this.data = data;
		if (data != null && data.length > 3 && bool) {
			short crc16 = Crc16JiaoYanMa.CRC16Code(data, 1, data.length - 4);
			data[data.length - 3] = (byte) (crc16 >> 8);
			data[data.length - 2] = (byte) crc16;
			data[data.length - 1] = (byte) 0x55;
		}
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
