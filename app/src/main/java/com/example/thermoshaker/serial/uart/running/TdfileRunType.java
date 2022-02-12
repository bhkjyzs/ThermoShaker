package com.example.thermoshaker.serial.uart.running;

/**
 * @@文件运行数据的枚举
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午10:28:48
 */
public class TdfileRunType {
	// 运行状态
	//1：正在运行
	//2：没有在运行
	public enum RunStateEnum {
		 START(1), STOP(2);
		private final int value;

		RunStateEnum(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	// 文件状态
	public enum RunFileDefectEnum {
		NULL(-1), EMPTY(0), FULL(1);
		private final int value;

		RunFileDefectEnum(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	// 运行来源
	public enum RunClassEnum {
		NULL(""), CreateFragment("Create"), FileFragment("File"), IncubateFragment("Incubate"), EditFragment("Edit"),
		RunEditFragment("RunEdit"), NetWork("NetWork");
		private final String value;

		RunClassEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

}
