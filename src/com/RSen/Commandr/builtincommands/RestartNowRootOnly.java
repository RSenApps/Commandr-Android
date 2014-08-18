package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Aaron Disibio
 * @version 1.0 August 16th 14
 */
public class RestartNowRootOnly extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;


    public RestartNowRootOnly(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.reboot_phrases);
        TITLE = ctx.getString(R.string.reboot_title);
        context = ctx;

    }

    /**
     * command reboots NOW
     */
    @Override
    public void execute(final Context context, String predicate) {

        // Unfortunately I cannot find a way to force the system only broadcast of reboot using root, so this reboots IMMEDIATELY! Without warning other apps.
        Intent i = new Intent(context, RootCommandActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("command", new String[]{"su", "-c", "reboot now"});
        context.startActivity(i);
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
    protected boolean isOnByDefault() {
        return false;
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