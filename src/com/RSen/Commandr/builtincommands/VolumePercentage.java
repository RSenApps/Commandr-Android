package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Aaron Disibio
 * @version 1.0 August 8th 14
 */
public class VolumePercentage extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;


    public VolumePercentage(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.volume_percentage_phrases);
        TITLE = ctx.getString(R.string.volume_percentage_title);
        context=ctx;

    }

    /**
     * command changes volume
     */
    @Override
    public void execute(Context context, String predicate) {

        Log.d("predicate", "volume " + predicate);
        try

        {
            predicate = predicate.replaceAll("[^\\.0123456789]", "");
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            // int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) ((Integer.parseInt(predicate) / 100.0) * maxVolume), AudioManager.FLAG_SHOW_UI);
        } catch (
                NumberFormatException e
                )

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

}