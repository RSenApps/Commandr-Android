package com.RSen.Commandr.core;

import android.content.Context;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         Command.java
 * @version 1.0
 *          5/28/14
 */
public abstract class Command {
    public abstract void execute(Context context, String predicate);

    /**
     * Checks if the command is enabled. Some commands are not enabled on all devices or at all times.
     *
     * @param context The Context required to check system features.
     * @return boolean representing if the command is enabled
     */
    public abstract boolean isEnabled(Context context);

    //return null to not allow predicate
    public abstract String getPredicateHint();
}
