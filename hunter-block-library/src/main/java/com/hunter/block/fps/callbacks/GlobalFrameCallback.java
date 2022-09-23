package com.tencent.monitor.fps.callbacks;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import com.hunter.block.Utils;
import com.tencent.monitor.fps.FpsConfig;
import com.tencent.monitor.fps.IFpsUi;
import com.tencent.monitor.ui.Foreground;

/**
 *  
 * @date 2017-04-24
 */

public class GlobalFrameCallback extends BaseFrameCallback {
    private long mStartSampleTimeInNs = 0;
    private List<Long> mFpsCollect = new ArrayList<>();
    private IFpsUi mFpsUi;

    public GlobalFrameCallback() {
        super();
    }

    @Override
    public void setFpsConfig(FpsConfig fpsConfig) {
        super.setFpsConfig(fpsConfig);
        mFpsUi = fpsConfig.getFpsUi();
    }

    private Foreground.Listener mForegroundListener = new Foreground.Listener() {
        @Override
        public void onBecameForeground() {
            if (mFpsUi != null) {
                mFpsUi.onBecameForeground();
            }
        }

        @Override
        public void onBecameBackground() {
            if (mFpsUi != null) {
                mFpsUi.onBecameBackground();
            }
        }
    };

    public void setFpsUi(IFpsUi fpsUi) {
        mFpsUi = fpsUi;
    }

    @Override
    public void onStart() {
        if (mFpsConfig.getContext() == null) {
            return;
        }
        if (mFpsUi != null && !Utils.requestOverlayPermission(mFpsConfig.getContext())) {
            Foreground.init((Application) mFpsConfig.getContext().getApplicationContext())
                    .addListener(mForegroundListener);
            mFpsUi.start();
        }
    }

    @Override
    protected void onStop(List<Long> frameCollect) {
        Foreground.get().removeListener(mForegroundListener);
        if (mFpsUi != null) {
            mFpsUi.stop();
        }
        mStartSampleTimeInNs = 0;
        mFpsCollect.clear();
    }

    @Override
    protected void onDoFrame(long frameTimeNanos) {
        if (mStartSampleTimeInNs == 0) {
            mStartSampleTimeInNs = frameTimeNanos;
        }

        if (isFinishedWithSample(frameTimeNanos)) {
            calculateFps(frameTimeNanos);
        }
    }

    protected void calculateFps(long frameTimeNanos) {
        mFpsCollect.add(getSampleData(mFpsConfig, mFrameCollect));
        if (mFpsUi != null) {
            int size = mFpsCollect.size();
            if (size > 1) {
                mFpsUi.onChange(mFpsCollect.get(size - 2), mFpsCollect.get(size - 1));
            } else {
                mFpsUi.onChange(mFpsConfig.getDeviceRefreshRateInNs(), mFpsCollect.get(0));
            }
        }

        // clear data
        mFrameCollect.clear();

        //reset sample timer to last frame
        mStartSampleTimeInNs = frameTimeNanos;
    }


    protected boolean isFinishedWithSample(long frameTimeNanos) {
        return frameTimeNanos - mStartSampleTimeInNs > mFpsConfig.getFpsCalculateSampleInNs();
    }
}
