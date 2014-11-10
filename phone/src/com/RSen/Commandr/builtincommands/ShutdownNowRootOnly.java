package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Aaron Disibio
 * @version 1.0 August 16th 14
 */
public class ShutdownNowRootOnly extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;


    public ShutdownNowRootOnly(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.shutdown_phrases); //Google doesn't seem to ever put "shutdown" only "shut down"
        TITLE = ctx.getString(R.string.shutdown_title);

    }

    /**
     * command shutdown NOW
     */
    @Override
    public void execute(final Context context, String predicate) {

        // Unfortunately I cannot find a way to force the system only broadcast of shutdown using root, so this shutsdown IMMEDIATELY! Without warning other apps.
        Intent i = new Intent(context, RootCommandActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("command", new String[]{"su", "-c", "reboot -p"});
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