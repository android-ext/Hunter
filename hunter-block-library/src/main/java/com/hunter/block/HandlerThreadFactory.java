package com.hunter.block;

import android.os.Handler;
import android.os.HandlerThread;

/**
 *  
 * @date 2017-02-28
 */

public class HandlerThreadFactory {
    private static class Holder {
        static MonitorThread sSamplerThread = new MonitorThread("Sampler");
        static MonitorThread sLogWriterThread = new MonitorThread("MonitorLogWriter");
    }

    public static Handler getSamplerHandler() {
        return Holder.sSamplerThread.getHandler();
    }

    public static Handler getLogWriterHandler() {
        return Holder.sLogWriterThread.getHandler();
    }

    public static class MonitorThread extends HandlerThread {
        private Handler mHandler = null;

        public MonitorThread(String name) {
            super("[MOAI]Monitor-" + name);
            start();
            mHandler = new Handler(getLooper());
        }

        public Handler getHandler() {
            return mHandler;
        }
    }
}
