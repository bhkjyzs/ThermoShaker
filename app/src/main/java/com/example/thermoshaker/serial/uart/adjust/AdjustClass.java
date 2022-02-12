package com.example.thermoshaker.serial.uart.adjust;

import android.annotation.SuppressLint;

import com.example.thermoshaker.serial.uart.Crc16JiaoYanMa;


/**
 * @温度修正数据的父类
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午14:23:26
 */
@SuppressLint("DefaultLocale")
public class AdjustClass implements AdjustInterface {
	protected int size;
	protected byte[] data;
	protected int group;

	protected final float minTempAdj = -9.99f;
	protected final float maxTempAdj = 9.99f;

	protected void init() {
		data = new byte[size + 8];
		data[0] = (byte) 0xaa;
		data[1] = (byte) 0x3d;
		data[2] = (byte) 0xd3;
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
	public float getTemp95Adj(int num) {

		return 0;
	}

	@Override
	public String getTemp95AdjStr(int num) {

		return String.format("%.2f",getTemp95Adj(num));
	}

	@Override
	public boolean setTemp95Adj(int num, float value) {

		return false;
	}

	@Override
	public float getTemp72Adj(int num) {

		return 0;
	}

	@Override
	public String getTemp72AdjStr(int num) {

		return String.format("%.2f",getTemp72Adj(num));
	}

	@Override
	public boolean setTemp72Adj(int num, float value) {

		return false;
	}

	@Override
	public float getTemp55Adj(int num) {

		return 0;
	}

	@Override
	public String getTemp55AdjStr(int num) {

		return String.format("%.2f",getTemp55Adj(num));
	}

	@Override
	public boolean setTemp55Adj(int num, float value) {

		return false;
	}

	@Override
	public float getTemp31Adj(int num) {

		return 0;
	}

	@Override
	public String getTemp31AdjStr(int num) {

		return String.format("%.2f",getTemp31Adj(num));
	}

	@Override
	public boolean setTemp31Adj(int num, float value) {

		return false;
	}

	@Override
	public float getTemp04Adj(int num) {

		return 0;
	}

	@Override
	public String getTemp04AdjStr(int num) {

		return String.format("%.2f",getTemp04Adj(num));
	}

	@Override
	public boolean setTemp04Adj(int num, float value) {

		return false;
	}


	@Override
	public int getGroup() {
		return group;
	}
}
