package com.example.thermoshaker.serial;


import android.content.Context;
import android.util.Log;


import com.example.thermoshaker.model.ProgramInfo;

import java.util.List;

import static com.example.thermoshaker.serial.DataUtils.HexToByteArr;
import static com.example.thermoshaker.serial.DataUtils.byteMergerAll;
import static com.example.thermoshaker.serial.DataUtils.crc16;
import static com.example.thermoshaker.serial.DataUtils.ByteArrToHex;


public class ControlUtil {
    private static final String TAG = "ControlUtil";

    private Context context;

    //起始符
    static byte[] b1 = {(byte) 0xAA};
    //b2 通讯体类型
    //b3 通讯命令
    //b4 数据长度
    //b5 具体数据
    //b6 校验码
    static byte[] b7 = {(byte) 0x55};



    private final static int num_redo = 5; //重试五次

    //数据命令发送
    public static boolean sendData(byte[] data) {

        byte[] res; //通讯体
        //发送数据
        Log.i(TAG, "data: *****  " + ByteArrToHex(data) + "   ********");
        for (int i = 0; i < num_redo; i++) {
            res = SerialPortUtil.sendSerialPort(data);
            if (res != null) {
                if (isDataOk(res)) {

                    Log.i(TAG, "the process is ok");
                    return true;
                }
            }
        }
        Log.d(TAG, "the process is error");
        return false;
    }

    //数据命令接收
    private static boolean isDataOk(byte[] res) {
        if (res[0] == ControlParam.response_data && res[1] == ControlParam.response_ok) {
            Log.i(TAG, "** return info is ok"+res[0]);
            return true;
        }
        Log.d(TAG, "** the return info is error"+res[0]);
        return false;
    }

    //查询发送
    public static byte[] sendDataQuery(byte[] data) {
        byte[] res; //通讯体
        //发送数据
        Log.i(TAG, "data: *****  " + ByteArrToHex(data) + "   ********");
        for (int i = 0; i < num_redo; i++) {
            res = SerialPortUtil.sendSerialPort(data);
            if (res != null) {
                Log.e(TAG, "the process is ok");
                return res;
            }
        }
        Log.e(TAG, "the process is error");
        return null;
    }

//    //发送升级包
//    public static byte[] sendDataUpdate(byte[] data,int time) {
//        byte[] res; //通讯体
//
//        //发送数据
//        Log.i(TAG, "data: *****  " + ByteArrToHex(data) + "   ********");
//        res = SerialPortUtil.sendSerialPort_1(data,time);
//        if (res != null) {
//            Log.e(TAG, "the send is succeed:" + res);
//            return res;
//        }
//        Log.d(TAG, MyApp.baudrate+"   the send is error");
//        return null;
//    }

    /**
     * 命令通讯
     **/
    public static boolean bioOrderSend(byte order_type) {
        byte b2 = ControlParam.head_order;
        byte b3 = order_type;
        byte[] temp = {b2, b3};
        byte[] b5 =  HexToByteArr(crc16(temp));
        byte[] All =  byteMergerAll(b1, temp, b5, b7);
        return sendData(All);
    }

    /**
     * 数据通讯
     **/
    public static boolean bioDataInfoSend(byte data_type, byte[] len, byte[] body) {
        byte[] b2 = {ControlParam.head_data};
        byte[] b3 = {data_type};
        byte[] temp =  byteMergerAll(b2, b3, len, body);
        byte[] b5 = HexToByteArr(crc16(temp));
        byte[] All =  byteMergerAll(b1, temp, b5, b7);
        return sendData(All);
    }


    /**
     * 查询通讯
     **/
    public static byte[] bio_query_info(byte order_type) {
        byte b2 = ControlParam.head_query;
        byte b3 = order_type;
        byte[] temp = {b2, b3};
        byte[] b5 = HexToByteArr(crc16(temp));
        byte[] All =  byteMergerAll(b1, temp, b5, b7);
        return sendDataQuery(All);
    }
}
