package com.RSen.Commandr.builtincommands;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.BluetoothUtil;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class ClearNotificationsCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public ClearNotificationsCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.clear_notifications_phrase);
        TITLE = ctx.getString(R.string.clear_notifications_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        Intent i = new Intent(context, RootCommandActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("command", new String[]{"su", "-c", "service call notification 1"});
        context.startActivity(i);
        
    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }
    // If this is disabled then it redirects users away from the Super User permission dialog.
    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }

}
