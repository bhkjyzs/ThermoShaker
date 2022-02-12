package com.example.thermoshaker.serial.uart.system;

public class DefaultSystemClass extends SystemClass{

    /* 构造函数 */
    public DefaultSystemClass() {
        super();
        size = 19;
        init();
    }
    /* 开始符和地址位 5/0-4 */

    // 模块类型 1/5

    // 指示灯运行模式 1/6
    @Override
    public void setLedState(SystemType.LedState key) {
        switch (key) {
            case CYCLE_BRIGHT:
                data[6] = 0x00;
                break;
            case CONST_BRIGHT:
                data[6] = 0x3A;
                break;
            default:
                data[6] = 0x00;
                break;
        }
    }
    // 声音设置 1/7
    @Override
    public void setSoundSetting(byte key) {
        data[7] = key;
    }

    //版本后缀 1/8
    @Override
    public String getVerSuffix() {
        return String.valueOf((char) data[8]);
    }

    // 软件版本 1/9
    @Override
    public String getSoftwareVersion() {
        return String.valueOf(data[9]  & 0xff);
    }

    // 设备编号 10/10-19
    @Override
    public String getDeviceNum() {
        String numStr = "";
        try {
            byte[] Num = new byte[10];
            System.arraycopy(data, 10, Num, 0, 10);
            numStr = new String(Num, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numStr;
    }


    @Override
    public void setDeviceNum(String str) {
        try {
            byte[] code = str.getBytes("UTF-8");
            for (int i = 0; i < 10; i++) {
                if (i < code.length)
                    data[10 + i] = code[i];
                else
                    data[10 + i] = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
