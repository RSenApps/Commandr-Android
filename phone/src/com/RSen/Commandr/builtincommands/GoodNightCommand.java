package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.CellularDataUtil;

/**
 * Created by Daniel Quah on 11/10/2014.
 */
public class GoodNightCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public GoodNightCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.good_night_phrase);
        TITLE = ctx.getString(R.string.good_night_title);
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        CellularDataUtil.setMobileDataEnabled(context, false);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
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
