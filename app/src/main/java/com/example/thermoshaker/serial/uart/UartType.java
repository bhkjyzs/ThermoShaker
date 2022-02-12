package com.example.thermoshaker.serial.uart;

/**
 * @通信中部分指令的定义
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月24日 下午12:55:45
 */
public class UartType {
	// 运行命令
	public static final byte OT_RUN_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xa1, 0, 0, 0 };
	// 停止命令
	public static final byte OT_STOP_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xa2, 0, 0, 0 };
	// 暂停命令
	public static final byte OT_PAUSE_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xa3, 0, 0, 0 };
	// 继续命令
	public static final byte OT_RESUME_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xa4, 0, 0, 0 };
	// 按键提示音命令
	public static final byte OT_KEY_SOUND_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xc1, 0, 0, 0 };
	// IAP升级命令
	public static final byte OT_KEY_IAP_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xd1, 0, 0, 0 };
	// 运行调试模式命令
	public static final byte OT_DEBUG_RUN_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x51, 0, 0, 0 };
	// 停止调试模式命令
	public static final byte OT_DEBUG_STOP_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x52, 0, 0, 0 };
	// 模块1加热命令
	public static final byte OT_DB_TE1_HT_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x53, 0, 0, 0 };
	// 模块1制冷命令
	public static final byte OT_DB_TE1_CL_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x54, 0, 0, 0 };
	// 模块1关闭命令
	public static final byte OT_DB_TE1_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x55, 0, 0, 0 };
	// 模块2加热命令
	public static final byte OT_DB_TE2_HT_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x56, 0, 0, 0 };
	// 模块2制冷命令
	public static final byte OT_DB_TE2_CL_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x57, 0, 0, 0 };
	// 模块2关闭命令
	public static final byte OT_DB_TE2_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x58, 0, 0, 0 };
	// 模块3加热命令
	public static final byte OT_DB_TE3_HT_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x59, 0, 0, 0 };
	// 模块3制冷命令
	public static final byte OT_DB_TE3_CL_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x5a, 0, 0, 0 };
	// 模块3关闭命令
	public static final byte OT_DB_TE3_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x5b, 0, 0, 0 };
	// 模块4加热命令
	public static final byte OT_DB_TE4_HT_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x5c, 0, 0, 0 };
	// 模块4制冷命令
	public static final byte OT_DB_TE4_CL_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x5d, 0, 0, 0 };
	// 模块4关闭命令
	public static final byte OT_DB_TE4_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x5e, 0, 0, 0 };

	// 风扇开启命令
	public static final byte OT_DB_FAN_ON_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x64, 0, 0, 0 };
	// 风扇关闭命令
	public static final byte OT_DB_FAN_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x65, 0, 0, 0 };
	// 电机开启命令
	public static final byte OT_DB_MOTOR_ON_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x66, 0, 0, 0 };
	// 电机关闭命令
	public static final byte OT_DB_MOTOR_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x67, 0, 0, 0 };
	// 释放电机命令
	public static final byte OT_DB_BRAKE_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x6c, 0, 0, 0 };
	// 写入校准值命令
	public static final byte OT_DB_POS_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0x6d, 0, 0, 0 };
	// 热盖开启命令
	public static final byte OT_DB_LIDA_ON_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xe8, 0, 0, 0 };
	// 热盖关闭命令
	public static final byte OT_DB_LIDA_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xe9, 0, 0, 0 };
	// led开启命令
	public static final byte OT_DB_LED_ON_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xea, 0, 0, 0 };
	// led关闭命令
	public static final byte OT_DB_LED_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xeb, 0, 0, 0 };
	// 关闭所有命令
	public static final byte OT_DB_ALL_OFF_BYTE[] = { (byte) 0xaa, (byte) 0xab, (byte) 0xec, 0, 0, 0 };

	// 系统参数数据，开机启动查询1次
	public static final byte ASK_SYSTEM_BYTE[] = { (byte) 0xaa, (byte) 0x4e, (byte) 0xe1, 0, 0, 0 };

	// 调试模式数据,调试模式下每0.5s查询1次
	public static final byte ASK_TEMPDATA_BYTE[] = { (byte) 0xaa, (byte) 0x4e, (byte) 0xe2, 0, 0, 0 };

	// 文件运行数据，运行时1s查询1次，其他时刻按需查询。
	public static final byte ASK_RUNDATA_BYTE[] = { (byte) 0xaa, (byte) 0x4e, (byte) 0xe3, 0, 0, 0 };

	// 温度修正数据，进入温度修正界面查询1次
	public static final byte ASK_TEMPADJ_BYTE[] = { (byte) 0xaa, (byte) 0x4e, (byte) 0xe4, 0, 0, 0 };
}
