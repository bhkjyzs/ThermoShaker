package com.example.thermoshaker.serial;

import android.annotation.SuppressLint;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.MainThread;

import com.example.thermoshaker.base.MyApplication;
import com.example.thermoshaker.serial.message.LogManager;
import com.example.thermoshaker.serial.message.SendMessage;
import com.licheedev.myutils.LogPlus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static com.example.thermoshaker.serial.DataUtils.ByteArrToHex;


public class SerialPortUtil {
    private static final String TAG = "SerialPortUtils";
    private static BlockingQueue queue = new LinkedBlockingQueue();
    private static final ExecutorService executorService = new ThreadPoolExecutor(2, 3, 10, TimeUnit.MILLISECONDS, queue);
    private static final OutputStream outputStream = SerialPortManager.instance().mSerialPort.getOutputStream();
    private static final InputStream inputStream = SerialPortManager.instance().mSerialPort.getInputStream();


    /**
     * 发送数据
     * 通过串口，发送数据到单片机
     * 同时接收应答数据，判断数据是否正确
     *
     * @param datas 要发送的数据
     */
    @SuppressLint("CheckResult")
    public static byte[] sendSerialPort(final byte[] datas) {
        Log.i(TAG, "开始线程");
        final byte[][] ReturnDate = {new byte[1024]};
        if (outputStream == null || inputStream == null) {
            return null;
        }
         HandlerThread mWriteThread;
         Scheduler mSendScheduler;
        mWriteThread = new HandlerThread("write-thread");
        mWriteThread.start();
        mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());
        Observable<Object> objectObservable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    outputStream.write(datas);
                    emitter.onNext(new Object());
                } catch (Exception e) {

                    LogPlus.e("发送：" + ByteArrToHex(datas) + " 失败", e);

                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                        return;
                    }
                }
                emitter.onComplete();
            }
        });
        objectObservable.subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                byte[] received = new byte[1024];
                int size;

                Log.e(TAG,"开始读线程");

                while (true) {

                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    try {

                        int available = inputStream.available();

                        if (available > 0) {
                            size = inputStream.read(received);
                            if (size > 0) {
                                byte[] res = DataUtils.isChecked(received, size);
                                if (res != null) {
                                    Log.d("===", "*** the data is ok: " + ByteArrToHex(res));
                                    //处理逻辑
                                    ReturnDate[0] = res;
                                } else {
                                    Log.d("===", "*** the data is destroyed: ");
                                    ReturnDate[0] = null;
                                }

                            }
                        } else {
                            // 暂停一点时间，免得一直循环造成CPU占用率过高
                            SystemClock.sleep(1);
                        }
                    } catch (IOException e) {
                        Log.e(TAG,"读取数据失败", e);
                    }
                    //Thread.yield();
                }

                Log.e(TAG,"结束读进程");
            }

            @Override
            public void onError(Throwable e) {
                LogPlus.e("发送失败", e);
                ReturnDate[0] = null;
            }

            @Override
            public void onComplete() {

            }
        });
        return ReturnDate[0];
    }

//    /**
//     * 发送数据
//     * 通过串口，发送数据到单片机
//     * 同时接收应答数据，不判断数据是否正确，超时退出
//     *
//     * @param sendData 要发送的数据
//     */
//    public static byte[] sendSerialPort_1(final byte[] sendData,int time) {
//        Log.i("test", "开始线程");
//        final OutputStream outputStream = MyApp.getSerialPort().getOutputStream();
//        final InputStream inputStream = MyApp.getSerialPort().getInputStream();
//        //final ExecutorService executorService = Executors.newSingleThreadExecutor();
//        if (outputStream == null || inputStream == null) {
//            return null;
//        }
//        FutureTask<byte[]> futureTask = new FutureTask<>(new Callable<byte[]>() {
//            @Override
//            public byte[] call() throws Exception {
//                Log.i("test", "发送数据");
//                outputStream.write(sendData);
//                outputStream.flush();
////                if(time==10000){
//                    Thread.sleep(1000); //等待机器处理数据并返回
////                }else {
////                    Thread.sleep(300); //等待机器处理数据并返回
////
////                }
//
//                int dely = time / 20;
//
//                byte[] readData = new byte[1024];
//                for (int i = 0; i < 10; i++) {
//                    if (inputStream.available() <= 0) {
//                        Thread.sleep(100);
//                    } else {
//                        break;
//                    }
//                }
//                if (inputStream.available() > 0) {
//                    int size = inputStream.read(readData); //阻塞读取
//                    if (size > 0) {
//                        Log.i("test", "接收数据");
//                        return readData;
//                    }
//                }
//                Log.i(TAG, "线程超时");
//                return null;
//            }
//        });
//        try {
//            //new Thread(futureTask).start();
//            executorService.submit(futureTask);
//            byte[] result = futureTask.get();
//            //byte[] result = futureTask.get();
//            Log.i(TAG, "线程执行结束");
//            return result;
//        } catch (InterruptedException e) {
//            Log.i(TAG, "线程中断中途退出");
//            e.printStackTrace();
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


}

