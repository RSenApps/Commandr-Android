package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.SMSUtils;
import com.RSen.Commandr.util.SmsMmsMessage;
import com.RSen.Commandr.util.TTSService;

import java.util.ArrayList;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class ReadLastSMSFromContactCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private static Context ctx;

    public ReadLastSMSFromContactCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.read_last_sms_phrase);
        TITLE = ctx.getString(R.string.last_sms_title);
        this.ctx = ctx;
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {

        SmsMmsMessage message = SMSUtils.getRecentMessageFromSender(context, predicate.toLowerCase().trim());
        if (message == null) {
            Intent i = new Intent(context, TTSService.class);
            i.putExtra("toSpeak", context.getString(R.string.no_messages_found));
            context.startService(i);
        } else {
            Intent i = new Intent(context, TTSService.class);
            i.putExtra("toSpeak", message.getContactName() + " sent " + message.getMessageBody());
            context.startService(i);
        }
    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public String getPredicateHint() {
        return ctx.getString(R.string.sender_name);
    }

    @Override
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }


}
