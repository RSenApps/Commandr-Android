package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.pm.PackageManager;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.BluetoothUtil;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class BluetoothCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public BluetoothCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.bluetooth_phrases);
        TITLE = ctx.getString(R.string.bluetooth_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        BluetoothUtil.setBluetooth(true);
    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
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
