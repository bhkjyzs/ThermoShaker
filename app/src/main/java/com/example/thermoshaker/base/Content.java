package com.example.thermoshaker.base;

import android.content.Intent;
import android.hardware.usb.UsbDevice;

import com.example.thermoshaker.model.FileRunProgram;
import com.example.thermoshaker.model.SystemProgram;
import com.example.thermoshaker.util.usb.UsbHelper;
import com.github.mjdev.libaums.fs.FileSystem;

public class Content {

    //可添加程序总数
    public static int FileNumberNum = 50;

    public static String usb_state = Intent.ACTION_MEDIA_EJECT; //usb挂载状态
    public static String usb_path = ""; //usb挂载地址
    public static FileSystem currentFs;//挂载的U盘

    //设备是否正常通信
    public static boolean isCommunication = false;

    //系统参数缓存
    public static SystemProgram control_systemParam = new SystemProgram();
    //文件运行参数缓存
    public static FileRunProgram fileRunProgram = new FileRunProgram();

    public static boolean running_flag = true; //app是否运行 true运行 false停止
    //通信正常，默认正常
    public static boolean COMMUNICATION_NORMAL = true;

}
