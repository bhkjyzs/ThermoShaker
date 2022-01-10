package com.example.thermoshaker.base;

import com.example.thermoshaker.model.FileRunProgram;
import com.example.thermoshaker.model.SystemProgram;

public class Content {

    //可添加程序总数
    public static int FileNumberNum = 50;

    public static String usb_state = ""; //usb挂载状态
    public static String usb_path = ""; //usb挂载地址

    //设备是否正常通信
    public static boolean isCommunication = false;

    //系统参数缓存
    public static SystemProgram control_systemParam = new SystemProgram();
    //文件运行参数缓存
    public static FileRunProgram fileRunProgram = new FileRunProgram();

}
