package com.example.thermoshaker.serial;

public class ControlParam {
    public final static byte head_order=(byte)0xAB; //命令通讯
    public final static byte head_data=(byte)0x3d; //数据通讯
    public final static byte head_query=0x4e; //查询通讯

    public final static byte response_data=0x2c; //应答通讯头
    public final static byte response_ok=(byte)0xc1; //应答通讯正确
    public final static byte response_query=0x3d; //查询通讯头

    /******    命令模块   ******/
    public final static byte OT_RUN = (byte)0xA1; //运行
    public final static byte OT_STOP = (byte)0xA2; //停止
    public final static byte OT_PAUSE = (byte)0xA3; //暂停
    public final static byte OT_RESUME = (byte)0xA4; //继续
    public final static byte OT_KEY_SOUND = (byte)0xC1; //按键提示音命令
    public final static byte OT_UPDATE_APP = (byte)0xD1; //IAP升级命令

    public final static byte OT_DEBUG_RUN = (byte)0x51; //运行调试模式命令
    public final static byte OT_DEBUG_STOP = (byte)0x52; //停止调试模式命令

    public final static byte OT_DB_TE1_ON = (byte)0x53;  //调试模式模块1开启命令
    public final static byte OT_DB_TE1_OFF = (byte)0x54; //调试模式模块1关闭命令
    public final static byte OT_DB_TE2_ON = (byte)0x55;  //调试模式模块2开启命令
    public final static byte OT_DB_TE2_OFF = (byte)0x56; //调试模式模块2关闭命令
    public final static byte OT_DB_TE3_ON = (byte)0x57;  //调试模式模块3开启命令
    public final static byte OT_DB_TE3_OFF = (byte)0x58; //调试模式模块3关闭命令
    public final static byte OT_DB_TE4_ON = (byte)0x59;  //调试模式模块4开启命令
    public final static byte OT_DB_TE4_OFF = (byte)0x5A; //调试模式模块4关闭命令
    public final static byte OT_DB_TE5_ON = (byte)0x5B;  //调试模式模块5开启命令
    public final static byte OT_DB_TE5_OFF = (byte)0x5C; //调试模式模块5关闭命令
    public final static byte OT_DB_TE6_ON = (byte)0x5D;  //调试模式模块6开启命令
    public final static byte OT_DB_TE6_OFF = (byte)0x5E; //调试模式模块6关闭命令
    public final static byte OT_DB_TE7_ON = (byte)0x5F;  //调试模式模块7开启命令
    public final static byte OT_DB_TE7_OFF = (byte)0x60; //调试模式模块7关闭命令
    public final static byte OT_DB_TE8_ON = (byte)0x61;  //调试模式模块8开启命令
    public final static byte OT_DB_TE8_OFF = (byte)0x62; //调试模式模块8关闭命令

    public final static byte OT_DB_ADJ = (byte)0x63; //调试模式修正命令

    public final static byte OT_DB_FAN_ON =  (byte)0x64; //调试模式风扇开启命令
    public final static byte OT_DB_FAN_OFF = (byte)0x65; //调试模式风扇关闭命令
    public final static byte OT_DB_SUN_ON =  (byte)0x66; //调试模式日光灯开启命令
    public final static byte OT_DB_SUN_OFF = (byte)0x67; //调试模式日光灯关闭命令
    public final static byte OT_DB_UV_ON =   (byte)0x68; //调试模式紫外灯开启命令
    public final static byte OT_DB_UV_OFF =  (byte)0x69; //调试模式紫外灯关闭命令
    public final static byte OT_DB_MOVE_ZERO =  (byte)0x6A; //回到零位命令
    public final static byte OT_DB_MOVE_POS = (byte)0x6B; //回到孔位命令
    public final static byte OT_DB_BRAKE_OFF = (byte)0x6C; //释放电机命令
    public final static byte OT_DB_POS = (byte)0x6D; //写入校准值命令
    public final static byte OT_DB_LED_ON =  (byte)0x72; //调试模式LED开启命令
    public final static byte OT_DB_LED_OFF = (byte)0x73; //调试模式LED关闭命令
    public final static byte OT_DB_ALL_OFF =   (byte)0x88; //调试模式关闭所有命令


    /******    数据模块   ******/
    public final static byte DT_FILE =  (byte)0xDA; //文件数据
    public final static byte[] DT_FILE_LEN =  {(byte)0x02, (byte)0xB2}; //文件数据长度
    public final static byte[] DN_FILEDATA_NUM = {0x02,(byte)0xB4};
    public final static byte DT_SYSTEM = (byte)0xD2;
    public final static byte[] DN_SYSDATA_NUM = {0x00,(byte)0x17};
    public final static byte DT_TEMPADJ = (byte)0xD3;
    public final static byte[] DN_ADJDATA_NUM = {0x00,(byte)0x50};
    public final static byte DT_RUNDATA = (byte)0xD4;
    public final static byte[] DN_RUNDATA_NUM = {0x00,(byte)0x32};
    public final static byte DT_DEBUGDATA = (byte)0xD5;
    public final static byte[] DN_DEBUGDATA_NUM = {0x00,(byte)0x10};
    public final static byte DT_RELATIVEPOS = (byte)0xD6;
    public final static byte[] DN_RELATIVEPOS_NUM = {0x00,(byte)0x04};

    /******    查询模块   ******/
    public final static byte ASK_SYSTEM = (byte)0xE1;
    public final static byte ASK_DEBUGDATA = (byte)0xE2;
    public final static byte ASK_RUNDATA = (byte)0xE3;
    public final static byte ASK_TEMPADJ = (byte)0xE4;

    /*******  系统错误  *******/
    public final static byte SEC_MODSOR1OPEN = (byte)0x01;
    public final static byte SEC_MODSOR1SHORT = (byte)0x02;
    public final static byte SEC_MODSOR2OPEN = (byte)0x03;
    public final static byte SEC_MODSOR2SHORT = (byte)0x04;
    public final static byte SEC_MODSOR3OPEN = (byte)0x05;
    public final static byte SEC_MODSOR3SHORT = (byte)0x06;
    public final static byte SEC_MODSOR4OPEN = (byte)0x07;
    public final static byte SEC_MODSOR4SHORT = (byte)0x08;
    public final static byte SEC_MODSOR5OPEN = (byte)0x09;
    public final static byte SEC_MODSOR5SHORT = (byte)0x0A;
    public final static byte SEC_MODSOR6OPEN = (byte)0x0B;
    public final static byte SEC_MODSOR6SHORT = (byte)0x0C;
    public final static byte SEC_MODSOR7OPEN = (byte)0x0D;
    public final static byte SEC_MODSOR7SHORT = (byte)0x0E;
    public final static byte SEC_MODSOR8OPEN = (byte)0x0F;
    public final static byte SEC_MODSOR8SHORT = (byte)0x10;
    public final static byte SEC_MOD1TOOHEAT = (byte)0x11;
    public final static byte SEC_MOD2TOOHEAT = (byte)0x12;
    public final static byte SEC_MOD3TOOHEAT = (byte)0x13;
    public final static byte SEC_MOD4TOOHEAT = (byte)0x14;
    public final static byte SEC_MOD5TOOHEAT = (byte)0x15;
    public final static byte SEC_MOD6TOOHEAT = (byte)0x16;
    public final static byte SEC_MOD7TOOHEAT = (byte)0x17;
    public final static byte SEC_MOD8TOOHEAT = (byte)0x18;
    public final static byte SEC_MOD1HEATERR = (byte)0x19;
    public final static byte SEC_MOD2HEATERR = (byte)0x1A;
    public final static byte SEC_MOD3HEATERR = (byte)0x1B;
    public final static byte SEC_MOD4HEATERR = (byte)0x1C;
    public final static byte SEC_MOD5HEATERR = (byte)0x1D;
    public final static byte SEC_MOD6HEATERR = (byte)0x1E;
    public final static byte SEC_MOD7HEATERR = (byte)0x1F;
    public final static byte SEC_MOD8HEATERR = (byte)0x20;
    public final static byte SEC_XMOTORERR = (byte)0x21;
    public final static byte SEC_MAGNETERR = (byte)0x22;
    public final static byte SEC_MAGNETSETERR = (byte)0x23;

    //日光灯、紫外、门打开状态
    public final static byte WINDOW_OPEN_STATE = (byte)0xAA;
}
