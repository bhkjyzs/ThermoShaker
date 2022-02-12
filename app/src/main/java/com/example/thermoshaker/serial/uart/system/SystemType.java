package com.example.thermoshaker.serial.uart.system;

/**
 * @系统参数数据的枚举
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午10:02:48
 */
public class SystemType {

	/* 热盖状态 */
	public enum LidState {
		NULL((byte) -1), LS_CLOSED((byte) 0x2a), LS_BOOT((byte) 0x2b), LS_NOWAIT((byte) 0x2c), LS_AUTO((byte) 0x2d);
		private final byte value;

		LidState(byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}
	}

	/* LED状态 */
	public enum LedState {
		NULL((byte) -1), CYCLE_BRIGHT((byte) 0x0), CONST_BRIGHT((byte) 0x3a);
		private final byte value;

		LedState(byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}
	}

	/* 声音设置 */
	public enum Sound {
		NULL((byte) -1), SS_KEY((byte) 0x01), SS_ALARM((byte) 0x02), SS_PROEND((byte) 0x04), SS_TEMPLAND((byte) 0x08);
		private final byte value;

		Sound(byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}
	}
}
