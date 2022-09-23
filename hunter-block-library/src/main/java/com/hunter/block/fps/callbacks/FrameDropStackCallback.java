package com.tencent.monitor.fps.callbacks;

import java.util.List;

import com.hunter.block.MonitorLogWriter;
import com.hunter.block.Utils;
import com.tencent.monitor.fps.BlockInfo;
import com.tencent.monitor.fps.Calculation;
import com.tencent.monitor.fps.FpsConfig;
import com.tencent.monitor.sampler.CpuSampler;
import com.tencent.monitor.sampler.StackSampler;

/**
 *  
 * @date 2017-04-24
 */

public class FrameDropStackCallback extends BaseFrameCallback {
    private static final String TAG = "FrameDropStackCallback";
    private StackSampler mStackSampler;
    private CpuSampler mCpuSampler;
    private long mLastFrameTimeInNs = 0;
    private long mLastFrameTimeInMs = 0;
    private StringBuilder mSaveInfoBuilder = new StringBuilder();

    public FrameDropStackCallback() {
        super();
        mNeedFrameCollect = false;
    }

    @Override
    public void setFpsConfig(FpsConfig fpsConfig) {
        super.setFpsConfig(fpsConfig);
        mStackSampler = new StackSampler(Thread.currentThread(), fpsConfig.getDropStackSampleIntervalInMs());
        mStackSampler.setSampleDelay((long) (fpsConfig.getDeviceRefreshRateInMs() + 0.5 + 2)); // 延迟一帧开始取样，多延迟2ms
        if (fpsConfig.isOpenCpuSample()) {
            mCpuSampler = new CpuSampler(fpsConfig.getCpuSampleInterval(), fpsConfig.getCpuDelayInterval());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStackSampler.start();
        if (mCpuSampler != null) {
            mCpuSampler.start();
        }
    }

    @Override
    protected void onDoFrame(long frameTimeNanos) {
        if (mLastFrameTimeInNs != 0 && mLastFrameTimeInMs != 0) {
            saveInfo(frameTimeNanos);
        }

        mLastFrameTimeInNs = frameTimeNanos;
        mLastFrameTimeInMs = System.currentTimeMillis();
        // 每一帧都重新开始取样，因为sampleDelay微大于一帧的时间，所以如果没有丢帧的话，就不会收集堆栈信息
        mStackSampler.reStart();
    }

    private void saveInfo(long frameTimeNanos) {
        long endSampleTimeInMs = System.currentTimeMillis();
        int dropCount = Calculation.droppedCount(mLastFrameTimeInNs, frameTimeNanos, mFpsConfig.getDeviceRefreshRateInMs());
        if (dropCount > mFpsConfig.getMinDropCountToLog()) {
            List<String> stacks = mStackSampler.getThreadStackEntries(mLastFrameTimeInMs, endSampleTimeInMs);
            if (stacks.size() > 0) {
                String tag = Utils.generateTag();
                mSaveInfoBuilder.setLength(0);
                mSaveInfoBuilder.append(tag).append(Utils.LINE_SEPARATOR);
                mSaveInfoBuilder.append("dropped time: ").append(endSampleTimeInMs - mLastFrameTimeInMs);
                mSaveInfoBuilder.append(Utils.AREA_SEPARATOR);
                mSaveInfoBuilder.append(stacks.get(0));
                for (int i = 1; i < stacks.size(); i++) {
                    mSaveInfoBuilder.append(Utils.LINE_SEPARATOR);
                    mSaveInfoBuilder.append(stacks.get(i));
                }
                if (mCpuSampler != null) {
                    BlockInfo blockInfo = new BlockInfo();
                    blockInfo.setTimeInfo(mLastFrameTimeInMs, endSampleTimeInMs);
                    if (!stacks.isEmpty()) {
                        blockInfo.setCpuBusy(mCpuSampler.isCpuBusy(mLastFrameTimeInMs, endSampleTimeInMs));
                        blockInfo.setCpuRateInfo(mCpuSampler.getCpuRateInfo());
                    }
                    if (mFpsConfig.isOpenOtherStackSample()) {
                        MonitorLogWriter.saveDropFrameInfo(
                                blockInfo.toString() + Utils.LINE_SEPARATOR + mSaveInfoBuilder.toString(),
                                mStackSampler.getOtherThreadStack(tag).toString());
                    } else {
                        MonitorLogWriter.saveDropFrameInfo(
                                blockInfo.toString() + Utils.LINE_SEPARATOR + mSaveInfoBuilder.toString(),
                                "");
                    }
                } else {
                    if (mFpsConfig.isOpenOtherStackSample()) {
                        MonitorLogWriter.saveDropFrameInfo(mSaveInfoBuilder.toString(),
                                mStackSampler.getOtherThreadStack(tag).toString());
                    } else {
                        MonitorLogWriter.saveDropFrameInfo(mSaveInfoBuilder.toString(),
                                "");
                    }
                }
            }
        }
    }

    @Override
    protected void onStop(List<Long> frameCollect) {
        if (mCpuSampler != null) {
            mCpuSampler.stop();
        }
        mStackSampler.stop();
        mLastFrameTimeInNs = 0;
        mLastFrameTimeInMs = 0;
    }
}
