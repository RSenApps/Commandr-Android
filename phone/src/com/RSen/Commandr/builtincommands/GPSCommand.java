package com.RSen.Commandr.builtincommands;

import android.content.Context;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.GPSUtil;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class GPSCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public GPSCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.gps_phrases);
        TITLE = ctx.getString(R.string.gps_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        GPSUtil.turnGPSOn(context);
    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return GPSUtil.canToggleGPS(context);
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
