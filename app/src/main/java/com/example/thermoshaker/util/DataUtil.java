package com.example.thermoshaker.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DataUtil {
    public static final String data_path = Environment.getExternalStorageDirectory().toString() + "/thermoshaker/";
    public static final String data_name = "UserFiles/";
    public static final String param_name = "RunFiles/";

    //软件升级文件名
    public static final String apk_data_name = "updateApks.apk";
    //固件升级文件名
    public static final String firmware_data_name = "updateBins";


    public static boolean writeData(String data, String file_path, String file_name, boolean append) {
        Log.i("path:  ", Environment.getExternalStorageDirectory().getAbsolutePath());
        try {
            // 判断当前的手机是否有sd卡
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                // 已经挂载了sd卡
                return false;
            }
            File file = makeFilePath(file_path, file_name);
            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static String readData(String file_name) {
        Log.i("path:  ", Environment.getExternalStorageDirectory().getAbsolutePath());
        // 判断当前的手机是否有sd卡
        String state = Environment.getExternalStorageState();
        StringBuilder sb = new StringBuilder();

        try {
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                // 已经挂载了sd卡
                Log.e("DataUtil", "*** no sdcard ***");
                return null;
            }
            String filename = file_name;
            File file = new File(filename);
            if (!file.exists()) {
                return null;
            }
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while (len > 0) {
                sb.append(new String(buffer, 0, len));
                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            //确保file的父目录文件夹存在
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

}
