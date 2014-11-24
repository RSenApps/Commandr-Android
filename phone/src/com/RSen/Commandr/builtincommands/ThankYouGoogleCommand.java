package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.GoogleNowUtil;


/**
 * Created by Daniel Quah on 11/10/2014.
 */

public class ThankYouGoogleCommand extends MostWantedCommand {
    private static String TITLE;
    private static String DEFAULT_PHRASE;

    public ThankYouGoogleCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.thank_you_google_phrase);
        TITLE = ctx.getString(R.string.thank_you_google_title);
    }



    @Override
    public void execute(final Context context, String predicate){
        GoogleNowUtil.resetGoogleNow(context);
        GoogleNowUtil.returnPreviousApp();
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

    // If this is disabled then it redirects users away from the Super User permission dialog.
    @Override
        public boolean isHandlingGoogleNowReset() {
        return true;
    }


    }