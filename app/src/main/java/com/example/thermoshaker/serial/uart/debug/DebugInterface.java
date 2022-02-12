package com.example.thermoshaker.serial.uart.debug;


/**
 * @调试模式数据的接口
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午12:47:41
 */
public interface DebugInterface {

	public boolean analysis(byte[] bin);

	public byte[] output();

	public float getTrueTempBlock1();

	public String getTrueTempBlock1Str();


}
