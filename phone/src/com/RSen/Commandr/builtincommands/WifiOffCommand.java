package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class WifiOffCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public WifiOffCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.wifi_off_phrase);
        TITLE = ctx.getString(R.string.wifi_off_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return true;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }

}
