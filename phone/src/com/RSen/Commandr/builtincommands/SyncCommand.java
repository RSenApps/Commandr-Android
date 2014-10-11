package com.RSen.Commandr.builtincommands;

import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiManager;
import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * Created by Daniel Quah on 11/10/2014.
 */
public class SyncCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public SyncCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.sync_phrases);
        TITLE = ctx.getString(R.string.sync_title);
    }
    /**
     * Execute this command (turn sync on)
     */
    @Override
    public void execute(Context context, String predicate) {
        ContentResolver.setMasterSyncAutomatically(true);
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

}


