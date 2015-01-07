package com.RSen.Commandr.builtincommands;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

import java.net.URLEncoder;

/**
 * @author Aaron Disibio
 * @version 1.0 August 16th 14
 */
public class WazeNavigateCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;
    // final static String WOLFRAM_QUERY = "wolfram_query";


    public WazeNavigateCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.navigate_waze_phrase);
        TITLE = ctx.getString(R.string.navigate_waze_title);
        context = ctx;

    }

    /**
     * command shutdown NOW
     */
    @Override
    public void execute(final Context context, String predicate) {
        try
        {
            String url = "waze://?q=" + predicate;
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity( intent );
        }
        catch ( ActivityNotFoundException ex  )
        {
            Intent intent =
                    new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
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
    public String getPredicateHint() {
        return context.getString(R.string.wolfram_hint);
    }

    @Override
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }


    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }
}