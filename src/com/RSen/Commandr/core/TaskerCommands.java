package com.RSen.Commandr.core;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.tasker.TaskerIntent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         TaskerCommands.java
 * @version 1.0
 *          5/28/14
 */
public class TaskerCommands {
    /**
     * An arraylist of Tasker commands
     */
    public static ArrayList<TaskerCommand> taskerCommands;

    /**
     * Default constructor, load Tasker Commands
     */
    public TaskerCommands() {
    }

    private static void load(Context context) {
        File file = new File(context.getDir("data", Context.MODE_PRIVATE), "tasker");

        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            taskerCommands = (ArrayList<TaskerCommand>) inputStream.readObject();
            inputStream.close();

        } catch (Exception e) {
            taskerCommands = new ArrayList<TaskerCommand>();
        }
        ArrayList<String> commandsInTasker = getCommandListFromTasker(context);
        //add unadded commands
        for (String command : commandsInTasker) {
            boolean commandAlreadyAdded = false;
            for (TaskerCommand taskerCommand : taskerCommands) {
                if (taskerCommand.taskerCommandName.equals(command)) {
                    commandAlreadyAdded = true;
                    break;
                }
            }
            if (!commandAlreadyAdded) {
                taskerCommands.add(new TaskerCommand(command, command));
            }
        }
        //remove old commands
        Iterator<TaskerCommand> iterator = taskerCommands.iterator();
        while (iterator.hasNext()) {
            TaskerCommand taskerCommand = iterator.next();
            boolean commandFound = false;
            for (String commandInTasker : commandsInTasker) {
                if (taskerCommand.taskerCommandName.equals(commandInTasker)) {
                    commandFound = true;
                    break;
                }
            }
            if (!commandFound) {
                iterator.remove();
            }
        }
        save(context);
    }

    public static ArrayList<TaskerCommand> getTaskerCommands(Context context) {
        load(context);

        return taskerCommands;
    }

    public static void save(Context context) {
        if (taskerCommands == null) {
            return;
        }
        File file = new File(context.getDir("data", context.MODE_PRIVATE), "tasker");
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));

            outputStream.writeObject(taskerCommands);


            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the list of commands via the Tasker api
     *
     * @param context The Context required to communicate with Tasker
     * @return an Arraylist of commands.
     */
    public static ArrayList<String> getCommandListFromTasker(Context context) {
        ArrayList<String> returnList = new ArrayList<String>();
        Cursor c = context.getContentResolver().query(Uri.parse("content://net.dinglisch.android.tasker/tasks"), null, null, null, null);

        if (c != null) {
            int nameCol = c.getColumnIndex("name");
            int projNameCol = c.getColumnIndex("project_name");

            while (c.moveToNext())
                returnList.add(c.getString(nameCol));

            c.close();
        }
        return returnList;
    }

    /**
     * Executes the matching TaskerCommand for a given command prhase
     *
     * @param context            Context required to execute commands
     * @param interceptedCommand The command phrase to execute
     * @return True if command executed, otherwise false
     */
    public static boolean execute(Context context, String interceptedCommand) {
        if (taskerCommands == null) {
            load(context);
        }
        boolean commandExecuted = false;
        if (TaskerIntent.testStatus(context).equals(TaskerIntent.Status.OK)) {
            if (taskerCommands != null) {
                for (TaskerCommand cmd : taskerCommands) {
                    if (interceptedCommand.toLowerCase().trim().equals(cmd.activationName.toLowerCase().trim()) && cmd.isEnabled) {
                        cmd.execute(context, "");
                        commandExecuted = true;
                    }
                }
            }


        } else if (TaskerIntent.testStatus(context).equals(TaskerIntent.Status.NoPermission)) {
            Toast.makeText(context, context.getString(R.string.reinstall_commandr), Toast.LENGTH_LONG).show();
        }
        return commandExecuted;
    }
}
