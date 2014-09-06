package com.RSen.Commandr.builtincommands;

import android.content.Context;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.WifiAPUtils;

/**
 * @author Tim Schonberger
 *         Commandr for Google Now
 *         WifiAPCommand.java
 * @version 1.0
 *          8/24/14
 */

public class WifiAPCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public WifiAPCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.wifi_ap_on_phrase);
        TITLE = ctx.getString(R.string.wifi_ap_on_title);
    }

    @Override
    public void execute(Context context, String predicate) {
        WifiAPUtils.setWifiApEnabled(context, true);
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
