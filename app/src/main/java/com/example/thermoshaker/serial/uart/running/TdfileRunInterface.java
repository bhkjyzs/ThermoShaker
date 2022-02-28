package com.example.thermoshaker.serial.uart.running;


import java.util.Date;

/**
 * @文件运行数据的接口
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午10:28:48
 */
public interface TdfileRunInterface {

	public boolean analysis(byte[] bin);

	public byte[] output();

	public int getRunState();// 运行状态

	public String getRunStateStr();

	public int getRunStep();// 当前步骤的位置

	public int getALLRunStepNum();//总步骤

	public int getCURWaitTime();//当前等待时间

	public int getCURRemnantTime();//当前总剩余时间
	public String getCUREndTimeStr();//当前总剩余时间
	public int getRunCir();//剩余循环运行次数

	public int getARunFileDefect(); // 文件缺失

	public TdfileRunType.RunFileDefectEnum getARunFileDefectEnum();

	public int getRunCourse();

	public TdfileRunType.RunStateEnum getARunStateEnum();

	public float getBlockTemp1A(); // 模块后台显示温度

	public String getBlockTemp1AStr();


	public float getDispTemp1A(); // 模块显示温度

	public String getDispTemp1AStr();


	public float getLidModuleTemp(); // 热盖后台显示温度

	public String getLidModuleTempStr();


	public float getLidDispTemp(); // 热盖显示温度

	public String getLidDispTempStr();



	public int getSystemErrCode1();

	public int getSystemErrCode2();

	public int getSystemErrCode3();

	public int getSystemErrCode4();

	public boolean getSystemErrCode();

	public String getSystemErrCodeStr();

}
