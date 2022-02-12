package com.example.thermoshaker.serial.uart.debug;

import com.example.thermoshaker.util.Utils;

/**
 * @Repure_BC的调试模式数据
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午12:47:41
 */
public class Default_debugClass extends DebugClass implements DebugInterface {

	public Default_debugClass() {
		super();
		size = 2;
		init();
	}

//	模块实际温度1	TrueTempBlock1 2/5-6
	@Override
	public float getTrueTempBlock1() {
		int value;
		value = Utils.byteArrayToInt(data, 5);
		return value / 100f;
	}



	/* 校验码和结束符 3/7-9 */
}
