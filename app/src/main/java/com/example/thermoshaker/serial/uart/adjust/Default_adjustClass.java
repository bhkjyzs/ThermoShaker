package com.example.thermoshaker.serial.uart.adjust;


import com.example.thermoshaker.util.Utils;

/**
 * @Repure_BC的温度修正数据
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午14:47:41
 */
public class Default_adjustClass extends AdjustClass implements AdjustInterface {

	public Default_adjustClass() {
		super();
		group = 1;
		size = 10;
		init();
	}

	/* 开始符和地址位 5/0-4 */

//	95度修正值	Temp95Adj[4]  16/5-6
	@Override
	public float getTemp95Adj(int num) {
		if (num >= group)
			return 0;
		int value;
		int pos = num * 2;
		value = Utils.byteArrayToInt(data, 5 + pos);
		return value / 100f;
	}

	@Override
	public boolean setTemp95Adj(int num, float value) {
		if (num >= group)
			return false;
		if (value >= minTempAdj && value <= maxTempAdj) {
			int val = Math.round(value * 100F);
			int pos = num * 2;
			Utils.intTobyteArray(val, data, 5 + pos);
			return true;
		}
		return false;
	}

//	72度修正值	Temp72Adj[4] 16/7-8
	@Override
	public float getTemp72Adj(int num) {
		if (num >= group)
			return 0;
		int value;
		int pos = num * 2;
		value = Utils.byteArrayToInt(data, 7 + pos);
		return value / 100f;
	}

	@Override
	public boolean setTemp72Adj(int num, float value) {
		if (num >= group)
			return false;
		if (value >= minTempAdj && value <= maxTempAdj) {
			int val = Math.round(value * 100F);
			int pos = num * 2;
			Utils.intTobyteArray(val, data, 7 + pos);
			return true;
		}
		return false;
	}

//	55度修正值	Temp55Adj[4]  16/9-10
	@Override
	public float getTemp55Adj(int num) {
		if (num >= group)
			return 0;
		int value;
		int pos = num * 2;
		value = Utils.byteArrayToInt(data, 9 + pos);
		return value / 100f;
	}

	@Override
	public boolean setTemp55Adj(int num, float value) {
		if (num >= group)
			return false;
		if (value >= minTempAdj && value <= maxTempAdj) {
			int val = Math.round(value * 100F);
			int pos = num * 2;
			Utils.intTobyteArray(val, data, 9 + pos);
			return true;
		}
		return false;
	}

//	31度修正值	Temp31Adj[4]  16/11-12
	@Override
	public float getTemp31Adj(int num) {
		if (num >= group)
			return 0;
		int value;
		int pos = num * 2;
		value = Utils.byteArrayToInt(data, 11 + pos);
		return value / 100f;
	}

	@Override
	public boolean setTemp31Adj(int num, float value) {
		if (num >= group)
			return false;
		if (value >= minTempAdj && value <= maxTempAdj) {
			int val = Math.round(value * 100F);
			int pos = num * 2;
			Utils.intTobyteArray(val, data, 11 + pos);
			return true;
		}
		return false;
	}

//	04度修正值	Temp04Adj[4]  16/13-14
	@Override
	public float getTemp04Adj(int num) {
		if (num >= group)
			return 0;
		int value;
		int pos = num * 2;
		value = Utils.byteArrayToInt(data, 13 + pos);
		return value / 100f;
	}

	@Override
	public boolean setTemp04Adj(int num, float value) {
		if (num >= group)
			return false;
		if (value >= minTempAdj && value <= maxTempAdj) {
			int val = Math.round(value * 100F);
			int pos = num * 2;
			Utils.intTobyteArray(val, data, 13 + pos);
			return true;
		}
		return false;
	}

	/* 校验码和结束符 3/15-17 */
}
