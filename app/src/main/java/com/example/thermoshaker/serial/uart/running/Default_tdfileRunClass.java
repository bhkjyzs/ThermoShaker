package com.example.thermoshaker.serial.uart.running;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.util.Utils;

import java.util.Date;

/**
 * @Repure_BC的文件运行数据
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月30日 上午13:17:41
 */
public class Default_tdfileRunClass extends TdfileRunClass {
	public Default_tdfileRunClass() {
		super();
		size = 29;
		init();
	}

	/* 开始符和地址位 5/0-4 */
	/* 运行状态 1/5 */
	@Override
	public int getRunState() {
		return data[5] & 0xff;
	}

	/* 当前步骤 1/6 */
	@Override
	public int getRunStep() {
		return data[6] & 0xff;
	}

	/* 总步骤 1/7 */
	@Override
	public int getALLRunStepNum() {
		return data[7] & 0xff;

	}

	/* 当前等待时间 4/8-11 */
	@Override
	public int getCURWaitTime() {
		return Utils.byteArrayToInt4(data, 8);
	}

	/* 当前总剩余时间 4/12-15 */
	@Override
	public int getCURRemnantTime() {
		return Utils.byteArrayToInt4(data, 12);
	}
	/* 剩余运行循环次数 1/16 */
	@Override
	public int getRunCir() {
		return data[16] & 0xff;
	}

	/* 17字节暂不取 */




	/* 模块后台显示温度 2/18-19 */
	@Override
	public float getBlockTemp1A() {
		int val = Utils.byteArrayToInt(data, 18);
		return decimalToFloat(val / 100f);
	}

	/* 模块显示温度 2/20-21 */
	@Override
	public float getDispTemp1A() {
		int val = Utils.byteArrayToInt(data, 20);
		return decimalToFloat(val / 100f);
	}
	/* 热盖后台显示温度 2/22-23 */
	@Override
	public float getLidModuleTemp() {
		int val = Utils.byteArrayToInt(data, 22);
		return decimalToFloat(val / 100f);
	}

	/* 热盖显示温度 2/24-25 */
	@Override
	public float getLidDispTemp() {
		int val = Utils.byteArrayToInt(data, 24);
		return decimalToFloat(val / 100f);
	}

	@Override
	public TdfileRunType.RunFileDefectEnum getARunFileDefectEnum() {
		return super.getARunFileDefectEnum();

	}

	@Override
	public int getRunCourse() {
		return data[26] & 0xFF;
	}

	/* 下位机文件是否丢失 1/51 */
	@Override
	public int getARunFileDefect() {
		return data[27] & 0xFF;
	}




	/* 环境温度参数 2/238-239 */

	/* 系统错误代码 4/240-243 */
	@Override
	public int getSystemErrCode1() {
		return data[30] & 0xff;
	}

	@Override
	public int getSystemErrCode2() {
		return data[31] & 0xff;
	}

	@Override
	public int getSystemErrCode3() {
		return data[32] & 0xff;
	}

	@Override
	public int getSystemErrCode4() {
		return data[33] & 0xff;
	}

	/* 校验码和结束符 3/34-36 */

	@Override
	protected String getErrCodeStr(int val) {
		MyApplication app = MyApplication.getInstance();
		switch (val) {
		case 0x00:
			return "";
		case 0x01:
			return app.getString(R.string.err_SEC_MODSOR1OPEN);
		case 0x02:
			return app.getString(R.string.err_SEC_MODSOR1SHORT);
		case 0x03:
			return app.getString(R.string.err_SEC_MODSOR2OPEN);
		case 0x04:
			return app.getString(R.string.err_SEC_MODSOR2SHORT);
		case 0x05:
			return app.getString(R.string.err_SEC_MODSOR3OPEN);
		case 0x06:
			return app.getString(R.string.err_SEC_MODSOR3SHORT);
		case 0x07:
			return app.getString(R.string.err_SEC_MODSOR4OPEN);
		case 0x08:
			return app.getString(R.string.err_SEC_MODSOR4SHORT);
		case 0x09:
			return app.getString(R.string.err_SEC_MODSOR5OPEN);
		case 0x0A:
			return app.getString(R.string.err_SEC_MODSOR5SHORT);
		case 0x0B:
			return app.getString(R.string.err_SEC_MODSOR6OPEN);
		case 0x0C:
			return app.getString(R.string.err_SEC_MODSOR6SHORT);
		case 0x0D:
			return app.getString(R.string.err_SEC_LIDSORAERROR);
		case 0x0E:
			return app.getString(R.string.err_SEC_LIDSORBERROR);
		case 0x0F:
			return app.getString(R.string.err_SEC_LIDSORCERROR);
		case 0x10:
			return app.getString(R.string.err_SEC_RADSORAERROE);
		case 0x11:
			return app.getString(R.string.err_SEC_RADSORBERROE);
		case 0x12:
			return app.getString(R.string.err_SEC_RADSORCERROE);
		case 0x13:
			return app.getString(R.string.err_SEC_MOD1TOOHEAT);
		case 0x14:
			return app.getString(R.string.err_SEC_MOD1TOOCOOL);
		case 0x15:
			return app.getString(R.string.err_SEC_MOD2TOOHEAT);
		case 0x16:
			return app.getString(R.string.err_SEC_MOD2TOOCOOL);
		case 0x17:
			return app.getString(R.string.err_SEC_MOD3TOOHEAT);
		case 0x18:
			return app.getString(R.string.err_SEC_MOD3TOOCOOL);
		case 0x19:
			return app.getString(R.string.err_SEC_MOD4TOOHEAT);
		case 0x1A:
			return app.getString(R.string.err_SEC_MOD4TOOCOOL);
		case 0x1B:
			return app.getString(R.string.err_SEC_MOD5TOOHEAT);
		case 0x1C:
			return app.getString(R.string.err_SEC_MOD5TOOCOOL);
		case 0x1D:
			return app.getString(R.string.err_SEC_MOD6TOOHEAT);
		case 0x1E:
			return app.getString(R.string.err_SEC_MOD6TOOCOOL);
		case 0x1F:
			return app.getString(R.string.err_SEC_LIDATOOHEAT);
		case 0x20:
			return app.getString(R.string.err_SEC_LIDATOOCOOL);
		case 0x21:
			return app.getString(R.string.err_SEC_LIDBTOOHEAT);
		case 0x22:
			return app.getString(R.string.err_SEC_LIDBTOOCOOL);
		case 0x23:
			return app.getString(R.string.err_SEC_LIDCTOOHEAT);
		case 0x24:
			return app.getString(R.string.err_SEC_LIDCTOOCOOL);
		case 0x25:
			return app.getString(R.string.err_SEC_RADATOOHEAT);
		case 0x26:
			return app.getString(R.string.err_SEC_RADATOOCOOL);
		case 0x27:
			return app.getString(R.string.err_SEC_RADBTOOHEAT);
		case 0x28:
			return app.getString(R.string.err_SEC_RADBTOOCOOL);
		case 0x29:
			return app.getString(R.string.err_SEC_RADCTOOHEAT);
		case 0x2A:
			return app.getString(R.string.err_SEC_RADCTOOCOOL);
		case 0x2B:
			return app.getString(R.string.err_SEC_MOD1HEATERR);
		case 0x2C:
			return app.getString(R.string.err_SEC_MOD1COOLERR);
		case 0x2D:
			return app.getString(R.string.err_SEC_MOD2HEATERR);
		case 0x2E:
			return app.getString(R.string.err_SEC_MOD2COOLERR);
		case 0x2F:
			return app.getString(R.string.err_SEC_MOD3HEATERR);
		case 0x30:
			return app.getString(R.string.err_SEC_MOD3COOLERR);
		case 0x31:
			return app.getString(R.string.err_SEC_MOD4HEATERR);
		case 0x32:
			return app.getString(R.string.err_SEC_MOD4COOLERR);
		case 0x33:
			return app.getString(R.string.err_SEC_MOD5HEATERR);
		case 0x34:
			return app.getString(R.string.err_SEC_MOD5COOLERR);
		case 0x35:
			return app.getString(R.string.err_SEC_MOD6HEATERR);
		case 0x36:
			return app.getString(R.string.err_SEC_MOD6COOLERR);
		case 0x37:
			return app.getString(R.string.err_SEC_LIDAHEATERR);
		case 0x38:
			return app.getString(R.string.err_SEC_LIDACTRLERR);
		case 0x39:
			return app.getString(R.string.err_SEC_LIDBHEATERR);
		case 0x3A:
			return app.getString(R.string.err_SEC_LIDBCTRLERR);
		case 0x3B:
			return app.getString(R.string.err_SEC_LIDCHEATERR);
		case 0x3C:
			return app.getString(R.string.err_SEC_LIDCCTRLERR);
		case 0x3D:
			return app.getString(R.string.err_SEC_CONTROLTOOHEAT);
		case 0x3E:
			return app.getString(R.string.err_SEC_INSIDESORERROR);
		case 0x3F:
			return app.getString(R.string.err_SEC_MODEMPTY);
		case 0x40:
			return app.getString(R.string.err_SEC_LIDSORAOPEN);
		case 0x41:
			return app.getString(R.string.err_SEC_LIDSORASHORT);
		case 0x42:
			return app.getString(R.string.err_SEC_LIDSORBOPEN);
		case 0x43:
			return app.getString(R.string.err_SEC_LIDSORBSHORT);
		case 0x44:
			return app.getString(R.string.err_SEC_LIDSORCOPEN);
		case 0x45:
			return app.getString(R.string.err_SEC_LIDSORCSHORT);
		default:
			return app.getString(R.string.err_SEC_UNKNOWN);
		}
	}

}
