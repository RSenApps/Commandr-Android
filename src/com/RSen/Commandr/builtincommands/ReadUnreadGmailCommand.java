package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class ReadUnreadGmailCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public ReadUnreadGmailCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.unread_gmail_phrases);
        TITLE = ctx.getString(R.string.unread_gmail_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        Toast.makeText(context, context.getString(R.string.fetching_gmail), Toast.LENGTH_LONG).show();
       Intent i = new Intent(context, ReadUnreadGmailActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
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

    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }
}
