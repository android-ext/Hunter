package com.hunter.block;

/**
 *  
 * @date 2017-04-25
 */

public class MonitorLogWriter {
    private final static String MOAI = "moai";
    private final static String TENCENT = "tencent";

    public interface LogWriterDelegate {
        void saveDropFrameInfo(String str, String otherThreadStack);

        void debug(String tag, String info);
    }

    private MonitorLogWriter() {
        throw new InstantiationError("Must not instantiate this class");
    }

    private static LogWriterDelegate sDelegate = null;

    public static void setDelegate(LogWriterDelegate delegate) {
        sDelegate = delegate;
    }

    /**
     * 保存丢帧信息
     *
     * @param str
     */
    public static void saveDropFrameInfo(String str, String otherThreadStack) {
        if (sDelegate != null && isContainTarget(str)) {
            sDelegate.saveDropFrameInfo(str, otherThreadStack);
        }
    }

    public static void debug(String tag, String info) {
        if (sDelegate != null) {
            sDelegate.debug(tag, info);
        }
    }

    private static boolean isContainTarget(String str) {
        return str != null && str.length() > 0 && (str.contains(MOAI) || str.contains(TENCENT));
    }
}
