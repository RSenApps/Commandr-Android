package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.GoogleNowUtil;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class AudioCaptureCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private static String PREDICATE_HINT;
    public AudioCaptureCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.capture_audio_phras);
        TITLE = ctx.getString(R.string.audio_capture_title);
        PREDICATE_HINT = ctx.getString(R.string.seconds);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(final Context context, final String predicate) {
        GoogleNowUtil.resetGoogleNowOnly(context);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Intent i = new Intent(context, AudioCaptureActivity.class);
                int seconds = -1;
                try {
                   seconds = Integer.parseInt(predicate);
                }
                catch (Exception e)
                {}
                i.putExtra("seconds", seconds);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                return true;
            }
        });
        handler.sendEmptyMessageDelayed(0, 500);

    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
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

    @Override
    public String getPredicateHint() {
        return PREDICATE_HINT;
    }
}
