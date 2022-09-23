package com.tencent.monitor.fps.callbacks;


import java.util.List;

/**
 * Created by verus on 2016/12/26.
 */

public class DynamicAvgFrameCallback extends BaseFrameCallback {
    private long mStopResult = 0;

    public DynamicAvgFrameCallback() {
        super();
    }

    @Override
    protected void onDoFrame(long frameTimeNanos) {

    }

    @Override
    protected void onStop(List<Long> frameCollect) {
        mStopResult = getFps();
    }

    /**
     * @return 返回 -1代表无效值
     */
    public long getFps() {
        if (!isStarted()) {
            return mStopResult > 0 ? mStopResult : -1;
        }
        if (!mFrameCollect.isEmpty()) {
            long result = getSampleData(mFpsConfig, mFrameCollect);
            mFrameCollect.clear();
            return result;
        }
        return -1;
    }
}
