package com.tencent.monitor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 *  
 * @date 2017-02-28
 */

public class Utils {
    public static final String LINE_SEPARATOR = "\r\n";
    public static final String AREA_SEPARATOR = " ****************************** ";
    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * close quietly
     */
    public static void quietClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * request overlay permission when api >= 23
     */
    public static boolean requestOverlayPermission(Context context) {
        boolean permNeeded = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                permNeeded = true;
            }
        }
        return permNeeded;
    }


    public static float getRefreshRate(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getRefreshRate();
    }

    public static String generateTag() {
        return System.currentTimeMillis() + "_" + ((int) (Math.random() * 1000));
    }
}
