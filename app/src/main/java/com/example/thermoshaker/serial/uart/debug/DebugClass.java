package com.example.thermoshaker.serial.uart.debug;

import android.annotation.SuppressLint;

import com.example.thermoshaker.serial.uart.Crc16JiaoYanMa;


/**
 * @调试模式数据的父类
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午12:47:41
 */
@SuppressLint("DefaultLocale")
public class DebugClass implements DebugInterface {
	protected int size;
	protected byte[] data;

	protected void init() {
		data = new byte[size + 8];
		data[0] = (byte) 0xaa;
		data[1] = (byte) 0x3d;
		data[2] = (byte) 0xd5;
		data[3] = (byte) (size >> 8);
		data[4] = (byte) size;
	}

	@Override
	public boolean analysis(byte[] bin) {
		if (bin.length == size + 8 && bin[0] == data[0] && bin[2] == data[2] && bin[bin.length - 1] == (byte) 0x55) {
			short val = Crc16JiaoYanMa.CRC16Code(bin, 1, bin.length - 4);
			if (bin[bin.length - 3] == (byte) (val >> 8) && bin[bin.length - 2] == (byte) val) {
				data = bin;
				return true;
			}
		}
		return false;
	}

	@Override
	public byte[] output() {
		return data;
	}

	@Override
	public float getTrueTempBlock1() {

		return 0;
	}

	@Override
	public String getTrueTempBlock1Str() {

		return String.format("%.1f",getTrueTempBlock1());
	}

}
