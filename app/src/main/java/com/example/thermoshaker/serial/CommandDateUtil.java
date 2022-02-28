package com.example.thermoshaker.serial;

import static com.example.thermoshaker.serial.DataUtils.ByteArrToHex;
import static com.example.thermoshaker.serial.DataUtils.isDataOk;
import static com.example.thermoshaker.serial.DataUtils.translate;

import android.os.SystemClock;
import android.util.Log;

import com.example.thermoshaker.base.Content;
import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.model.FileRunProgram;
import com.example.thermoshaker.model.ProgramInfo;
import com.example.thermoshaker.model.ProgramStep;
import com.example.thermoshaker.model.SystemProgram;
import com.licheedev.myutils.LogPlus;

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
    private final static int num_redo = 1; //重试五次//暂时重试一次

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
        return sendDate(All);
    }
    public static boolean sendDate(byte[] data){
        byte[] res; //通讯体
        //发送数据
        LogPlus.i(TAG, "data: *****  " + ByteArrToHex(data) + "   ********");
        for (int i = 0; i < num_redo; i++) {
            res = SerialPortUtil.sendSerialPort(data);
            if (res != null) {
                if (isDataOk(res)) {

                    LogPlus.i(TAG, "the process is ok");
                    return true;
                }
            }
        }
        LogPlus.d(TAG, "the process is error");
        return false;
    }

    //发送升级包
    public static byte[] sendDataUpdate(byte[] data,int time) {
        byte[] res; //通讯体

        //发送数据
        Log.i(TAG, "data: *****  " + ByteArrToHex(data) + "   ********");
        res = SerialPortUtil.sendSerialPort_1(data,time);
        if (res != null) {
            Log.e(TAG, "the send is succeed:" + res);
            return res;
        }
        return null;
    }
    /**
     * 发送查询通讯
     * @param commend
     */
    public static byte[] SendQueryCommand(byte commend){
        byte b2 = ControlParam.head_query;
        byte b3 = commend;
        byte[] temp = {b2, b3};
        byte[] b5 = DataUtils.HexToByteArr(DataUtils.crc16(temp));
        byte[] All = DataUtils.byteMergerAll(b1, temp, b5, b7);
        return sendQueryDate(All);
    }
    public static byte[] sendQueryDate(byte[] data){
        byte[] res; //通讯体
        //发送数据
        LogPlus.i(TAG, "data: *****  " + ByteArrToHex(data) + "   ********");
        for (int i = 0; i < num_redo; i++) {
            res = SerialPortUtil.sendSerialPort(data);
            if (res != null) {
                LogPlus.e(TAG, "the process is ok");
                return res;
            }
        }
        LogPlus.e(TAG, "the process is error");
        return null;
    }
    /**
     * 发送数据通讯
     * @param commend
     */
    public static byte[] SendDataCommand(byte commend,byte[] body){
        byte[] b2 = {ControlParam.head_data};
        byte[] b3 = {commend};
        byte[] temp = DataUtils.byteMergerAll(b2, b3, body);
        byte[] b5 = DataUtils.HexToByteArr(DataUtils.crc16(temp));
        byte[] All = DataUtils.byteMergerAll(b1, temp, b5, b7);
        return sendQueryDate(All);
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

        if (param == null || param[0] != (byte) 0x3d || param[1] != (byte) 0xd4) {
            Log.e(TAG, "the return runParam is error");
            return null;
        }
        Content.fileRunProgram.setCURRunStep(param[4]);
        Content.fileRunProgram.setALLRunStepNum(param[5]);
        Content.fileRunProgram.setCURWaitTime(DataUtils.bytes2Int(param,6,4));
        Content.fileRunProgram.setCURRemnantTime(DataUtils.bytes2Int(param,10,4));
        Content.fileRunProgram.setRunCir(param[15]);
        Content.fileRunProgram.setCirStep(param[16]);

        Content.fileRunProgram.setModuleTemp((float)(DataUtils.bytes2Int(param, 17, 2) / 100.0));

        Content.fileRunProgram.setDispTemp((float)(DataUtils.bytes2Int(param, 19, 2) / 100.0));
        Content.fileRunProgram.setLidModuleTemp((float)(DataUtils.bytes2Int(param, 21, 2) / 100.0));
        Content.fileRunProgram.setLidDispTemp((float)(DataUtils.bytes2Int(param, 23, 2) / 100.0));


        Content.fileRunProgram.setRunCourse(param[25]);
        Content.fileRunProgram.setRunFileState(param[26]);
        //param[59],[60] reserve
        int[] system_err = new int[4];
        for (int i = 0; i < 4; i++) {
            system_err[i] = param[29 + i];
        }
        Content.fileRunProgram.setSystemErrCode(system_err);
        return Content.fileRunProgram;
    }



    //系统参数数据 byte[] -> param
    public static SystemProgram bioSystemRec(byte[] param) {
        if (param == null || param[0] != (byte) 0x3d || param[1] != (byte) 0xd2) {
            Log.e(TAG, "the return runParam is error");
            return null;
        }
        int len = DataUtils.bytes2Int(param, 2, 2);
        Content.control_systemParam.setUi_type(param[4]);
        Content.control_systemParam.setLed_runMode(param[5]);
        Content.control_systemParam.setSound(param[6]);
        Content.control_systemParam.setVERSUFFIX(new String(new byte[]{param[7]}));
        Content.control_systemParam.setVERSION(param[8]);
        byte[] temp = new byte[10];
        for (int i = 0; i < 10; i++) {
            temp[i] = param[9 + i];
        }
        Content.control_systemParam.setDevice_num(new String(temp));
        return Content.control_systemParam;
    }





}
