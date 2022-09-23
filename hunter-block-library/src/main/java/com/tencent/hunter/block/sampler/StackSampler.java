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

import android.os.Looper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tencent.monitor.Utils;

public class StackSampler extends AbstractSampler {

    private static final int DEFAULT_MAX_ENTRY_COUNT = 100;
    private static final LinkedHashMap<Long, String> sStackMap = new LinkedHashMap<>();
    private final StringBuilder mAllThreadStackBuilder = new StringBuilder();

    private int mMaxEntryCount = DEFAULT_MAX_ENTRY_COUNT;
    private Thread mCurrentThread;

    public StackSampler(Thread thread, long sampleIntervalMillis) {
        this(thread, DEFAULT_MAX_ENTRY_COUNT, sampleIntervalMillis, (long) (sampleIntervalMillis * 0.8));
    }

    public StackSampler(Thread thread, long sampleIntervalMillis, long sampleDelay) {
        this(thread, DEFAULT_MAX_ENTRY_COUNT, sampleIntervalMillis, sampleDelay);
    }

    public StackSampler(Thread thread, int maxEntryCount, long sampleIntervalMillis, long sampleDelay) {
        super(sampleIntervalMillis, sampleDelay);
        mCurrentThread = thread;
        mMaxEntryCount = maxEntryCount;
    }

    public List<String> getThreadStackEntries(long startTimeMs, long endTimeMs) {
        List<String> result = new ArrayList<>();
        synchronized (sStackMap) {
            for (Long entryTime : sStackMap.keySet()) {
                if (startTimeMs < entryTime && entryTime < endTimeMs) {
                    result.add(Utils.TIME_FORMATTER.format(entryTime)
                            + Utils.LINE_SEPARATOR
                            + sStackMap.get(entryTime));
                }
            }
        }
        return result;
    }

    public List<String> getThreadStackEntriesAndClear() {
        List<String> result = new ArrayList<>();
        synchronized (sStackMap) {
            for (Long entryTime : sStackMap.keySet()) {
                result.add(Utils.TIME_FORMATTER.format(entryTime)
                        + Utils.LINE_SEPARATOR
                        + sStackMap.get(entryTime));
            }
            sStackMap.clear();
        }
        return result;
    }

    public void clearStackEntries() {
        synchronized (sStackMap) {
            sStackMap.clear();
        }
    }

    public StringBuilder getOtherThreadStack(String tag) {
        Set<Map.Entry<Thread, StackTraceElement[]>> entries = Thread.getAllStackTraces().entrySet();
        mAllThreadStackBuilder.setLength(0);
        if(entries.size() > 0) {
            mAllThreadStackBuilder.append(tag).append(Utils.LINE_SEPARATOR);
            for (Map.Entry entry : entries) {
                Thread thread = (Thread) entry.getKey();
                StackTraceElement[] stacks = (StackTraceElement[]) entry.getValue();
                if (thread.isAlive() && thread.getId() != Looper.getMainLooper().getThread().getId()
                        && thread.getState() != Thread.State.NEW && stacks != null && stacks.length > 0) {
                    mAllThreadStackBuilder.append(thread.getName()).append("(").append(thread.getId()).append("):").append(Utils.LINE_SEPARATOR);
                    for (StackTraceElement element : stacks) {
                        mAllThreadStackBuilder.append(element.toString()).append(Utils.LINE_SEPARATOR);
                    }
                }
            }
        }
        return mAllThreadStackBuilder;
    }

    @Override
    protected void doSample() {
        StringBuilder stringBuilder = new StringBuilder();

        for (StackTraceElement stackTraceElement : mCurrentThread.getStackTrace()) {
            stringBuilder
                    .append(stackTraceElement.toString())
                    .append(Utils.LINE_SEPARATOR);
        }

        synchronized (sStackMap) {
            if (sStackMap.size() == mMaxEntryCount && mMaxEntryCount > 0) {
                sStackMap.remove(sStackMap.keySet().iterator().next());
            }
            sStackMap.put(System.currentTimeMillis(), stringBuilder.toString());
        }
    }
}