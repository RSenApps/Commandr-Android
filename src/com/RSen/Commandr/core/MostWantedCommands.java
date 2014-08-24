package com.RSen.Commandr.core;

import android.content.Context;

import com.RSen.Commandr.builtincommands.BluetoothCommand;
import com.RSen.Commandr.builtincommands.BluetoothOffCommand;
import com.RSen.Commandr.builtincommands.CellularDataCommand;
import com.RSen.Commandr.builtincommands.CellularDataOffCommand;
import com.RSen.Commandr.builtincommands.ChatbotCommand;
import com.RSen.Commandr.builtincommands.FlashlightCommand;
import com.RSen.Commandr.builtincommands.FlashlightOffCommand;
import com.RSen.Commandr.builtincommands.GPSCommand;
import com.RSen.Commandr.builtincommands.GPSOffCommand;
import com.RSen.Commandr.builtincommands.LockCommand;
import com.RSen.Commandr.builtincommands.LowerVolumeCommand;
import com.RSen.Commandr.builtincommands.NextMusicCommand;
import com.RSen.Commandr.builtincommands.PauseMusicCommand;
import com.RSen.Commandr.builtincommands.PlayPlaylistCommand;
import com.RSen.Commandr.builtincommands.PreviousMusicCommand;
import com.RSen.Commandr.builtincommands.RaiseVolumeCommand;
import com.RSen.Commandr.builtincommands.ReadUnreadGmailCommand;
import com.RSen.Commandr.builtincommands.ReadUnreadSMSCommand;
import com.RSen.Commandr.builtincommands.RebootIntoRecoveryRootOnly;
import com.RSen.Commandr.builtincommands.RestartNowRootOnly;
import com.RSen.Commandr.builtincommands.ResumeMusicCommand;
import com.RSen.Commandr.builtincommands.RotationLockOffCommand;
import com.RSen.Commandr.builtincommands.RotationLockOnCommand;
import com.RSen.Commandr.builtincommands.SendWhatsappCommand;
import com.RSen.Commandr.builtincommands.ShutdownNowRootOnly;
import com.RSen.Commandr.builtincommands.SilenceRingerCommand;
import com.RSen.Commandr.builtincommands.TakePictureCommand;
import com.RSen.Commandr.builtincommands.TakeSelfieCommand;
import com.RSen.Commandr.builtincommands.UnlockCommand;
import com.RSen.Commandr.builtincommands.UnsilenceRingerCommand;
import com.RSen.Commandr.builtincommands.VolumePercentage;
import com.RSen.Commandr.builtincommands.WifiAPCommand;
import com.RSen.Commandr.builtincommands.WifiAPOffCommand;
import com.RSen.Commandr.builtincommands.WifiCommand;
import com.RSen.Commandr.builtincommands.WifiOffCommand;
import com.RSen.Commandr.builtincommands.WolframRedirectCommand;
import com.RSen.Commandr.util.GoogleNowUtil;


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
                    new BluetoothOffCommand(context), new PauseMusicCommand(context), new ResumeMusicCommand(context), new NextMusicCommand(context),
                    new PreviousMusicCommand(context), new ReadUnreadSMSCommand(context), new PlayPlaylistCommand(context), new ChatbotCommand(context),
                    new CellularDataCommand(context), new CellularDataOffCommand(context), new ReadUnreadGmailCommand(context), new RaiseVolumeCommand(context),
                    new LowerVolumeCommand(context), new SilenceRingerCommand(context), new UnsilenceRingerCommand(context), new VolumePercentage(context),
                    new UnlockCommand(context), new LockCommand(context), new TakePictureCommand(context), new TakeSelfieCommand(context),
                    new ShutdownNowRootOnly(context), new RebootIntoRecoveryRootOnly(context), new RestartNowRootOnly(context),
                    new WolframRedirectCommand(context), new SendWhatsappCommand(context), new RotationLockOnCommand(context), new RotationLockOffCommand(context),
                    new WifiAPCommand(context), new WifiAPOffCommand(context)};
        }
        return commands;
    }

    public static boolean execute(Context context, String phrase) {
        boolean commandExecuted = false;
        for (MostWantedCommand command : getCommands(context)) {
            if (command.isEnabled(context)) {
                String[] activationPhrases = command.getPhrase(context).split(",");
                for (String activationPhrase : activationPhrases) {
                    if (command.getPredicateHint() == null) {
                        if (activationPhrase.toLowerCase().trim().equals(phrase.toLowerCase().trim())) {
                            command.execute(context, "");
                            commandExecuted = true;
                            if (!command.isHandlingGoogleNowReset()) {
                                GoogleNowUtil.resetGoogleNow(context);
                            }
                        }
                    } else {
                        if (phrase.toLowerCase().trim().startsWith(activationPhrase.toLowerCase().trim())) {
                            command.execute(context, phrase.toLowerCase().trim().substring(activationPhrase.trim().length()).trim());
                            commandExecuted = true;
                            if (!command.isHandlingGoogleNowReset()) {
                                GoogleNowUtil.resetGoogleNow(context);
                            }
                        }
                    }
                }

            }
        }
        return commandExecuted;
    }

}
