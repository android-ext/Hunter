package com.quinn.hunter.plugin.largeimage;

import com.tencent.method.plugin.largeimage.LargeImageExtension;

public class Config {
    public static String TAG = "Config";

    private LargeImageExtension  largeImageExtension = new LargeImageExtension();
    /**
     * 大图检测插件的开关
     */
    private boolean largeImagePluginSwitch = true;

    private Config() {}

    public boolean largeImagePluginSwitch() {
        return largeImagePluginSwitch;
    }

    private static class Holder {
        private static Config INSTANCE = new Config();
    }

    public static Config getInstance(){
        return Holder.INSTANCE;
    }


    public void setLargeImageExtension(LargeImageExtension largeImageExtension) {
        this.largeImageExtension = largeImageExtension;
    }

}
