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
public class ReadUnreadSMSCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public ReadUnreadSMSCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.read_unread_sms_phrase);
        TITLE = ctx.getString(R.string.read_unread_sms_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {

        ArrayList<SmsMmsMessage> unread = SMSUtils.getUnreadMessages(context);
        String text = "";
        if (unread == null || unread.size() < 1) {
            Intent i = new Intent(context, TTSService.class);
            i.putExtra("toSpeak", "No unread text messages");
            context.startService(i);
        } else {
            for (SmsMmsMessage message : unread) {
                text += message.getContactName() + " sent " + message.getMessageBody();
            }
            Intent i = new Intent(context, TTSService.class);
            i.putExtra("toSpeak", text);
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
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }


}
