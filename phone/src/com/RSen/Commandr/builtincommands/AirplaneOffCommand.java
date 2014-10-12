package com.RSen.Commandr.builtincommands;

import android.content.Context;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Daniel on 12/10/2014.
 */
public class AirplaneOffCommand  extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public AirplaneOffCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.airplane_off_phrase);
        TITLE = ctx.getString(R.string.airplane_off_title);
    }
    @Override
    public void execute(Context context, String predicate){
        Process p;
        try{
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("settings put global airplane_mode_on 0\n" +
                    "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false\n");
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