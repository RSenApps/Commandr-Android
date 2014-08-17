package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Aaron Disibio
 * @version 1.0 August 16th 14
 */
public class WolframRedirectCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;
   // final static String WOLFRAM_QUERY = "wolfram_query";


    public WolframRedirectCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.wolfram_phrases);
        TITLE = ctx.getString(R.string.wolfram_title);
        context = ctx;

    }

    /**
     * command shutdown NOW
     */
    @Override
    public void execute(final Context context, String predicate) {


        Toast.makeText(context, context.getString(R.string.wolfram_redirect), Toast.LENGTH_SHORT).show();
        try {

           String query = URLEncoder.encode(predicate, "utf-8");
            String url = "http://www.wolframalpha.com/input/?i=" + query;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        /*
        Intent i = new Intent(context, WolframRedirectActivity.class);
        i.putExtra(WOLFRAM_QUERY,predicate);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        */
        }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                Toast.makeText(context, "Wolfram redirect failed", Toast.LENGTH_SHORT).show();

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