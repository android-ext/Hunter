package com.tencent.monitor.fps;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Calculation {
    public enum Metric {
        GOOD, BAD, MEDIUM
    }

    public static List<Integer> getDroppedSet(FpsConfig fpsConfig, List<Long> dataSet) {
        List<Integer> droppedSet = new ArrayList<>();
        long start = -1;
        for (Long value : dataSet) {
            if (start == -1) {
                start = value;
                continue;
            }

            int droppedCount = droppedCount(start, value, fpsConfig.getDeviceRefreshRateInMs());
            if (droppedCount > 0) {
                droppedSet.add(droppedCount);
            }
            start = value;
        }
        return droppedSet;
    }

    public static int droppedCount(long start, long end, float devRefreshRate) {
        int count = 0;
        long diffNs = end - start;

        long diffMs = TimeUnit.MILLISECONDS.convert(diffNs, TimeUnit.NANOSECONDS);
        long dev = Math.round(devRefreshRate);
        if (diffMs > dev && dev > 0) {
            long droppedCount = (diffMs / dev);
            count = (int) droppedCount;
        }

        return count;
    }

    public static AbstractMap.SimpleEntry<Metric, Long> calculateMetric(FpsConfig fpsConfig,
                                                                        List<Long> dataSet,
                                                                        List<Integer> droppedSet) {
        long timeInNS = dataSet.get(dataSet.size() - 1) - dataSet.get(0);
        long size = getNumberOfFramesInSet(timeInNS, fpsConfig);

        //metric
        int runningOver = 0;
        // total dropped
        int dropped = 0;

        for (Integer k : droppedSet) {
            dropped += k;
            if (k >= 2) {
                runningOver += k;
            }
        }

        float multiplier = fpsConfig.getRefreshRate() / size;
        float answer = multiplier * (size - dropped);
        long realAnswer = Math.round(answer);

        // calculate metric
        float percentOver = (float) runningOver / (float) size;
        Metric metric = Metric.GOOD;
        if (percentOver >= fpsConfig.redFlagPercentage) {
            metric = Metric.BAD;
        } else if (percentOver >= fpsConfig.yellowFlagPercentage) {
            metric = Metric.MEDIUM;
        }

        return new AbstractMap.SimpleEntry<Metric, Long>(metric, realAnswer);
    }

    protected static long getNumberOfFramesInSet(long realSampleLengthNs, FpsConfig fpsConfig) {
        float realSampleLengthMs = TimeUnit.MILLISECONDS.convert(realSampleLengthNs, TimeUnit.NANOSECONDS);
        float size = realSampleLengthMs / fpsConfig.getDeviceRefreshRateInMs();
        return Math.round(size);
    }

}
