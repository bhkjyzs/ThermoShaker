package com.example.thermoshaker.serial;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {
    private static final String TAG = "DataUtils";
    //多个byte[]合并成一个byte[]
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }



    //CRC校验
    public static String crc16_t(byte []gprsstr) {
        try {
            int crc;
            int strlength, r;
            byte sbit;
            int tc;
            int lc;
            strlength = gprsstr.length;
            crc = 0xFFFF;
            for (int i = 0; i < strlength; i++) {
                tc = (int) (crc & 0x00FF); //取后八位
                lc = (int) (crc & 0xFF00);  //取前八位
                crc = (int) ((tc ^ gprsstr[i]) | lc);//后八位异或数组，前八位不变
                for (r = 0; r < 8; r++) { //移动8位
                    sbit = (byte) (crc & 0x0001);
                    crc >>>= 1;
                    if (sbit != 0)
                        crc ^= 0xA001;
                }
            }
            crc = ((crc & 0xff00) >> 8) | ((crc & 0x00ff) << 8);//前后8位交换
            return String.format("%04X", crc);
            //return Integer.toHexString(crc);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String crc16(byte[] bytes){
        int crc = 0x00;          // initial value
        int polynomial = 0x1021;
        for (int index = 0 ; index< bytes.length; index++) {
            byte b = bytes[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return String.format("%04X", crc);
    }

    //十六进制的字符串转换为byte数组
    public static byte[] HexToByteArr(String hex16Str) {
        char[] c = hex16Str.toCharArray();
        byte[] b = new byte[c.length / 2];
        for (int i = 0; i < b.length; i++) {
            int pos = i * 2;
            b[i] = (byte) ("0123456789ABCDEF".indexOf(c[pos]) << 4 | "0123456789ABCDEF"
                    .indexOf(c[pos + 1]));
            //System.out.print(b[i] + ",");
        }
        return b;
    }
    //int转化为byte数组 len为字节数
    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte)(value >> 8 * i);
        }
        return b;
    }

    //int数组转化成byte数组
    public static byte[] translate(int []BB, int len)
    {
        byte[] b = new byte[BB.length*len];
        for(int i=0;i<BB.length;i++)
        {
            for(int j=0;j<len;j++){
                b[i*len+len-j-1] = (byte)( BB[i]>>8 * j);
            }
        }
        return b;
    }
    //byte数组转化成int
    public static int bytes2Int(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = b[i] & 0xff;
            n <<= (--len) * 8; 
            sum += n;
        }
        return sum;
    }

    //byte数组转化成有符号int
    public static int bytes2IntSigned(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = (i==start) ? b[i] : (b[i] & 0xff);
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }



    // param int数组转byte[]
    public static byte[] translates(int [][]BB){
        int rowlength = BB.length; //获取行数
        int num_param=11;
        byte[] bb = new byte[16*rowlength];
        for(int i = 0 ;i<rowlength;i++)
        {
            bb[i]= (byte) BB[i][0];//孔位
            bb[i+1] = int2Bytes(BB[i][1],2)[0]; //震荡时间
            bb[i+2] = int2Bytes(BB[i][1],2)[1];
            bb[i+3] = int2Bytes(BB[i][2],2)[0]; //吸磁时间
            bb[i+4] = int2Bytes(BB[i][2],2)[1];
            bb[i+5] = int2Bytes(BB[i][3],2)[0]; //等待时间
            bb[i+6] = int2Bytes(BB[i][3],2)[1];
            bb[i+7] = int2Bytes(BB[i][4],2)[0]; //容积
            bb[i+8] = int2Bytes(BB[i][4],2)[1];
            bb[i+9] = (byte)(BB[i][5]); //混合速度
            bb[i+10] = int2Bytes(BB[i][6],2)[0]; //设置温度值
            bb[i+11] = int2Bytes(BB[i][6],2)[1];
            bb[i+12] = (byte)(BB[i][7]); //混合位置
            bb[i+13] = (byte)(BB[i][8]); //混合幅度
            bb[i+14] = (byte)(BB[i][9]); //吸磁位置
            bb[i+15] = (byte)(BB[i][10]); //吸磁速度
        }
        return bb;
    }

    //1字节转2个Hex字符
    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

    //字节数组转转hex字符串
    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte valueOf : inBytArr) {
            strBuilder.append(Byte2Hex(Byte.valueOf(valueOf)));
            //strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    //字节数组转转hex字符串，可选长度
    public static String ByteArrToHex(byte[] inBytArr, int offset, int byteCount) {
        StringBuilder strBuilder = new StringBuilder();
        int j = byteCount;
        for (int i = offset; i < j; i++) {
            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i])));
        }
        return strBuilder.toString();
    }

    //获取命令数据
    public static byte[] delete(byte a[], int len) {
        byte b[] = new byte[len - 4];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[i+1];
        }

        return b;
    }
    //获取命令数据
    public static byte[] deletes(byte a[], int len) {
        List<Integer> list = new ArrayList<>();

        int flag=0;
        for (int i = 0; i < len; i++) {
            if(a[i]==(byte)0xAA){
                list.add(i);
                flag++;
            }
        }

        byte b[] = null;
        if(flag>=2){
            b = new byte[len-list.get(list.size()-1) - 4];
            for (int i = 0; i < b.length; i++) {
                b[i] = a[list.get(list.size()-1)+1];
            }
        }else {
            b = new byte[len - 4];
            for (int i = 0; i < b.length; i++) {
                b[i] = a[i+1];
            }
        }

        Log.i(TAG,list.toString()+"   "+list.size()+"   "+flag);

        return b;
    }

    public static byte[] isChecked(byte[] a, int len){
        byte[] a_1 ={a[len-3],a[len-2]};//CRC校验位
        byte[] body=delete(a,len);
        String res=ByteArrToHex(a,0,len);
        Log.i(TAG,"*** the data body is: " + res+"\n"+ByteArrToHex(body)+"\n"+ByteArrToHex(a_1)+"\n"+ByteArrToHex(a)+"\n"+len);
        if(a[0]==(byte)0xAA && a[len-1]== (byte)0x55 && crc16(body).equals(ByteArrToHex(a_1))){
            return body;
        }
        Log.i(TAG,"*** the null body is: " +  res+"\n"+ByteArrToHex(deletes(a,len))+"\n"+ByteArrToHex(a_1)+"\n"+ByteArrToHex(a)+"\n"+len);

        return deletes(a,len);
    }

}
