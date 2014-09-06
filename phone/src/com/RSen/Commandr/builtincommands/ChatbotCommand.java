package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;
import com.RSen.Commandr.util.PandoraBotsUtil;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class ChatbotCommand extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;

    public ChatbotCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.chatbot_phrases);
        TITLE = ctx.getString(R.string.chatbot_title);
        context = ctx;
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {
        Toast.makeText(context, context.getString(R.string.fetch_response), Toast.LENGTH_LONG).show();
        PandoraBotsUtil.askPandorabots(context, predicate);
    }

    @Override
    public boolean isAvailable(Context context) {
        return true;
    }

    @Override
    public String getPredicateHint() {
        return context.getString(R.string.chatbot_predicate_hint);
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
