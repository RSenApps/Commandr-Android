package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightOffCommand.java
 * @version 1.0
 *          5/28/14
 */
public class FlashlightOffCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public FlashlightOffCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.flashlight_off_phrases);
        TITLE = ctx.getString(R.string.flashlight_off_title);
    }

    /**
     * Turns off the flashlight
     */
    @Override
    public void execute(Context context, String predicate) {
        Intent i = new Intent(context, FlashlightActivity.class);
        i.putExtra("onOrOff", false);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    /**
     * Enabled if flash is enabled for the phone
     */
    @Override
    public boolean isAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public boolean isHandlingGoogleNowReset() {
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
