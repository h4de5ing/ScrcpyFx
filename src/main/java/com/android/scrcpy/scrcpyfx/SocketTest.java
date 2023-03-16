package com.android.scrcpy.scrcpyfx;

import com.android.scrcpy.scrcpyfx.change.ChangeListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SocketTest {
    private ChangeListener mListener;

    public SocketTest(ChangeListener listener) {
        mListener = listener;
        Runnable mRunnable = () -> {
            if (mSocket == null || !mSocket.isConnected()) {
                mLogger.info("尝试建立连接...");
                try {
                    mSocket = new Socket("localhost", 18000);
                    mLogger.info("建立新连接:" + mSocket);
                    CompletableFuture.runAsync(this::session);
                } catch (Exception e) {
                    mLogger.info("连接异常");
                }
            } else {
                mLogger.info("连接心跳检测:当前已经建立连接，无需重连");
            }
        };
        // 每隔3秒周期性的执行心跳检测动作。
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(mRunnable, 0, 3, TimeUnit.SECONDS);
    }

    private Socket mSocket = null;

    private final Logger mLogger = Logger.getLogger(SocketTest.class.getName());

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(System.currentTimeMillis()));
    }

    private void session() {
        DataInputStream dis;
        DataOutputStream dos;
        try {
            dis = new DataInputStream(mSocket.getInputStream());
            dos = new DataOutputStream(mSocket.getOutputStream());
            while (true) {
                dos.writeUTF("PC端时间:" + now());
                dos.flush();
                String s = dis.readUTF();
                mListener.change(s);
                mLogger.info("收到数据:" + s);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            try {
                mSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }
}
