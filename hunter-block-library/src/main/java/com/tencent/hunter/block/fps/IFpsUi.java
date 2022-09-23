package com.tencent.monitor.fps;

/**
 *  
 * @date 2017-04-23
 */

public interface IFpsUi {
    void start();

    void stop();

    void onChange(float prevFps, float currentFps);

    void onBecameForeground();

    void onBecameBackground();

}
