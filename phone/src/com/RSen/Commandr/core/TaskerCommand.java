package com.RSen.Commandr.core;

import android.content.Context;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.tasker.TaskerIntent;

import java.io.Serializable;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         TaskerCommand.java
 * @version 1.0
 *          5/28/14
 */
public class TaskerCommand extends Command implements Serializable {
    /**
     * Serialization id used for ensuring class has not changed post-serialization
     */
    private static final long serialVersionUID = 6857044522819206055L;
    /**
     * The command that activates this command
     */
    public String activationName;
    /**
     * The name of the Tasker command to activate
     */
    public String taskerCommandName;
    public boolean isEnabled = false;

    /**
     * Default constructor
     *
     * @param activationName    The command phrase that activates the command
     * @param taskerCommandName The Tasker command name.
     */
    public TaskerCommand(String activationName, String taskerCommandName) {
        this.activationName = activationName.toLowerCase().trim();
        this.taskerCommandName = taskerCommandName;
    }

    @Override
    public void execute(Context context, String predicate) {
        if (TaskerIntent.testStatus(context).equals(TaskerIntent.Status.OK)) {
            try {
                TaskerIntent i = new TaskerIntent(taskerCommandName);
                context.sendBroadcast(i);
            } catch (Exception e) {
            }
        } else if (TaskerIntent.testStatus(context).equals(TaskerIntent.Status.NoPermission)) {
            Toast.makeText(context, context.getString(R.string.reinstall_commandr), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean isEnabled(Context context) {
        return isEnabled;
    }

    //default behavior don't allow predicates
    public String getPredicateHint() {
        return null;
    }
}
