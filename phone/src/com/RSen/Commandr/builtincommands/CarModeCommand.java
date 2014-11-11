package com.RSen.Commandr.builtincommands;

import android.app.UiModeManager;
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
public class CarModeCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public CarModeCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.car_mode_phrase);
        TITLE = ctx.getString(R.string.car_mode_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        uiModeManager.enableCarMode(UiModeManager.ENABLE_CAR_MODE_GO_CAR_HOME);
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
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }


}
