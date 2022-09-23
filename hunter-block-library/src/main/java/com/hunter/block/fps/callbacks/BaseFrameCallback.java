package com.tencent.monitor.fps.callbacks;

import android.view.Choreographer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tencent.monitor.fps.Calculation;
import com.tencent.monitor.fps.FpsConfig;

/**
 * Created by verus on 2016/12/23.
 */

public abstract class BaseFrameCallback implements Choreographer.FrameCallback {
    private boolean mIsStarted = false;
    protected List<Long> mFrameCollect = new ArrayList<>();
    protected FpsConfig mFpsConfig;
    protected boolean mNeedFrameCollect;

    public BaseFrameCallback() {
        mNeedFrameCollect = true;
    }

    public void setFpsConfig(FpsConfig fpsConfig) {
        mFpsConfig = fpsConfig;
    }


    @Override
    public void doFrame(long frameTimeNanos) {
        if (!mIsStarted) {
            mFrameCollect.clear();
            return;
        }
        onDoFrame(frameTimeNanos);
        if (mNeedFrameCollect) {
            mFrameCollect.add(frameTimeNanos);
        }
        Choreographer.getInstance().postFrameCallback(this);
    }

    protected void onStart() {
    }

    protected abstract void onDoFrame(long frameTimeNanos);

    protected abstract void onStop(List<Long> frameCollect);


    public void start() {
        mIsStarted = true;
        Choreographer.getInstance().postFrameCallback(this);
        mFrameCollect.clear();
        onStart();
    }

    public void stop() {
        onStop(Collections.unmodifiableList(mFrameCollect));
        mFrameCollect.clear();
        mIsStarted = false;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    protected long getSampleData(FpsConfig fpsConfig, List<Long> dataSet) {
        List<Integer> droppedSet = Calculation.getDroppedSet(fpsConfig, dataSet);
        AbstractMap.SimpleEntry<Calculation.Metric, Long> answer = Calculation.calculateMetric(fpsConfig, dataSet, droppedSet);
        return answer.getValue();
    }
}