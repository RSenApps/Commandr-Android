package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;

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
public class BatteryCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public BatteryCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.battery_status_phrase);
        TITLE = ctx.getString(R.string.battery_status_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        float batteryPct = level / (float)scale;

        String toSpeak = Math.round(batteryPct*100) + " percent ";
        if (isCharging)
        {
            toSpeak += "charging";
        }
        else {
            toSpeak += "left";
        }

        Intent i = new Intent(context, TTSService.class);
        i.putExtra("toSpeak", toSpeak);
        context.startService(i);
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
