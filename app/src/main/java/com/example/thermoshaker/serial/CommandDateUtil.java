package com.example.thermoshaker.serial;

import static com.example.thermoshaker.serial.DataUtils.translate;

import android.util.Log;

import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.FileRunProgram;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.model.SystemProgram;
import com.kongqw.serialportlibrary.SerialPortManager;

import java.util.Date;

public class CommandDateUtil {
    private static final String TAG = "CommandDateUtil";
    //起始符
    static byte[] b1 = {(byte) 0xAA};
    //b2 通讯体类型
    //b3 通讯命令
    //b4 数据长度
    //b5 具体数据
    //b6 校验码
    static byte[] b7 = {(byte) 0x55};
    /**
     * 发送命令通讯
     * @param commend
     */
    public static boolean SendCommand(byte commend) {
        byte b2 = ControlParam.head_order;
        byte b3 = commend;
        byte[] temp = {b2, b3};
        byte[] b5 = DataUtils.HexToByteArr(DataUtils.crc16(temp));
        byte[] All = DataUtils.byteMergerAll(b1, temp, b5, b7);
        return MyApplication.getInstance().mSerialPortManager.sendBytes(All);
    }
    /**
     * 发送查询通讯
     * @param commend
     */
    public static void SendQueryCommand(byte commend){
        byte b2 = ControlParam.head_query;
        byte b3 = commend;
        byte[] temp = {b2, b3};
        byte[] b5 = DataUtils.HexToByteArr(DataUtils.crc16(temp));
        byte[] All = DataUtils.byteMergerAll(b1, temp, b5, b7);
        MyApplication.getInstance().mSerialPortManager.sendBytes(All);
    }
    /**
     * 发送数据通讯
     * @param commend
     */
    public static void SendDataCommand(byte commend){
        byte b2 = ControlParam.head_query;
        byte b3 = commend;
        byte[] temp = {b2, b3};
        byte[] b5 = DataUtils.HexToByteArr(DataUtils.crc16(temp));
        byte[] All = DataUtils.byteMergerAll(b1, temp, b5, b7);
        MyApplication.getInstance().mSerialPortManager.sendBytes(All);
    }

    /**
     * 文件数据
     * @param programInfo
     * @return
     */
    public static byte[] getProgramByte(ProgramInfo programInfo) {
        byte[] bytes = new byte[95];
        //升温设置
        // 0：升温动作同步
        //1：先升温后动作
        //2-51：升温到低于目标温度（1-50℃）开始动作
        bytes[0] = 0;
        //程序提前几步开始加热   1-3：程序提前1-3步开始加热
        bytes[1] = 1;
        //加热通道开关  0：对应加热通道关闭   1：对应加热通道开启
        bytes[2] = 0;
        //步骤总数
        bytes[3] = (byte) (programInfo.getStepList().size()+1);
        //当前步骤 当前程序开始运行步骤
        bytes[4] = (byte) (1);
        //循环开关
        bytes[5] = (byte) (programInfo.isLoopEnable()==true? 0:1);
        //开始步骤
        bytes[6] = (byte) programInfo.getLoopStart();
        //结束步骤
        bytes[7] = (byte) programInfo.getLoopEnd();
        //循环次数
        bytes[8] = (byte) programInfo.getLoopNum();
        //热盖温度
        intTobyteArray(Math.round(programInfo.getLidTm()*100F),bytes,9);
        for (int i = 0; i < programInfo.getStepList().size(); i++) {
            int index = 11 + i * 5;
            ProgramStep programStep = programInfo.getStepList().get(i);
            //设置温度
            intTobyteArray(Math.round(programStep.getTemperature()*100F),bytes,0+index);
            //升温速率
            if(programStep.getUpSpeed()==3.0){
                bytes[2+index] = 4;

            }else if(programStep.getUpSpeed()==2.0){
                bytes[2+index] = 3;

            }else if(programStep.getUpSpeed()==1.0){
                bytes[2+index] = 2;

            }else if(programStep.getUpSpeed()==0.1){
                bytes[2+index] = 1;

            }
            //降温速率
            if(programStep.getDownSpeed()==1.0){
                bytes[3+index] = 3;

            }else if(programStep.getDownSpeed()==0.5){
                bytes[3+index] = 2;

            }else if(programStep.getDownSpeed()==0.1){
                bytes[3+index] = 1;
            }

            //电机转速
            intTobyteArray(programStep.getZSpeed(),bytes,4+index);
            //方向
            switch (programStep.getDirection())
            {
                case Forward:
                    bytes[6+index] =0;
                    break;
                case Reversal:
                    bytes[6+index] =1;
                    break;
                case ForwardAndReverse:
                    bytes[6+index] =2;
                    break;
            }
            //步骤时间
            Date date = new Date(programStep.getTime());
            intTobyteArrayTime(Math.round(date.getTime()/1000L),bytes,7+index);
            //混匀方式
            bytes[10+index] = (byte) programStep.getMixingMode();
            //持续时间
            Date dateContinue = new Date(programStep.getContinued());
            intTobyteArray(Math.round(dateContinue.getTime()/1000L),bytes,11+index);
            //间隔时间
            Date dateIntermission = new Date(programStep.getIntermission());
            intTobyteArray(Math.round(dateIntermission.getTime()/1000L),bytes,13+index);
            //混合起点
            bytes[15+index] = (byte) programStep.getBlendStart();



        }


        return bytes;
    }

    /**
     * 系统数据
     */
    public static void getSystemProgram(SystemProgram systemParam){
        byte[] temp = new byte[9];
        temp[0] = (byte) systemParam.getUi_type();
        temp[1] = (byte) systemParam.getLed_runMode();
        temp[2] = (byte) systemParam.getSound();
        temp[3] = (byte) systemParam.getVERSUFFIX().getBytes()[0];
        temp[4] = (byte) systemParam.getVERSION();
        byte[] temp2 = systemParam.getDevice_num().getBytes();
        byte[] ress = new byte[10];
        if (temp2.length > 10) {
            for (int i = 0; i < 10; i++) {
                ress[i] = temp2[i];
            }
        } else {
            for (int i = 0; i < 10; i++) {
                if (i >= temp2.length) {
                    ress[i] = 0;
                } else {
                    ress[i] = temp2[i];
                }
            }
        }
    }

    //温度修正参数 param -> byte[]
    public static byte[] bioTempSend(int[][] param) {
        byte[] tt1 = translate(param[0], 2);
        byte[] tt2 = translate(param[1], 2);
        byte[] tt3 = translate(param[2], 2);
        byte[] tt4 = translate(param[3], 2);
        byte[] tt5 = translate(param[4], 2);
        return DataUtils.byteMergerAll(tt1, tt2, tt3, tt4, tt5);
    }

    //文件运行参数 下位机传上位机
    public static FileRunProgram bioRunParam(byte[] param) {
        FileRunProgram fileRunProgram = new FileRunProgram();
        if (param == null || param[0] != (byte) 0x3d || param[1] != (byte) 0xd4) {
            Log.e(TAG, "the return runParam is error");
            return null;
        }
        fileRunProgram.setCURRunStep(param[4]);
        fileRunProgram.setALLRunStepNum(param[5]);
        fileRunProgram.setModuleTemp((float)(DataUtils.bytes2Int(param, 6, 2) / 100.0));

        fileRunProgram.setDispTemp((float)(DataUtils.bytes2Int(param, 8, 2) / 100.0));
        fileRunProgram.setRunCourse(param[10]);
        fileRunProgram.setRunFileState(param[11]);
        //param[59],[60] reserve
        int[] system_err = new int[4];
        for (int i = 0; i < 4; i++) {
            system_err[i] = param[61 + i];
        }
        fileRunProgram.setSystemErrCode(system_err);
        return fileRunProgram;
    }





    /* int转byte */
    public static void intTobyteArray(int value, byte[] buffer, int offset) {
        buffer[offset] = (byte) (value >> 8);
        buffer[offset + 1] = (byte) value;
    }
    //int转化为byte数组 len为字节数
    public static void intTobyteArrayTime(int value, byte[] buffer, int offset) {
        buffer[offset] = (byte) (value >> 8);
        buffer[offset + 1] = (byte) value;
        buffer[offset + 2] = (byte) value;

    }
}