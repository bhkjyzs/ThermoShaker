package com.example.thermoshaker.serial;

import static com.example.thermoshaker.serial.DataUtils.ByteArrToHex;

import com.example.thermoshaker.base.MyApplication;
import com.licheedev.myutils.LogPlus;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class SerialPortUtil {
    private static final String TAG = "SerialPortUtils";
    private static BlockingQueue queue = new LinkedBlockingQueue();
    private static final ExecutorService executorService = new ThreadPoolExecutor(2, 3, 10, TimeUnit.MILLISECONDS, queue);
    private static final OutputStream outputStream = MyApplication.getSerialPort().getOutputStream();
    private static final InputStream inputStream = MyApplication.getSerialPort().getInputStream();



    /**
     * 发送数据
     * 通过串口，发送数据到单片机
     * 同时接收应答数据，判断数据是否正确
     *
     * @param sendData 要发送的数据
     */
    public static byte[] sendSerialPort(final byte[] sendData) {
        LogPlus.i("test", "开始线程");

        if (outputStream == null || inputStream == null) {
            return null;
        }
        FutureTask<byte[]> futureTask = new FutureTask<>(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
//                String sdata = ByteUtil.getSum16(sendData,sendData.length);
                LogPlus.i(TAG, "发送数据"+ByteArrToHex(sendData));
                outputStream.write(sendData);
//                LogPlus.i(TAG, "发送数据"+"   "+sdata);
//                outputStream.write(ByteUtil.hex2byte(sdata));
                outputStream.flush();


                Thread.sleep(100); //等待机器处理数据并返回
                byte[] readData = new byte[1024];
                for (int i = 0; i < 10; i++) {
                    LogPlus.i(TAG, "接收数据inputStream.available() "+inputStream.available());

                    if (inputStream.available() <= 0) {
                        Thread.sleep(200);
                    } else {
                        break;
                    }

                }

                if (inputStream.available() > 0) {
                    int size = inputStream.read(readData); //阻塞读取
                    if (size > 0) {
                        LogPlus.i(TAG, "接收数据"+ByteArrToHex(readData));
                        byte[] res = DataUtils.isChecked(readData, size);
                        if (res != null) {
                            LogPlus.i(TAG, "*** the data is ok: " + ByteArrToHex(res));
                            return res;
                        } else {
                            LogPlus.e(TAG, "*** the data is destroyed: ");

                        }
                    }
                }



                LogPlus.i(TAG, "线程超时");
                return null;
            }
        });
        try {
            executorService.submit(futureTask);
            byte[] result = futureTask.get();
            return result;
        } catch (InterruptedException e) {
            LogPlus.i(TAG, "线程中断中途退出");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
    }

    /**
     * 发送数据
     * 通过串口，发送数据到单片机
     * 同时接收应答数据，不判断数据是否正确，超时退出
     *
     * @param sendData 要发送的数据
     */
    public static byte[] sendSerialPort_1(final byte[] sendData,int time) {
        LogPlus.i("test", "开始线程");
        final OutputStream outputStream = MyApplication.getSerialPort().getOutputStream();
        final InputStream inputStream = MyApplication.getSerialPort().getInputStream();
        //final ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (outputStream == null || inputStream == null) {
            return null;
        }
        FutureTask<byte[]> futureTask = new FutureTask<>(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                LogPlus.i("test", "发送数据");
                outputStream.write(sendData);
                outputStream.flush();
//                if(time==10000){
                    Thread.sleep(1000); //等待机器处理数据并返回
//                }else {
//                    Thread.sleep(300); //等待机器处理数据并返回
//
//                }

                int dely = time / 20;

                byte[] readData = new byte[1024];
                for (int i = 0; i < 10; i++) {
                    if (inputStream.available() <= 0) {
                        Thread.sleep(100);
                    } else {
                        break;
                    }
                }
                if (inputStream.available() > 0) {
                    int size = inputStream.read(readData); //阻塞读取
                    if (size > 0) {
                        LogPlus.i("test", "接收数据");
                        return readData;
                    }
                }
                LogPlus.i(TAG, "线程超时");
                return null;
            }
        });
        try {
            //new Thread(futureTask).start();
            executorService.submit(futureTask);
            byte[] result = futureTask.get();
            //byte[] result = futureTask.get();
            LogPlus.i(TAG, "线程执行结束");
            return result;
        } catch (InterruptedException e) {
            LogPlus.i(TAG, "线程中断中途退出");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

