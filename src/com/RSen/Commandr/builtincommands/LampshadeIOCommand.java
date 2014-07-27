package com.RSen.Commandr.builtincommands;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.PandoraBotsUtil;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class LampshadeIOCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;
    public LampshadeIOCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.lampshade_phrases);
        TITLE = ctx.getString(R.string.lampshade_title);
        context = ctx;
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.kuxhausen.huemore", "com.kuxhausen.huemore.automation.VoiceInputReceiver"));
        i.putExtra("automation.extra.voice_input_string", predicate);
        context.sendBroadcast(i);
    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }
    @Override
    public String getPredicateHint() {
        return context.getString(R.string.lampshade_predicate);
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
