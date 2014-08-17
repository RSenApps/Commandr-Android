package com.RSen.Commandr.builtincommands;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MostWantedCommand;

/**
 * @author Aaron Disibio
 * @version 1.0 August 16th 14
 */
public class RebootIntoRecoveryRootOnly extends MostWantedCommand {

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;


    public RebootIntoRecoveryRootOnly(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.reboot_recovery_phrases);
        TITLE = ctx.getString(R.string.reboot_recovery_title);
        context = ctx;

    }

    /**
     * command reboots NOW
     */
    @Override
    public void execute(final Context context, String predicate) {
        Toast.makeText(context, "Boot to recovery 1111!", Toast.LENGTH_SHORT).show();

        // Unfortunately I cannot find a way to force the system only broadcast of reboot using root, so this reboots IMMEDIATELY! Without warning other apps.
        if (RebootIntoRecoveryRootOnly.this.context instanceof Activity) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot recovery"});
                        proc.waitFor();
                    } catch (Exception ex) {
                        ((Activity) RebootIntoRecoveryRootOnly.this.context).runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, context.getString(R.string.boot_recovery_failed), Toast.LENGTH_SHORT).show();
                            }
                        });
                        ex.printStackTrace();
                    }
                }
            }, "RebootIntoRecoveryRootOnly ").start();
        } else {
            Toast.makeText(context, context.getString(R.string.boot_recovery_failed), Toast.LENGTH_SHORT).show();
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
    protected boolean isOnByDefault() {
        return false;
    }

    @Override
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }


    // If this is disabled then it redirects users away from the Super User permission dialog.
    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }
}