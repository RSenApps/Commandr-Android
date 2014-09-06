package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
public class SendWhatsappCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;

    public SendWhatsappCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.send_whatsapp_phrase);
        TITLE = ctx.getString(R.string.send_whatsapp_title);
        context = ctx;
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, predicate);
            sendIntent.setType("text/plain");
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.setPackage("com.whatsapp");
            context.startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.install_whatsapp), Toast.LENGTH_LONG).show();
            final String appPackageName = "com.whatsapp"; // getPackageName() from Context or Activity object
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
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

    @Override
    public String getPredicateHint() {
        return context.getString(R.string.message);
    }
}
