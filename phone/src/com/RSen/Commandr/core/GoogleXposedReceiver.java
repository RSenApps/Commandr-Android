package com.RSen.Commandr.core;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import com.RSen.Commandr.util.GoogleSearchApi;

public class GoogleXposedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String queryText = intent.getStringExtra(GoogleSearchApi.KEY_QUERY_TEXT);
        if (queryText != null && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("usexposed", false)) {
           CommandInterpreter.interpret(context, queryText, false);
        }
	}


}
