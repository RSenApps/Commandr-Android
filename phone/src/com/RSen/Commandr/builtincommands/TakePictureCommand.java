package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class TakePictureCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public TakePictureCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.take_picture_phrases);
        TITLE = ctx.getString(R.string.take_a_picture);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(final Context context, String predicate) {

        Intent i = new Intent(context, TakePictureActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }

    @Override
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }


}
