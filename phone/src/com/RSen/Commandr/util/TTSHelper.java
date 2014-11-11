package com.RSen.Commandr.util;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import com.RSen.Commandr.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Ryan on 7/6/2014.
 */
public class TTSHelper extends UtteranceProgressListener implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private Context context;
    private String toSpeak;

    public TTSHelper(Context context, String toSpeak) {
        this.context = context;
        tts = new TextToSpeech(context, this);
        tts.setOnUtteranceProgressListener(this);
        this.toSpeak = toSpeak;
    }

    public void stop() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, context.getString(R.string.language_not_supported), Toast.LENGTH_LONG).show();
            } else {
                speakOut();
            }

        } else {
            Toast.makeText(context, context.getString(R.string.tts_failed), Toast.LENGTH_LONG).show();
        }

    }

    private void speakOut() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
        Toast.makeText(context, toSpeak, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart(String s) {

    }

    @Override
    public void onDone(String s) {
        context.stopService(new Intent(context, TTSService.class));
    }

    @Override
    public void onError(String s) {

    }
}
