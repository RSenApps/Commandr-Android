package com.RSen.Commandr.builtincommands;

import android.content.Context;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.WifiAPUtils;

/**
 * @author Tim Schonberger
 *         Commandr for Google Now
 *         WifiAPOffCommand.java
 * @version 1.0
 *          8/24/14
 */

public class WifiAPOffCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public WifiAPOffCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.wifi_ap_off_phrase);
        TITLE = ctx.getString(R.string.wifi_ap_off_title);
    }

    @Override
    public void execute(Context context, String predicate) {
        WifiAPUtils.setWifiApEnabled(context, false);
    }

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
