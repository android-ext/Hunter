package com.hunter.block;


import java.util.HashMap;

import com.tencent.monitor.fps.FpsConfig;
import com.tencent.monitor.fps.SimpleStackPool;
import com.tencent.monitor.fps.callbacks.BaseFrameCallback;
import com.tencent.monitor.fps.callbacks.DynamicAvgFrameCallback;
import com.tencent.monitor.fps.callbacks.FrameDropStackCallback;
import com.tencent.monitor.fps.callbacks.GlobalFrameCallback;

/**
 * Created by verus on 2016/12/23.
 *
 * 参考文档: https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653579565&idx=1&sn=ecc33d3675a8aef7c3dcc6b20e53989c&chksm=84b3bb2ab3c4323c16d41332c6906d18144ba2afce47fea79981eadfaea3d87c2e332a36c6af&scene=0#rd
 *
 *     var fpsMonitor: FpsMonitor? = null
 *
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         val builder: FpsConfig.Builder = FpsConfig.Builder(activity)
 *         fpsMonitor = MonitorService.getDropStackFpsMonitor(builder)
 *         // 需要输出卡顿时其余线程的信息
 *         // builder.setOpenOtherStackSample(true);
 *         fpsMonitor?.start()
 *
 *         MonitorLogWriter.setDelegate(object : MonitorLogWriter.LogWriterDelegate {
 *             override fun saveDropFrameInfo(str: String, otherThreadStack: String?) {
 *                 // str 卡顿时主线程信息 otherThreadStack 卡顿时其余线程信息，默认为""
 *                 Log.i("@@@", "str: $str")
 *             }
 *
 *             override fun debug(s: String?, s1: String?) {
 *                 //debug信息
 *             }
 *         })
 *     }
 *
 *     override fun onPause() {
 *         super.onPause()
 *         MonitorService.stopMonitor(fpsMonitor);
 *     }
 *
 */

public class MonitorService {


    private static HashMap<BaseFrameCallbackType, SimpleStackPool<BaseFrameCallback>> sPoolHashMap = new HashMap<>();

    private static SimpleStackPool<BaseFrameCallback> getSimpleStackPool(BaseFrameCallbackType type) {
        SimpleStackPool<BaseFrameCallback> pool = sPoolHashMap.get(type);
        if (pool == null) {
            switch (type) {
                case DynamicAvg:
                case DropStack:
                case Global:
                    pool = new SimpleStackPool<>();
                    break;
            }
            sPoolHashMap.put(type, pool);
        }
        return pool;
    }

    private static BaseFrameCallback getFrameCallback(BaseFrameCallbackType type) {
        SimpleStackPool<? extends BaseFrameCallback> pool = getSimpleStackPool(type);
        BaseFrameCallback frameCallback = pool.acquire();
        if (frameCallback == null) {
            switch (type) {
                case DynamicAvg:
                    frameCallback = new DynamicAvgFrameCallback();
                    break;
                case DropStack:
                    frameCallback = new FrameDropStackCallback();
                    break;
                case Global:
                    frameCallback = new GlobalFrameCallback();
                    break;
            }
            return frameCallback;
        }
        return frameCallback;
    }

    public static FpsMonitor getDynamicAvgFpsMonitor(FpsConfig.Builder builder) {
        return getFpsMonitor(builder)
                .setFrameCallback(getFrameCallback(BaseFrameCallbackType.DynamicAvg));
    }

    public static FpsMonitor getDropStackFpsMonitor(FpsConfig.Builder builder) {
        return getFpsMonitor(builder)
                .setFrameCallback(getFrameCallback(BaseFrameCallbackType.DropStack));
    }

    private static FpsMonitor getFpsMonitor(FpsConfig.Builder builder) {
        return new FpsMonitor(builder.build());
    }

    /**
     * @return 返回 -1代表无效值
     */
    public static long stopMonitor(FpsMonitor monitor) {
        if (monitor != null && monitor.isStarted()) {
            monitor.stop();
            BaseFrameCallback frameCallback = monitor.getFrameCallback();
            getSimpleStackPool(BaseFrameCallbackType.typeOf(frameCallback)).release(frameCallback);
            return monitor.getRecord();
        }
        return -1;
    }

    enum BaseFrameCallbackType {
        Global,
        DynamicAvg,
        DropStack;

        public static BaseFrameCallbackType typeOf(BaseFrameCallback frameCallback) {
            if (frameCallback instanceof GlobalFrameCallback) {
                return Global;
            } else if (frameCallback instanceof DynamicAvgFrameCallback) {
                return DynamicAvg;
            } else if (frameCallback instanceof FrameDropStackCallback) {
                return DropStack;
            }
            throw new RuntimeException("undefine Type");
        }
    }
}
