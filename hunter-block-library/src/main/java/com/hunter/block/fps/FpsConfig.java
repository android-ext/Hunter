package com.tencent.monitor.fps;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import com.hunter.block.Utils;

/**
 * Created by brianplummer on 8/29/15.
 */
public class FpsConfig implements Cloneable {
    public float redFlagPercentage = 0.2f;
    public float yellowFlagPercentage = 0.05f;

    private float refreshRate;
    private IFpsUi fpsUi;
    private Context context;

    private long fpsSampleIntervalInMs; // 计算fps信息的取样时间
    private long dropStackSampleIntervalInMs; // 丢帧堆栈信息取样时间
    private int minDropCountToLog; //丢帧次数大于minDropCountToLog，则写入日志
    private boolean isOpenCpuSample;
    private boolean isOpenOtherStackSample;
    private long cpuSampleInterval;
    private long cpuDelayInterval;

    FpsConfig() {
    }

    public long getFpsCalculateSampleInNs() {
        return TimeUnit.NANOSECONDS.convert(fpsSampleIntervalInMs, TimeUnit.MILLISECONDS);
    }


    public float getDeviceRefreshRateInMs() {
        return 1000 / refreshRate;
    }

    public long getDeviceRefreshRateInNs() {
        float value = getDeviceRefreshRateInMs() * 1000000f;
        return (long) value;
    }

    public Context getContext() {
        return context;
    }

    public IFpsUi getFpsUi() {
        return fpsUi;
    }

    public float getRefreshRate() {
        return refreshRate;
    }

    public long getDropStackSampleIntervalInMs() {
        return dropStackSampleIntervalInMs;
    }

    public int getMinDropCountToLog() {
        return minDropCountToLog;
    }

    public boolean isOpenCpuSample() {
        return isOpenCpuSample;
    }

    public boolean isOpenOtherStackSample() {
        return isOpenOtherStackSample;
    }

    public long getCpuSampleInterval() {
        return cpuSampleInterval;
    }

    public long getCpuDelayInterval() {
        return cpuDelayInterval;
    }

    @Override
    public FpsConfig clone() {
        FpsConfig config;
        try {
            config = (FpsConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            config = new FpsConfig();
            e.printStackTrace();
        }
        return config;
    }

    public static class Builder {
        public final static long FPS_SAMPLE_INTERVAL_MS = 736;
        public final static long DROP_STACK_SAMPLE_INTERVAL_MS = 52;
        public final static int MIN_DROP_COUNT_TO_LOG = 4; // 默认值是5
        public final static boolean IS_OPEN_CPU_STACK = false;
        public final static boolean IS_OPEN_OTHER_STACK_SAMPLE = false;
        public final static int DEFAULT_CPU_SAMPLE_INTERVAL = 600;
        public final static int DEFAULT_CPU_DELAY_INTERVAL = 100;

        private Context context;
        private IFpsUi fpsUi;

        private long fpsSampleIntervalMs = FPS_SAMPLE_INTERVAL_MS; // 计算fps的取样时间
        private long dropStackSampleIntervalMs = DROP_STACK_SAMPLE_INTERVAL_MS; // 堆栈信息取样时间, 默认每丢3帧取一次样 16.6 * 3
        private int minDropCountToLog = MIN_DROP_COUNT_TO_LOG; // 默认丢5帧就写入日志文件
        private boolean isOpenCpuStack = IS_OPEN_CPU_STACK; //是否开启CPU监控
        private boolean isOpenOtherStackSample = IS_OPEN_OTHER_STACK_SAMPLE; //是否获取卡顿时其他线程的堆栈
        private long cpuSampleInterval = DEFAULT_CPU_SAMPLE_INTERVAL; //CPU监控间隔
        private long cpuDelayInterval = DEFAULT_CPU_DELAY_INTERVAL;  //CPU监控启动延时

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setFpsUi(IFpsUi fpsUi) {
            this.fpsUi = fpsUi;
            return this;
        }

        public Builder setFpsSampleIntervalMs(long fpsSampleIntervalInMs) {
            this.fpsSampleIntervalMs = fpsSampleIntervalInMs;
            return this;
        }

        public Builder setDropStackSampleIntervalMs(long dropStackSampleIntervalMs) {
            this.dropStackSampleIntervalMs = dropStackSampleIntervalMs;
            return this;
        }

        public Builder setMinDropCountToLog(int minDropCountToLog) {
            this.minDropCountToLog = minDropCountToLog;
            return this;
        }

        public Builder setOpenCpuStack(boolean openCpuStack) {
            isOpenCpuStack = openCpuStack;
            return this;
        }

        public Builder setCpuSampleInterval(long cpuSampleInterval) {
            this.cpuSampleInterval = cpuSampleInterval;
            return this;
        }

        public Builder setCpuDelayInterval(long cpuDelayInterval) {
            this.cpuDelayInterval = cpuDelayInterval;
            return this;
        }

        public Builder setOpenOtherStackSample(boolean openOtherStackSample) {
            this.isOpenOtherStackSample = openOtherStackSample;
            return this;
        }

        public FpsConfig build() {
            FpsConfig fpsConfig = new FpsConfig();
            fpsConfig.context = context;
            fpsConfig.refreshRate = Utils.getRefreshRate(context);
            fpsConfig.fpsUi = fpsUi;
            fpsConfig.fpsSampleIntervalInMs = fpsSampleIntervalMs;
            fpsConfig.dropStackSampleIntervalInMs = dropStackSampleIntervalMs;
            fpsConfig.minDropCountToLog = minDropCountToLog;
            fpsConfig.isOpenCpuSample = isOpenCpuStack;
            fpsConfig.isOpenOtherStackSample = isOpenOtherStackSample;
            fpsConfig.cpuSampleInterval = cpuSampleInterval;
            fpsConfig.cpuDelayInterval = cpuDelayInterval;
            return fpsConfig;
        }
    }
}
