package com.RSen.Commandr.core;

import android.content.Context;

import com.RSen.Commandr.builtincommands.AirplaneCommand;
import com.RSen.Commandr.builtincommands.AirplaneOffCommand;
import com.RSen.Commandr.builtincommands.AudioCaptureCommand;
import com.RSen.Commandr.builtincommands.BatteryCommand;
import com.RSen.Commandr.builtincommands.BluetoothCommand;
import com.RSen.Commandr.builtincommands.BluetoothOffCommand;
import com.RSen.Commandr.builtincommands.CarModeCommand;
import com.RSen.Commandr.builtincommands.CarModeOffCommand;
import com.RSen.Commandr.builtincommands.CellularDataCommand;
import com.RSen.Commandr.builtincommands.CellularDataOffCommand;
import com.RSen.Commandr.builtincommands.ChatbotCommand;
import com.RSen.Commandr.builtincommands.ClearNotificationsCommand;
import com.RSen.Commandr.builtincommands.FlashlightCommand;
import com.RSen.Commandr.builtincommands.FlashlightOffCommand;
import com.RSen.Commandr.builtincommands.GPSCommand;
import com.RSen.Commandr.builtincommands.GPSOffCommand;
import com.RSen.Commandr.builtincommands.GoodNightCommand;
import com.RSen.Commandr.builtincommands.LockCommand;
import com.RSen.Commandr.builtincommands.LowerVolumeCommand;
import com.RSen.Commandr.builtincommands.NextMusicCommand;
import com.RSen.Commandr.builtincommands.PauseMusicCommand;
import com.RSen.Commandr.builtincommands.PlayPlaylistCommand;
import com.RSen.Commandr.builtincommands.PreviousMusicCommand;
import com.RSen.Commandr.builtincommands.RaiseVolumeCommand;
import com.RSen.Commandr.builtincommands.ReadLastSMSFromContactCommand;
import com.RSen.Commandr.builtincommands.ReadUnreadGmailCommand;
import com.RSen.Commandr.builtincommands.ReadUnreadSMSCommand;
import com.RSen.Commandr.builtincommands.RebootIntoRecoveryRootOnly;
import com.RSen.Commandr.builtincommands.RestartNowRootOnly;
import com.RSen.Commandr.builtincommands.ResumeMusicCommand;
import com.RSen.Commandr.builtincommands.RotationLockOffCommand;
import com.RSen.Commandr.builtincommands.RotationLockOnCommand;
import com.RSen.Commandr.builtincommands.ScreenBrightnessCommand;
import com.RSen.Commandr.builtincommands.SendWhatsappCommand;
import com.RSen.Commandr.builtincommands.ShutdownNowRootOnly;
import com.RSen.Commandr.builtincommands.SilenceRingerCommand;
import com.RSen.Commandr.builtincommands.SyncCommand;
import com.RSen.Commandr.builtincommands.SyncOffCommand;
import com.RSen.Commandr.builtincommands.TakePictureCommand;
import com.RSen.Commandr.builtincommands.TakeSelfieCommand;
import com.RSen.Commandr.builtincommands.ThankYouGoogleCommand;
import com.RSen.Commandr.builtincommands.UnlockCommand;
import com.RSen.Commandr.builtincommands.UnsilenceRingerCommand;
import com.RSen.Commandr.builtincommands.VolumePercentage;
import com.RSen.Commandr.builtincommands.WazeNavigateCommand;
import com.RSen.Commandr.builtincommands.WifiAPCommand;
import com.RSen.Commandr.builtincommands.WifiAPOffCommand;
import com.RSen.Commandr.builtincommands.WifiCommand;
import com.RSen.Commandr.builtincommands.WifiOffCommand;
import com.RSen.Commandr.builtincommands.WolframRedirectCommand;
import com.RSen.Commandr.util.GoogleNowUtil;
import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         MostWantedCommand.java
 * @version 1.0
 *          5/28/14
 */
public class MostWantedCommands {
    private static MostWantedCommand[] commands;

    /**
     * Executes a matching MostWantedCommand
     *
     * @param context The context necessary to execute the command
     * @return true if matching command found, false otherwise.
     */
    public static MostWantedCommand[] getCommands(Context context) {
        if (commands == null) {
            commands = new MostWantedCommand[]{new FlashlightCommand(context), new FlashlightOffCommand(context),
                    new WifiCommand(context), new WifiOffCommand(context), new GPSCommand(context), new GPSOffCommand(context), new BluetoothCommand(context),
                    new BluetoothOffCommand(context), new WifiAPCommand(context), new WifiAPOffCommand(context), new PauseMusicCommand(context),
                    new ResumeMusicCommand(context), new NextMusicCommand(context), new PreviousMusicCommand(context), new ReadUnreadSMSCommand(context), new ReadLastSMSFromContactCommand(context),
                    new PlayPlaylistCommand(context), new ChatbotCommand(context), new CellularDataCommand(context), new CellularDataOffCommand(context),
                    new ReadUnreadGmailCommand(context), new RaiseVolumeCommand(context), new LowerVolumeCommand(context), new SilenceRingerCommand(context),
                    new UnsilenceRingerCommand(context), new VolumePercentage(context), new UnlockCommand(context), new LockCommand(context),
                    new TakePictureCommand(context), new TakeSelfieCommand(context), new ShutdownNowRootOnly(context), new RebootIntoRecoveryRootOnly(context),
                    new RestartNowRootOnly(context), new ClearNotificationsCommand(context), new WolframRedirectCommand(context), new SendWhatsappCommand(context), new RotationLockOnCommand(context),
                    new RotationLockOffCommand(context), new SyncCommand(context), new SyncOffCommand(context), new GoodNightCommand(context), new AirplaneCommand(context),
                    new AirplaneOffCommand(context), new CarModeCommand(context), new CarModeOffCommand(context), new ThankYouGoogleCommand(context),new ScreenBrightnessCommand(context), new BatteryCommand(context)
            , new AudioCaptureCommand(context), new WazeNavigateCommand(context)};
        }
        return commands;
    }
    public static boolean execute (Context context, String phrase)
    {
        return execute(context, phrase, false);
    }
    public static boolean execute(Context context, String phrase, boolean dontResetGoogleNow) {
        boolean commandExecuted = false;
        for (MostWantedCommand command : getCommands(context)) {
            if (command.isEnabled(context)) {
                String[] activationPhrases = command.getPhrase(context).split(",");
                for (String activationPhrase : activationPhrases) {
                    boolean commandFound = true;
                    int commandLength = 0;
                    for (String activationPhrasePart : activationPhrase.split("&"))
                    {
                        commandLength += activationPhrasePart.trim().length();
                        if (!phrase.toLowerCase().trim().contains(activationPhrasePart.toLowerCase().trim()))
                        {
                            commandFound = false;
                            break;
                        }
                    }
                    if (commandFound)
                    {
                        if (command.getPredicateHint() == null)
                        {
                            command.execute(context, "");

                        }
                        else {
                            command.execute(context, phrase.toLowerCase().trim().substring(commandLength).trim());
                        }
                        commandExecuted = true;
                        if (!command.isHandlingGoogleNowReset() && !dontResetGoogleNow) {
                            GoogleNowUtil.resetGoogleNow(context);
                        }
                        break;
                    }


                }

            }
        }
        return commandExecuted;
    }
    public static ArrayList<String> getCommandPhrasesList(Context context)
    {
        ArrayList<String> returnList = new ArrayList<String>();
        for (MostWantedCommand command : getCommands(context))
        {
            if (command.isEnabled(context) && command.getPredicateHint() == null) {
                returnList.add(command.getPhrase(context).split(",")[0]);
            }
        }
        return returnList;
    }

}
