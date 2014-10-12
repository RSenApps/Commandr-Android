package com.RSen.Commandr.builtincommands;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by Daniel on 11/10/2014.
 */

public class AirplaneCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public AirplaneCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.airplane_phrase);
        TITLE = ctx.getString(R.string.airplane_title);
    }



    @Override
    public void execute(Context context, String predicate){
    Process p;
        try{
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("settings put global airplane_mode_on 1\n" +
                    "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true\n");
            os.flush();
        }catch(IOException e) {

        }
        }
        /**
         * It is enabled if the phone has a flash feature
         */
        @Override
        public boolean isAvailable (Context context){
            return true;
        }

        @Override
        public String getTitle () {
            return TITLE;
        }

        @Override
        public String getDefaultPhrase () {
            return DEFAULT_PHRASE;
        }

    }