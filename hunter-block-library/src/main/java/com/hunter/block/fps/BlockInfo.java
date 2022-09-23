package com.tencent.monitor.fps;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunter.block.Utils;

/**
 * @date 2017-02-28
 */

public class BlockInfo implements Parcelable {
    public static final String KV = " = ";
    public static final String STRING = "**";
    public static final String COLON = ":";
    public static final String KEY_CPU_BUSY = "cpu-busy";
    public static final String KEY_CPU_RATE = "cpu-rate";
    public static final String KEY_TIME_COST = "time-cost";

    private boolean mIsCpuBusy;
    private String mCpuRateInfo;
    private long mTimeStart;
    private long mTimeEnd;

    private StringBuilder mSampleBuilder = null;
    private StringBuilder mTimeBuilder = null;
    private StringBuilder mCpuBuilder = null;

    public void setCpuBusy(boolean cpuBusy) {
        mIsCpuBusy = cpuBusy;
    }

    public void setCpuRateInfo(String cpuRateInfo) {
        mCpuRateInfo = cpuRateInfo;
    }

    public boolean isCpuBusy() {
        return mIsCpuBusy;
    }

    public String getCpuRateInfo() {
        return mCpuRateInfo;
    }

    public void setTimeInfo(long startTime, long endTime) {
        mTimeStart = startTime;
        mTimeEnd = endTime;
    }

    public long getTimeStart() {
        return mTimeStart;
    }

    public long getTimeEnd() {
        return mTimeEnd;
    }

    public String buildSampleInfo() {
        if (mSampleBuilder == null) {
            mSampleBuilder = new StringBuilder();
        }
        return mSampleBuilder.toString();
    }

    public String buildTimeInfo() {
        if (mTimeBuilder == null) {
            mTimeBuilder = new StringBuilder();
            String separator = Utils.LINE_SEPARATOR;
            mTimeBuilder.append(KEY_TIME_COST).append(KV).append(mTimeEnd - mTimeStart).append(separator);
        }
        return mTimeBuilder.toString();
    }

    public String buildCpuInfo() {
        if (mCpuBuilder == null) {
            mCpuBuilder = new StringBuilder();
            String separator = Utils.LINE_SEPARATOR;
            mCpuBuilder.append(KEY_CPU_BUSY).append(KV).append(mIsCpuBusy).append(separator);
            mCpuBuilder.append(KEY_CPU_RATE).append(COLON).append(separator);
            mCpuBuilder.append(mCpuRateInfo).append(separator);
        }
        return mCpuBuilder.toString();
    }

    @Override
    public String toString() {
        return buildSampleInfo() + buildTimeInfo() + buildCpuInfo();
    }

    public static final Creator<BlockInfo> CREATOR = new Creator<BlockInfo>() {
        @Override
        public BlockInfo createFromParcel(Parcel source) {
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.mTimeStart = source.readLong();
            blockInfo.mTimeEnd = source.readLong();
            blockInfo.mIsCpuBusy = source.readInt() == 1;
            blockInfo.mCpuRateInfo = source.readString();
            return blockInfo;
        }

        @Override
        public BlockInfo[] newArray(int size) {
            return new BlockInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTimeStart);
        dest.writeLong(mTimeEnd);
        dest.writeInt(mIsCpuBusy ? 1 : 0);
        dest.writeString(mCpuRateInfo);
    }
}
