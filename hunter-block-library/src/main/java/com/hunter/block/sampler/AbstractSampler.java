/*
 * Copyright (C) 2016 MarkZhai (http://zhaiyifan.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.monitor.sampler;

import java.util.concurrent.atomic.AtomicBoolean;

import com.hunter.block.HandlerThreadFactory;

/**
 * {@link AbstractSampler} sampler defines sampler work flow.
 */
abstract class AbstractSampler {

    private static final int DEFAULT_SAMPLE_INTERVAL = 300;

    protected AtomicBoolean mIsInProgress = new AtomicBoolean(false);
    protected long mSampleInterval = DEFAULT_SAMPLE_INTERVAL;
    protected long mSampleDelay = 0;

    private Runnable mSampleRunnable = new Runnable() {
        @Override
        public void run() {
            doSample();

            if (mIsInProgress.get()) {
                HandlerThreadFactory.getSamplerHandler().postDelayed(mSampleRunnable, mSampleInterval);
            }
        }
    };

    public AbstractSampler(long sampleInterval, long sampleDelay) {
        if (sampleInterval == 0) {
            sampleInterval = DEFAULT_SAMPLE_INTERVAL;
        }
        mSampleInterval = sampleInterval;
        mSampleDelay = sampleDelay;
    }

    public void setSampleDelay(long sampleDelay) {
        mSampleDelay = sampleDelay;
    }

    public long getSampleDelay() {
        return mSampleDelay;
    }

    public long getSampleInterval() {
        return mSampleInterval;
    }

    public void start() {
        if (mIsInProgress.get()) {
            return;
        }
        mIsInProgress.set(true);

        HandlerThreadFactory.getSamplerHandler().removeCallbacks(mSampleRunnable);
        HandlerThreadFactory.getSamplerHandler().postDelayed(mSampleRunnable, mSampleDelay);
    }

    public void reStart(){
        mIsInProgress.set(true);
        HandlerThreadFactory.getSamplerHandler().removeCallbacks(mSampleRunnable);
        HandlerThreadFactory.getSamplerHandler().postDelayed(mSampleRunnable, mSampleDelay);
    }

    public void stop() {
        if (!mIsInProgress.get()) {
            return;
        }
        mIsInProgress.set(false);
        HandlerThreadFactory.getSamplerHandler().removeCallbacks(mSampleRunnable);
    }

    abstract void doSample();
}
