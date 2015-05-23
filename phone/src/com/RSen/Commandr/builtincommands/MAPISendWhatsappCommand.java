package com.RSen.Commandr.builtincommands;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.MAPIReceiver;
import com.RSen.Commandr.core.MostWantedCommand;
import com.seebye.messengerapi.api.Contact;
import com.seebye.messengerapi.api.MessengerAPI;
import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.MessageType;
import com.seebye.messengerapi.api.constants.Messenger;
import com.seebye.messengerapi.api.constants.ResponseType;
import com.seebye.messengerapi.api.utils.PackageUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         FlashlightCommand.java
 * @version 1.0
 *          5/28/14
 */
public class MAPISendWhatsappCommand extends MostWantedCommand
    implements MAPIReceiver.ResponseCallback
{
    private static final String PKG_WHATSAPP = "com.whatsapp";

    private static String TITLE;
    private static String DEFAULT_PHRASE;
    private Context context;

    private ConcurrentHashMap<Long, String>  m_mapPredicate = new ConcurrentHashMap<Long, String>();

    public MAPISendWhatsappCommand(Context ctx) {
        DEFAULT_PHRASE = ctx.getString(R.string.send_whatsapp_phrase);
        TITLE = ctx.getString(R.string.send_whatsapp_title);
        context = ctx;
    }

    /**
     * Execute this command (turn on flashlight)
     */
    @Override
    public void execute(Context context, String predicate) {

        if(!PackageUtils.exists(PKG_WHATSAPP))
        {
            installWhatsApp();
        }
        else
        {
            if(!isMAPIAvailable())
            {
                executeEasySolution(predicate);
            }
            else
            {
                try
                {
                    executeMAPISolution(predicate);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isMAPIAvailable()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    private void interpretMessage(ArrayList<Contact> aContacts, String predicate)
    {
        boolean bFound = false;

        for(int i = 0; i < aContacts.size()
                    && !bFound;
                i++)
        {
            Contact contact = aContacts.get(i);
            String strText;

            if(predicate.startsWith(contact.getDisplayname().toLowerCase()))
            {
                bFound = true;
                strText = predicate.substring(contact.getDisplayname().length()).trim();

                try
                {
                    MessengerAPI.sendMessage(Messenger.WHATSAPP, contact.getIDMessenger(), MessageType.TEXT, strText).send();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        if(!bFound)
        {
            Toast.makeText(context, context.getString(R.string.unknown_contact), Toast.LENGTH_LONG).show();
        }
    }

    private void executeMAPISolution(String predicate) throws Exception
    {
        if(MessengerAPI.isInstalled())
        {
            if(MessengerAPI.isEnabled())
            {
                long lBroadcastID = MessengerAPI.getContacts(Messenger.WHATSAPP.getFlag()).send().getID();
                m_mapPredicate.put(lBroadcastID, predicate);
                MAPIReceiver.registerRequest(this, lBroadcastID, this);
            }
            else
            {
                requestAccess();
            }
        }
        else
        {
            installMAPI();
        }
    }

    private void executeEasySolution(String predicate)
    {
        /**
         * Duplicated from {@link SendWhatsappCommand#execute(Context, String)}
         */
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, predicate);
            sendIntent.setType("text/plain");
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.setPackage(PKG_WHATSAPP);
            context.startActivity(sendIntent);
        } catch (Exception e) {}
    }

    private void requestAccess() throws Exception
    {
        Toast.makeText(context, context.getString(R.string.allow_access_mapi), Toast.LENGTH_LONG).show();
        MessengerAPI.requestAccess().send();
    }

    private void installMAPI()
    {
        Toast.makeText(context, context.getString(R.string.install_mapi), Toast.LENGTH_LONG).show();
        MessengerAPI.openPlayStoreEntry();
    }

    private void installWhatsApp()
    {
        Toast.makeText(context, context.getString(R.string.install_whatsapp), Toast.LENGTH_LONG).show();
        final String appPackageName = PKG_WHATSAPP;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    protected boolean isOnByDefault() {
        return false;
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
    public String getDefaultPhrase() {
        return DEFAULT_PHRASE;
    }

    @Override
    public boolean isHandlingGoogleNowReset() {
        return true;
    }

    @Override
    public String getPredicateHint() {
        return context.getString(isMAPIAvailable() ? R.string.syntax_mapi : R.string.message);
    }

    @Override
    public void onResponseReceived(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras)
    {
        String strPredicate = m_mapPredicate.get(lBroadcastID);

        if(strPredicate != null && responseType == ResponseType.SUCCESS)
        {
            interpretMessage(Contact.fromBundle(extras), strPredicate);
        }
    }
}
