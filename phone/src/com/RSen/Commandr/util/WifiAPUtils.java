package com.RSen.Commandr.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author Tim Schonberger
 *         Commandr for Google Now
 *         WifiAPUtils.java
 * @version 1.0
 *          8/24/14
 */
public class WifiAPUtils {
    private enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    public static boolean setWifiApEnabled(Context context, boolean enabled) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            if (enabled) {
                mWifiManager.setWifiEnabled(false);
            }

            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            return (Boolean) method.invoke(mWifiManager, null, enabled);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isWifiApEnabled(Context context) {
        return getWifiApState(context) == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    private WIFI_AP_STATE getWifiApState(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer)method.invoke(mWifiManager));

            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }

            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }
}
