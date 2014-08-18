package com.RSen.Commandr.core;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Ryan on 6/27/2014.
 */
public abstract class MostWantedCommand extends Command {
    @Override
    public abstract void execute(Context context, String predicate);

    protected boolean isOnByDefault() {
        return true;
    }

    @Override
    public boolean isEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getTitle(), isOnByDefault()) && isAvailable(context);
    }

    public abstract boolean isAvailable(Context context);

    public abstract String getTitle();

    protected abstract String getDefaultPhrase();

    public String getPhrase(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("phrase" + getTitle(), getDefaultPhrase());
    }

    public void setPhrase(Context context, String phrase) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("phrase" + getTitle(), phrase).commit();
    }

    //commands that launch their own activity must handle resetting google now
    public boolean isHandlingGoogleNowReset() {
        return false;
    }

    //default behavior - don't allow predicates
    public String getPredicateHint() {
        return null;
    }
}