package com.example.thermoshaker.serial.uart.adjust;

/**
 * @温度修正数据的接口
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午13:27:26
 */
public interface AdjustInterface {

	public boolean analysis(byte[] bin);

	public byte[] output();

	public int getGroup(); // 取一组的大小

	/* 开始符和地址位 5/0-4 */

//	95度修正值	Temp95Adj[4]  8/5-12
	public float getTemp95Adj(int num);

	public String getTemp95AdjStr(int num);

	public boolean setTemp95Adj(int num, float value);

//	72度修正值	Temp72Adj[4] 8/13-20
	public float getTemp72Adj(int num);

	public String getTemp72AdjStr(int num);

	public boolean setTemp72Adj(int num, float value);

//	55度修正值	Temp55Adj[4]  8/21-28
	public float getTemp55Adj(int num);

	public String getTemp55AdjStr(int num);

	public boolean setTemp55Adj(int num, float value);

//	31度修正值	Temp31Adj[4]  8/29-36
	public float getTemp31Adj(int num);

	public String getTemp31AdjStr(int num);

	public boolean setTemp31Adj(int num, float value);

//	04度修正值	Temp04Adj[4]  8/37-44
	public float getTemp04Adj(int num);

	public String getTemp04AdjStr(int num);

	public boolean setTemp04Adj(int num, float value);

}
