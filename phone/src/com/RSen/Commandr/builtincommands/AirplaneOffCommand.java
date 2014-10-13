package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Daniel Quah on 12/10/2014.
 */
public class AirplaneOffCommand  extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;

    public AirplaneOffCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.airplane_off_phrase);
        TITLE = ctx.getString(R.string.airplane_off_title);
        context = ctx;
    }

    @Override
    public void execute(Context context, String predicate){
        Intent i = new Intent(context, RootCommandActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("command", new String[]{"su", "-c","settings put global airplane_mode_on 0\n","am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false\n"});
        context.startActivity(i);
    }

    /**
     * It is enabled if the phone has a flash feature
     */
    @Override
    public boolean isAvailable (Context context){
        return true;
    }
    @Override
    protected boolean isOnByDefault() {
        return false;
    }

    @Override
    public String getTitle () {
        return TITLE;
    }

    @Override
    public String getDefaultPhrase () {
        return DEFAULT_PHRASE;
    }
    // If this is disabled then it redirects users away from the Super User permission dialog.
    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }

}