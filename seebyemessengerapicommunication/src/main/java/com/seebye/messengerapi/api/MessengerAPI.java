package com.seebye.messengerapi.api;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.Extra;
import com.seebye.messengerapi.api.constants.General;
import com.seebye.messengerapi.api.constants.MessageType;
import com.seebye.messengerapi.api.constants.Messenger;
import com.seebye.messengerapi.api.constants.SPKey;
import com.seebye.messengerapi.api.utils.PackageUtils;

/**
 * Created by Seebye on 11.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class MessengerAPI
{
	/**
	 * This method should not wake your interest as long as your Application class is a subclass from {@link App}
	 *
	 * Sends a request to Seebye Messenger API to check whether our current secret is the same like the created one by it.
	 * If it differs, the generated one will send to this modul by a explicit Broadcast.
	 * -> No other app will know the secret.
	 * .. We're always using explicit Broadcasts..
	 *
	 * We're going to save the Broadcast ID, so nobody get's the idea to disrupt the moduls..
	 * (We will do a check on receiving the answer)
	 *
	 */
	public static Request requestSecret()
	{
		App.getSPAPI().set(SPKey.WAITING_FOR_SECRET, true);
		return new Request.Builder(Action.requestSecret).add(Extra.SECRET_OLD, App.getSPAPI().getStr(SPKey.SECRET))
				.createWithoutCheckHash();
	}

	/**
	 * Ask the User to grant access to the API.
	 *
	 * @return The id of the broadcast.
	 * @throws Exception
	 */
	public static Request requestAccess() throws Exception
	{
		return new Request.Builder(Action.requestAccess)
				.create();
	}

	/**
	 * Determines whether the user allowed us to interact with the API.
	 */
	public static boolean isEnabled()
	{
		return App.getSPAPI().getBool(SPKey.ENABLED);
	}

	/**
	 * Determines whether the API is installed or not.
	 */
	public static boolean isInstalled()
	{
		return PackageUtils.exists(General.PKG_MESSENGERAPI);
	}

	/**
	 * Opens the Play Store-site of Seebye Messenger API
	 */
	public static void openPlayStoreEntry()
	{
		App.getInstance().startActivity(new Intent(Intent.ACTION_VIEW)
				.setData(Uri.parse("market://details?id=" + General.PKG_MESSENGERAPI+"&referrer=utm_source%3D"+App.getInstance().getPackageName()+"%26utm_medium%3DAPIMethod%26utm_campaign%3DAPICampaign"))
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	private static void checkIDMessenger(String strIDMessenger) throws Exception
	{
		if(strIDMessenger == null || strIDMessenger.isEmpty())
		{
			throw  new Exception("IDMessenger is invalid");
		}
	}

	/**
	 * Sends a message to the contact of the passed ID.
	 *
	 * @param messenger			The messenger which should be used.
	 * @param strIDMessenger	The ID of the contact, given by the messenger.
	 * @param type				The type of the message. Note: Unknown is not a valid type.
	 * @param strData			The data to send. This should be the text for text messages otherwise the location of the file.
	 *
	 * @return The id of the broadcast.
	 * @throws Exception
	 */
	public static Request sendMessage(@NonNull Messenger messenger, @NonNull String strIDMessenger, @NonNull MessageType type, @NonNull String strData) throws
			Exception
	{
		checkIDMessenger(strIDMessenger);

		if(type == MessageType.UNKNOWN)
		{
			throw new Exception("Invalid MessageType: "+type.name());
		}

		return new Request.Builder(Action.requestSendMessage)
				.add(Extra.MESSENGER, messenger.getFlag())
				.add(Extra.ID_MESSENGER, strIDMessenger)
				.add(Extra.MESSAGE_TYPE, type.ordinal())
				.add(Extra.DATA, strData)
				.create();
	}

	/**
	 * Loads all contacts from the given messengers.
	 *
	 * @param nMessengers	The flags from the messenger to load the contact from. Example for the future: WHATSAPP.getFlag() | THREEMA.getFlag()
	 *
	 * @return The id of the broadcast.
	 * @throws Exception
	 */
	public static Request getContacts(int nMessengers) throws Exception
	{
		return new Request.Builder(Action.getContacts)
				.add(Extra.MESSENGER, nMessengers)
				.create();
	}
}
