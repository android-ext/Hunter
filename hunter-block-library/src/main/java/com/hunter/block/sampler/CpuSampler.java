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

import android.util.Log;

import com.hunter.block.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class CpuSampler extends AbstractSampler {

    private static final String TAG = "CpuSampler";
    private static final int BUFFER_SIZE = 1024;

    private final int BUSY_TIME;
    private static final int MAX_ENTRY_COUNT = 10;

    private final LinkedHashMap<Long, String> mCpuInfoEntries = new LinkedHashMap<>();
    private int mPid = 0;
    private long mUserLast = 0;
    private long mSystemLast = 0;
    private long mIdleLast = 0;
    private long mIoWaitLast = 0;
    private long mTotalLast = 0;
    private long mAppCpuTimeLast = 0;

    public CpuSampler(long sampleInterval, long delayInterval) {
        super(sampleInterval, delayInterval);
        BUSY_TIME = (int) (mSampleInterval * 1.2f);
    }

    @Override
    public void start() {
        reset();
        super.start();
    }

    /**
     * Get cpu rate information
     *
     * @return string show cpu rate information
     */
    public String getCpuRateInfo() {
        StringBuilder sb = new StringBuilder();
        synchronized (mCpuInfoEntries) {
            for (Map.Entry<Long, String> entry : mCpuInfoEntries.entrySet()) {
                long time = entry.getKey();
                sb.append(Utils.TIME_FORMATTER.format(time))
                        .append(' ')
                        .append(entry.getValue())
                        .append(Utils.LINE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    public boolean isCpuBusy(long start, long end) {
        if (end - start > mSampleInterval) {
            long s = start - mSampleInterval;
            long e = start + mSampleInterval;
            long last = 0;
            synchronized (mCpuInfoEntries) {
                for (Map.Entry<Long, String> entry : mCpuInfoEntries.entrySet()) {
                    long time = entry.getKey();
                    if (s < time && time < e) {
                        if (last != 0 && time - last > BUSY_TIME) {
                            return true;
                        }
                        last = time;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void doSample() {
        BufferedReader cpuReader = null;
        BufferedReader pidReader = null;

        try {
            cpuReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), BUFFER_SIZE);
            // 文件/proc/stat的第一行表示CPU的总体情况
            // user             处于用户态的运行时间，不包含 nice值为负的进程
            // nice             nice值为负的进程所占用的CPU时间
            // system           处于核心态的运行时间
            // idle             除IO等待时间以外的其它等待时间
            // iowait           从系统启动开始累计到当前时刻，IO等待时间
            // irq              硬中断时间
            // irq              软中断时间
            // stealstolen      一个其他的操作系统运行在虚拟环境下所花费的时间
            // guest            这是在Linux内核控制下为客户操作系统运行虚拟CPU所花费的时间
            String cpuRate = cpuReader.readLine();
            if (cpuRate == null) {
                cpuRate = "";
            }

            if (mPid == 0) {
                mPid = android.os.Process.myPid();
            }

            // 文件/proc/pid/stat包含了某一进程所有的活动的信息,split(" ")后，与cpu使用率有关的值是(单位为jiffies)：
            // index = 13: utime    该任务在用户态运行的时间
            // index = 14: stime    该任务在核心态运行的时间
            // index = 15: cutime   所有已死线程在用户态运行的时间
            // index = 16: cstime   所有已死在核心态运行的时间
            pidReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + mPid + "/stat")), BUFFER_SIZE);
            String pidCpuRate = pidReader.readLine();
            if (pidCpuRate == null) {
                pidCpuRate = "";
            }

            parse(cpuRate, pidCpuRate);
        } catch (Throwable throwable) {
            Log.e(TAG, "doSample: ", throwable);
        } finally {
            Utils.quietClose(cpuReader);
            Utils.quietClose(pidReader);
        }
    }

    private void reset() {
        mUserLast = 0;
        mSystemLast = 0;
        mIdleLast = 0;
        mIoWaitLast = 0;
        mTotalLast = 0;
        mAppCpuTimeLast = 0;
    }

    private void parse(String cpuRate, String pidCpuRate) {
        String[] cpuInfoArray = cpuRate.split(" ");
        if (cpuInfoArray.length < 9) {
            return;
        }
        long user = Long.parseLong(cpuInfoArray[2]);
        long nice = Long.parseLong(cpuInfoArray[3]);
        long system = Long.parseLong(cpuInfoArray[4]);
        long idle = Long.parseLong(cpuInfoArray[5]);
        long ioWait = Long.parseLong(cpuInfoArray[6]);
        long total = user + nice + system + idle + ioWait
                + Long.parseLong(cpuInfoArray[7])
                + Long.parseLong(cpuInfoArray[8]);

        String[] pidCpuInfoList = pidCpuRate.split(" ");
        if (pidCpuInfoList.length < 17) {
            return;
        }

        long appCpuTime = Long.parseLong(pidCpuInfoList[13])
                + Long.parseLong(pidCpuInfoList[14])
                + Long.parseLong(pidCpuInfoList[15])
                + Long.parseLong(pidCpuInfoList[16]);

        long totalTime = total - mTotalLast;
        if (mTotalLast != 0 && totalTime > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            long idleTime = idle - mIdleLast;

            stringBuilder
                    .append("cpu:")
                    .append((totalTime - idleTime) * 100L / totalTime)
                    .append("% ")
                    .append("app:")
                    .append((appCpuTime - mAppCpuTimeLast) * 100L / totalTime)
                    .append("% ")
                    .append("[")
                    .append("user:").append((user - mUserLast) * 100L / totalTime)
                    .append("% ")
                    .append("system:").append((system - mSystemLast) * 100L / totalTime)
                    .append("% ")
                    .append("ioWait:").append((ioWait - mIoWaitLast) * 100L / totalTime)
                    .append("% ]");

            synchronized (mCpuInfoEntries) {
                mCpuInfoEntries.put(System.currentTimeMillis(), stringBuilder.toString());
                if (mCpuInfoEntries.size() > MAX_ENTRY_COUNT) {
                    mCpuInfoEntries.remove(mCpuInfoEntries.keySet().iterator().next());
                }
            }
        }
        mUserLast = user;
        mSystemLast = system;
        mIdleLast = idle;
        mIoWaitLast = ioWait;
        mTotalLast = total;

        mAppCpuTimeLast = appCpuTime;
    }
}