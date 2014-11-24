package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Aaron Disibio
 * @version 1.0 August 8th 14
 */
public class ScreenBrightnessCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;


    public ScreenBrightnessCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.screen_brightness_phrase);
        TITLE = ctx.getString(R.string.screen_brightness_title);
        context = ctx;

    }

    /**
     * command changes volume
     */
    @Override
    public void execute(Context context, String predicate) {

        try

        {
            predicate = predicate.replaceAll("[^\\.0123456789]", "");

            // int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            int brightness = Integer.parseInt(predicate);
            if (brightness < 0 || brightness > 100)
            {
                Toast.makeText(context, context.getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
            }
            Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
            context.startActivity(new Intent(context, ScreenBrightnessActivity.class).putExtra("brightness", brightness));
        } catch (NumberFormatException e)

        {
            e.printStackTrace();
        }

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
    public String getPredicateHint() {
        return context.getString(R.string.volume_percentage_hint);
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