package com.RSen.Commandr.builtincommands;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.BluetoothUtil;
import com.RSen.Commandr.util.KeyguardUtil;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class UnlockCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public UnlockCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.unlock_phrases);
        TITLE = ctx.getString(R.string.unlock_title);
    }

    @Override
    public void execute(Context context, String predicate) {
        KeyguardUtil.unlock(context);
    }

    @Override
    public boolean isAvailable(Context context) {
        return true;
    }

    @Override
    protected boolean isOnByDefault() {
        return false;
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
