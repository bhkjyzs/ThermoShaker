package com.example.thermoshaker.serial.uart.system;


/**
 * @系统参数数据的接口
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午10:02:48
 */
public interface SystemInterface {
	/* 解析 */
	public boolean analysis(byte[] bin);

	/* 输出 */
	public byte[] output();

//	// 模块类型
//	public MachineEnum getModuleType();
	// 运行界面类型
	public String getUiType();
	// led状态
	public void setLedState(SystemType.LedState key);

	// 声音设置
	public void setSoundSetting(byte key);
	
	// 设备编号
	public String getDeviceNum();

	public void setDeviceNum(String str);

	// 软件版本
	public String getSoftwareVersion();
	
	// 版本后缀
	public String getVerSuffix();

//	//热盖模式
//	public LidState getFLidMode();
//
//	public void setFLidMode(LidState lidMode);


}
