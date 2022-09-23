package com.hunter.block;

import java.util.concurrent.atomic.AtomicBoolean;

import com.tencent.monitor.fps.FpsConfig;
import com.tencent.monitor.fps.callbacks.BaseFrameCallback;
import com.tencent.monitor.fps.callbacks.DynamicAvgFrameCallback;

/**
 *  
 * @date 2017-04-21
 */

public class FpsMonitor extends BaseMonitor {
    private FpsConfig mFpsConfig;
    private AtomicBoolean mIsStarted;
    private BaseFrameCallback mFrameCallback;

    FpsMonitor(FpsConfig fpsConfig) {
        mFpsConfig = fpsConfig;
        mIsStarted = new AtomicBoolean(false);
    }

    BaseFrameCallback getFrameCallback() {
        return mFrameCallback;
    }

    FpsMonitor setFrameCallback(BaseFrameCallback frameCallback) {
        mFrameCallback = frameCallback;
        return this;
    }

    @Override
    public void start() {
        if (isStarted()) {
            return;
        }

        mIsStarted.set(true);
        if (mFrameCallback != null) {
            mFrameCallback.setFpsConfig(mFpsConfig);
            mFrameCallback.start();
        }
    }

    @Override
    void stop() {
        if (!isStarted()) {
            return;
        }
        mIsStarted.set(false);
        if (mFrameCallback != null) {
            mFrameCallback.stop();
        }
    }

    /**
     * @return 返回 -1代表无效值
     */
    long getRecord() {
        if (mFrameCallback instanceof DynamicAvgFrameCallback) {
            return ((DynamicAvgFrameCallback) mFrameCallback).getFps();
        }
        return -1;
    }

    public boolean isStarted() {
        return mIsStarted.get();
    }
}
