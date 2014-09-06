package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class PlayPlaylistCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;

    public PlayPlaylistCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.play_playlist_phrase);
        TITLE = ctx.getString(R.string.google_music_playlist);
        context = ctx;
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        Intent i = new Intent(context, PlayPlaylistActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("playlistname", predicate);
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
    public String getPredicateHint() {
        return context.getString(R.string.playlist_predicate_hint);
    }

    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }
}
