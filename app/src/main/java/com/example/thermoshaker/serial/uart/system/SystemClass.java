package com.example.thermoshaker.serial.uart.system;


import com.example.thermoshaker.serial.uart.Crc16JiaoYanMa;

/**
 * @author LiJ E-mail:newwuxian911@QQ.com
 * @version 2019年5月27日 上午10:02:48
 * @系统参数数据的父类
 */
public class SystemClass implements SystemInterface {
    protected int size;
    protected byte[] data;

    protected void init() {
        data = new byte[size + 8];
        data[0] = (byte) 0xaa;
        data[1] = (byte) 0x3d;
        data[2] = (byte) 0xd2;
        data[3] = (byte) (size >> 8);
        data[4] = (byte) size;
    }

    /* 解析数组 */
    @Override
    public boolean analysis(byte[] bin) {
        if (bin.length == size + 8 && bin[0] == data[0] && bin[2] == data[2] && bin[bin.length - 1] == (byte) 0x55) {
            short val = Crc16JiaoYanMa.CRC16Code(bin, 1, bin.length - 4);
            if (bin[bin.length - 3] == (byte) (val >> 8) && bin[bin.length - 2] == (byte) val) {
                data = bin;
                return true;
            }
        }
        return false;
    }

    /* 输出数组 */
    @Override
    public byte[] output() {
        return data;
    }

    @Override
    public String getUiType() {
        return null;
    }

//



    @Override
    public void setLedState(SystemType.LedState key) {

    }

    @Override
    public void setSoundSetting(byte key) {

    }

    @Override
    public String getDeviceNum() {
        return null;
    }

    @Override
    public void setDeviceNum(String str) {

    }

    @Override
    public String getSoftwareVersion() {
        return null;
    }

    @Override
    public String getVerSuffix() {
        return "";

    }

//    @Override
//    public SystemType.LidState getFLidMode() {
//        return null;
//    }
//
//    @Override
//    public void setFLidMode(SystemType.LidState lidMode) {
//
//    }

//    @Override
//    public MachineEnum getModuleType() {
//        switch (data[5] & 0xff) {
//            case 0x01:
//                return MachineEnum.ELVE_16;
//            case 0x02:
//                return MachineEnum.ELVE_16V;
//            case 0x03:
//                return MachineEnum.ELVE_32G;
//            case 0x0A:
//            case 0x10:
//                return MachineEnum.RePure_A;
//            case 0x0E:
//                return MachineEnum.RePure_384;
//            case 0x0F:
//                return MachineEnum.RePure_AF;
//            case 0x11:
//                return MachineEnum.RePure_B;
//            case 0x12:
//                return MachineEnum.RePure_C;
//            case 0x13:
////                MainApp app = MainApp.getInstance();
////                boolean is_gradient_on = app.appClass.isGradientOn();
////                if (!is_gradient_on) {//开关关闭
////                    return MachineEnum.RePure_DB;
////                }
//                return MachineEnum.RePure_D;
//            case 0x20:
//                return MachineEnum.PRO_X2;
//            case 0x21:
//                return MachineEnum.PRO_X3;
//            case 0x30:
//                return MachineEnum.Prima96_;
//            case 0x31:
//                return MachineEnum.Prima_DUO_;
//            case 0x32:
//                return MachineEnum.Prima_TRIO_;
//            case 0x33:
//                return MachineEnum.Prima_TRIO;
//            case 0x34:
//                return MachineEnum.Prima_96_2D;
//            default:
//                return null;
//        }
//    }
}
