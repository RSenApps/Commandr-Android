package com.RSen.Commandr.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.tasker.EditActivity;
import com.RSen.Commandr.tasker.TaskerPlugin;

/**
 * Created by Ryan on 7/5/2014.
 */
public class CommandInterpreter {
    //continuous from accessibility, don't always show no command found...
    public static boolean interpret(Context context, String interceptedCommand, boolean continuous)
    {
        return interpret(context, interceptedCommand, continuous, false);
    }
    public static boolean interpret(Context context, String interceptedCommand, boolean continuous, boolean dontResetGoogleNow) {
        boolean commandExecuted = false;
        Intent taskerActionPlugin = new Intent("com.twofortyfouram.locale.intent.action.REQUEST_QUERY").putExtra("com.twofortyfouram.locale.intent.extra.ACTIVITY",
                EditActivity.class.getName());
        Bundle bundle = new Bundle();
        bundle.putString("interceptedCommand", interceptedCommand);
        TaskerPlugin.Event.addPassThroughMessageID(taskerActionPlugin);
        TaskerPlugin.Event.addPassThroughData(taskerActionPlugin, bundle);
        context.sendBroadcast(taskerActionPlugin);
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("enabled", true)) {

            if (interceptedCommand == null || interceptedCommand.equals("TEST")) {
                return false;
            }
            commandExecuted = MostWantedCommands.execute(context, interceptedCommand, dontResetGoogleNow);

            if (TaskerCommands.execute(context, interceptedCommand, dontResetGoogleNow)) {
                commandExecuted = true;
            }
            if (!commandExecuted) {
                String passThroughPkg = PreferenceManager.getDefaultSharedPreferences(context).getString("passthrough_pkg", "");
                if (passThroughPkg.equals("") || !interceptedCommand.toLowerCase().startsWith(context.getString(R.string.note)) || continuous) {
                    if (!continuous) {
                        Toast.makeText(context, context.getString(R.string.no_command_found) + " " + interceptedCommand, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        i.putExtra(Intent.EXTRA_TEXT, interceptedCommand.split(" ", 2)[1]);
                    } catch (Exception e) {
                        i.putExtra(Intent.EXTRA_TEXT, "");
                    }
                    i.setType("text/plain");
                    i.setPackage(passThroughPkg);
                    try {
                        context.startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(context, context.getString(R.string.note_uninstalled), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        return commandExecuted;
    }

}
