package com.example.thermoshaker.serial.uart.running;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.thermoshaker.R;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.serial.uart.Crc16JiaoYanMa;
import com.example.thermoshaker.serial.uart.UartClass;
import com.example.thermoshaker.serial.uart.UartServer;
import com.example.thermoshaker.serial.uart.UartType;
import com.example.thermoshaker.util.TimeUtils;
import com.licheedev.myutils.LogPlus;

import java.math.BigDecimal;

/**
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年7月3日 上午08:16:00
 * @@文件运行数据的父类
 */
@SuppressLint("DefaultLocale")
public class TdfileRunClass implements TdfileRunInterface {
    private static String TAG = TdfileRunClass.class.getSimpleName();
    private static String MSG = TdfileRunClass.class.getName(); // 用于接收消息

    private boolean isWork; // 是否正在工作
    private int errorDispTime;

    protected int size; // 数据大小
    protected byte[] data; // 接收和发送数组
//	protected HorseRaceLamp horseRaceA; // 跑马显示
//	protected HorseRaceLamp horseRaceB;
//	protected HorseRaceLamp horseRaceC;

    /**
     * @构造函数 LiJ
     */
    public TdfileRunClass() {
        isWork = false;
        errorDispTime = 0;

    }

    /**
     * @析构函数 LiJ
     */
    protected void finalizeRec() throws Throwable {

    }


    /**
     * @初始化 LiJ
     */
    protected void init() {
        data = new byte[size + 8];
        data[0] = (byte) 0xaa;
        data[1] = (byte) 0x3d;
        data[2] = (byte) 0xd4;
        data[3] = (byte) (size >> 8);
        data[4] = (byte) size;
    }

    /* 解析 */
    @Override
    public boolean analysis(byte[] bin) {
        try {
            if (bin.length == size + 8 && bin[0] == (byte) 0xAA && bin[2] == data[2]
                    && bin[bin.length - 1] == (byte) 0x55) {
                short val = Crc16JiaoYanMa.CRC16Code(bin, 1, bin.length - 4);
                if (bin[bin.length - 3] == (byte) (val >> 8) && bin[bin.length - 2] == (byte) val) {
                    data = bin;

                    return true;
                }
            }
        } catch (Exception e) {
            LogPlus.d(e.getMessage());

        }
        return false;
    }

    /* 输出 */
    @Override
    public byte[] output() {
        return data;
    }


    @Override
    public int getRunStep() {

        return 0;
    }

    @Override
    public int getALLRunStepNum() {
        return 0;
    }

    @Override
    public int getStepSurplusTime() {
        return 0;
    }

    @Override
    public String getStepSurplusStr() {
        int time = getStepSurplusTime();
        MyApplication app = MyApplication.getInstance();
        if (time == 0)
            return "∞";
        else

            return TimeUtils.secondToTime(time);

    }


    @Override
    public int getCURRemnantTime() {
        return 0;
    }

    @Override
    public String getCUREndTimeStr() {
        int time = getCURRemnantTime();
        MyApplication app = MyApplication.getInstance();
        if (time == 0)
            return "∞";
        else
            return String.valueOf(time / 60) + app.getString(R.string.bio_hh) + " " + String.valueOf(time % 60)
                    + app.getString(R.string.bio_mm);
    }

    @Override
    public int getRunCir() {
        return 0;
    }

    @Override
    public int getARunFileDefect() {

        return 0;
    }

    @Override
    public TdfileRunType.RunFileDefectEnum getARunFileDefectEnum() {
        switch (getARunFileDefect()) {
            case 0:
                return TdfileRunType.RunFileDefectEnum.EMPTY;
            case 1:
                return TdfileRunType.RunFileDefectEnum.FULL;
            default:
                return TdfileRunType.RunFileDefectEnum.NULL;
        }
    }

    @Override
    public int getRunCourse() {
        return 0;
    }

    @Override
    public int getRunState() {

        return 0;
    }

    @Override
    public String getRunStateStr() {
        MyApplication app = MyApplication.getInstance();
        switch (getRunState()) {
            case 0:
            	//没有运行
				return "没有运行";
            case 1:

                return app.getString(R.string.run_state_run);
            case 2:

                return app.getString(R.string.run_state_stop);
            default:
                return "";
        }
    }

    @Override
    public TdfileRunType.RunStateEnum getARunStateEnum() {
        switch (getRunState()) {
			case 0:
				return TdfileRunType.RunStateEnum.OVER;
            case 1:
                return TdfileRunType.RunStateEnum.START;
            case 2:
                return TdfileRunType.RunStateEnum.STOP;
            default:
                return TdfileRunType.RunStateEnum.NULL;
        }
    }

    @Override
    public float getBlockTemp1A() {

        return 0;
    }

    @Override
    public String getBlockTemp1AStr() {
        return String.format("%.1f", getBlockTemp1A());
    }


    @Override
    public float getLidDispTemp() {

        return 0;
    }

    @Override
    public String getLidDispTempStr() {
        return String.format("%.1f", getLidDispTemp());
    }


    @Override
    public float getDispTemp1A() {

        return 0;
    }

    @Override
    public String getDispTemp1AStr() {
        return String.format("%.1f", getDispTemp1A());
    }

    @Override
    public float getLidModuleTemp() {
        return 0;
    }

    @Override
    public String getLidModuleTempStr() {
        return String.format("%.1f", getLidModuleTemp());
    }


    @Override
    public int getSystemErrCode1() {

        return 0;
    }

    @Override
    public int getSystemErrCode2() {

        return 0;
    }

    @Override
    public int getSystemErrCode3() {

        return 0;
    }

    @Override
    public int getSystemErrCode4() {

        return 0;
    }

    @Override
    public boolean getSystemErrCode() {
        if (getSystemErrCode1() == 0 && getSystemErrCode2() == 0 && getSystemErrCode3() == 0
                && getSystemErrCode4() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public String getSystemErrCodeStr() {
        String retString = "";
        String ret = getErrCodeStr(getSystemErrCode1());
        if (!ret.equals("")) {
            retString += ret + "\n";
        }
        ret = getErrCodeStr(getSystemErrCode2());
        if (!ret.equals("")) {
            retString += ret + "\n";
        }
        ret = getErrCodeStr(getSystemErrCode3());
        if (!ret.equals("")) {
            retString += ret + "\n";
        }
        ret = getErrCodeStr(getSystemErrCode4());
        if (!ret.equals("")) {
            retString += ret + "\n";
        }
        if (retString.endsWith("\n"))
            retString = retString.substring(0, retString.length() - 1);
        return retString;
    }

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
                return app.getString(R.string.err_SEC_LIDSORAOPEN);
            case 0x0E:
                return app.getString(R.string.err_SEC_LIDSORASHORT);
            case 0x0F:
                return app.getString(R.string.err_SEC_LIDSORBOPEN);
            case 0x10:
                return app.getString(R.string.err_SEC_LIDSORBSHORT);
            case 0x11:
                return app.getString(R.string.err_SEC_LIDSORCOPEN);
            case 0x12:
                return app.getString(R.string.err_SEC_LIDSORCSHORT);
            case 0x13:
                return app.getString(R.string.err_SEC_RADSORAOPEN);
            case 0x14:
                return app.getString(R.string.err_SEC_RADSORASHORT);
            case 0x15:
                return app.getString(R.string.err_SEC_RADSORBOPEN);
            case 0x16:
                return app.getString(R.string.err_SEC_RADSORBSHORT);
            case 0x17:
                return app.getString(R.string.err_SEC_RADSORCOPEN);
            case 0x18:
                return app.getString(R.string.err_SEC_RADSORCSHORT);
            case 0x19:
                return app.getString(R.string.err_SEC_MOD1TOOHEAT);
            case 0x1A:
                return app.getString(R.string.err_SEC_MOD1TOOCOOL);
            case 0x1B:
                return app.getString(R.string.err_SEC_MOD2TOOHEAT);
            case 0x1C:
                return app.getString(R.string.err_SEC_MOD2TOOCOOL);
            case 0x1D:
                return app.getString(R.string.err_SEC_MOD3TOOHEAT);
            case 0x1E:
                return app.getString(R.string.err_SEC_MOD3TOOCOOL);
            case 0x1F:
                return app.getString(R.string.err_SEC_MOD4TOOHEAT);
            case 0x20:
                return app.getString(R.string.err_SEC_MOD4TOOCOOL);
            case 0x21:
                return app.getString(R.string.err_SEC_MOD5TOOHEAT);
            case 0x22:
                return app.getString(R.string.err_SEC_MOD5TOOCOOL);
            case 0x23:
                return app.getString(R.string.err_SEC_MOD6TOOHEAT);
            case 0x24:
                return app.getString(R.string.err_SEC_MOD6TOOCOOL);
            case 0x25:
                return app.getString(R.string.err_SEC_LIDATOOHEAT);
            case 0x26:
                return app.getString(R.string.err_SEC_LIDATOOCOOL);
            case 0x27:
                return app.getString(R.string.err_SEC_LIDBTOOHEAT);
            case 0x28:
                return app.getString(R.string.err_SEC_LIDBTOOCOOL);
            case 0x29:
                return app.getString(R.string.err_SEC_LIDCTOOHEAT);
            case 0x2A:
                return app.getString(R.string.err_SEC_LIDCTOOCOOL);
            case 0x2B:
                return app.getString(R.string.err_SEC_RADATOOHEAT);
            case 0x2C:
                return app.getString(R.string.err_SEC_RADATOOCOOL);
            case 0x2D:
                return app.getString(R.string.err_SEC_RADBTOOHEAT);
            case 0x2E:
                return app.getString(R.string.err_SEC_RADBTOOCOOL);
            case 0x2F:
                return app.getString(R.string.err_SEC_RADCTOOHEAT);
            case 0x30:
                return app.getString(R.string.err_SEC_RADCTOOCOOL);
            case 0x31:
                return app.getString(R.string.err_SEC_MOD1HEATERR);
            case 0x32:
                return app.getString(R.string.err_SEC_MOD1COOLERR);
            case 0x33:
                return app.getString(R.string.err_SEC_MOD2HEATERR);
            case 0x34:
                return app.getString(R.string.err_SEC_MOD2COOLERR);
            case 0x35:
                return app.getString(R.string.err_SEC_MOD3HEATERR);
            case 0x36:
                return app.getString(R.string.err_SEC_MOD3COOLERR);
            case 0x37:
                return app.getString(R.string.err_SEC_MOD4HEATERR);
            case 0x38:
                return app.getString(R.string.err_SEC_MOD4COOLERR);
            case 0x39:
                return app.getString(R.string.err_SEC_MOD5HEATERR);
            case 0x3A:
                return app.getString(R.string.err_SEC_MOD5COOLERR);
            case 0x3B:
                return app.getString(R.string.err_SEC_MOD6HEATERR);
            case 0x3C:
                return app.getString(R.string.err_SEC_MOD6COOLERR);
            case 0x3D:
                return app.getString(R.string.err_SEC_LIDAERRHEAT);
            case 0x3E:
                return app.getString(R.string.err_SEC_LIDAERRCOOL);
            case 0x3F:
                return app.getString(R.string.err_SEC_LIDBERRHEAT);
            case 0x40:
                return app.getString(R.string.err_SEC_LIDBERRCOOL);
            case 0x41:
                return app.getString(R.string.err_SEC_LIDCERRHEAT);
            case 0x42:
                return app.getString(R.string.err_SEC_LIDCERRCOOL);
            case 0x43:
                return app.getString(R.string.err_SEC_RADAERRHEAT);
            case 0x44:
                return app.getString(R.string.err_SEC_RADAERRCOOL);
            case 0x45:
                return app.getString(R.string.err_SEC_RADBERRHEAT);
            case 0x46:
                return app.getString(R.string.err_SEC_RADBERRCOOL);
            case 0x47:
                return app.getString(R.string.err_SEC_RADCERRHEAT);
            case 0x48:
                return app.getString(R.string.err_SEC_RADCERRCOOL);
            case 0x49:
                return app.getString(R.string.err_SEC_LIDAHEATERR);
            case 0x4A:
                return app.getString(R.string.err_SEC_LIDACTRLERR);
            case 0x4B:
                return app.getString(R.string.err_SEC_LIDBHEATERR);
            case 0x4C:
                return app.getString(R.string.err_SEC_LIDBCTRLERR);
            case 0x4D:
                return app.getString(R.string.err_SEC_LIDCHEATERR);
            case 0x4E:
                return app.getString(R.string.err_SEC_LIDCCTRLERR);
            case 0x4F:
                return app.getString(R.string.err_SEC_FANNOTWORK);
            case 0x50:
                return app.getString(R.string.err_SEC_FANCTRLERR);
            case 0x51:
                return app.getString(R.string.err_SEC_CONTROLTOOHEAT);
            case 0x52:
                return app.getString(R.string.err_SEC_MODEMPTY);
            case 0x53:
                return app.getString(R.string.err_SEC_INSIDESOROPEN);
            case 0x54:
                return app.getString(R.string.err_SEC_INSIDESORSHORT);
            default:
                return "";
        }
    }

    protected float decimalToFloat(float val) {
        return BigDecimal.valueOf(val).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }


//
//主面中的数值跑马
//	@Override
//	public String getDispTempAStr() {
//		return String.format("%.1f", horseRaceA.getTemp());
//	}
//
//	@Override
//	public String getUnDispTempAStr() {
//		return String.format("%.1f", horseRaceA.getUnTemp());
//	}
//
//	@Override
//	public String getDispTempBStr() {
//		return String.format("%.1f", horseRaceB.getTemp());
//	}
//
//	@Override
//	public String getUnDispTempBStr() {
//		return String.format("%.1f", horseRaceB.getUnTemp());
//	}
//
//	@Override
//	public String getDispTempCStr() {
//		return String.format("%.1f", horseRaceC.getTemp());
//	}
//
//	@Override
//	public String getUnDispTempCStr() {
//		return String.format("%.1f", horseRaceC.getUnTemp());
//	}

}
