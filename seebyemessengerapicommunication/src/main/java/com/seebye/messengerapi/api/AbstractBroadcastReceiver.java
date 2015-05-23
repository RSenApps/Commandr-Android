package com.seebye.messengerapi.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.*;
import android.util.Log;

import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.Extra;
import com.seebye.messengerapi.api.constants.General;
import com.seebye.messengerapi.api.constants.Messenger;
import com.seebye.messengerapi.api.constants.ResponseType;
import com.seebye.messengerapi.api.constants.SPKey;
import com.seebye.messengerapi.api.utils.LogUtils;
import com.seebye.messengerapi.api.utils.PackageUtils;

/**
 * Created by Nico on 11.04.2015.
 */
public abstract class AbstractBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getExtras() != null)
		{
			processResponse(intent);
		}
	}

	public void processResponse(Intent intent)
	{
		Bundle extras = intent.getExtras();
		int nRequestActionID = extras.getInt(Extra.REQUEST_ACTION_ID.getKey(), -1);
		int nAction = extras.getInt(Extra.ACTION.getKey(), -1);
		long lBroadcastID = extras.getLong(Extra.BROADCASTID.getKey(), -1);
		String strCheckHash = extras.getString(Extra.CHECK_HASH.getKey(), null);
		String strSecret = App.getSPAPI().getStr(SPKey.SECRET);
		ResponseType responseType = ResponseType.fromOrdinal(extras.getInt(Extra.RESPONSE_TYPE.getKey()));

		extras.remove(Extra.PKG.getKey());
		extras.remove(Extra.CHECK_HASH.getKey());

		LogUtils.i("BROADCAST received " + LogUtils.dumpIntent(intent));


		if(intent.getAction().equals(General.ACTION_MESSENGERAPI)
				&& nAction != -1)
		{
			if(nAction == Action.requestSecret.ordinal()
					&& extras.containsKey(Extra.SECRET.getKey())

					// no need to check responseType only successful generations of secrets will be sent
					// ensure it's the response from the request we sent
					&& lBroadcastID == App.getSPAPI().getLong(SPKey.SECRET_REQUEST_ID)
					&& App.getSPAPI().getBool(SPKey.WAITING_FOR_SECRET))
			{
				LogUtils.i("secret received "+intent.toString());
				onResponseSecret(extras.getString(Extra.SECRET.getKey()));
			}
			else if(strCheckHash != null
					&& strSecret != null
					&& strCheckHash.equals(Request.Builder.createCheckHash(extras, App.getInstance().getPackageName(), strSecret)))
			{
				if(nAction == Action.requestAccess.ordinal())
				{
					onEnabledStateChanged(responseType);
				}

				switch(Action.fromOrdinal(nAction))
				{
					case informAppComponentVisible:
						onAppComponentBecomesVisible(extras.getString(Extra.PKG2.getKey()), extras.getString(Extra.CLASS.getKey()));
						break;
					case informConversationOpened:
						onConversationOpened(Messenger.fromFlag(extras.getInt(Extra.MESSENGER.getKey()))
								, extras.getString(Extra.ID_MESSENGER.getKey())
								, extras.getString(Extra.NAME.getKey()));
						break;
					case informEmojiButtonPressed:
						onEmojiButtonPressed(Messenger.fromFlag(extras.getInt(Extra.MESSENGER.getKey()))
								, extras.getString(Extra.ID_MESSENGER.getKey())
								, (Rect)extras.getParcelable(Extra.RECT.getKey()));
						break;
					case informNewMessage:
						onNewMessage(Messenger.fromFlag(extras.getInt(Extra.MESSENGER.getKey()))
								, extras.getString(Extra.ID_MESSENGER.getKey())
								, (Message)extras.getParcelable(Extra.MESSAGE.getKey())
								, extras.getBoolean(Extra.CONVERSATIONOPENED.getKey()));
						break;
					default:
						onResponseReceived(lBroadcastID, nRequestActionID, responseType, Action.fromOrdinal(nAction), extras);
						break;
				}
			}
		}
	}

	private void onResponseSecret(@NonNull String strSecret)
	{
		App.getSPAPI().set(SPKey.SECRET, strSecret);
		App.getSPAPI().set(SPKey.WAITING_FOR_SECRET, false);
	}

	private void onEnabledStateChanged(ResponseType responseType)
	{
		App.getSPAPI().set(SPKey.ENABLED, responseType == ResponseType.SUCCESS);
	}


	// Methods which have to be overridden

	/**
	 * Called on receiving a response to a command
	 */
	protected abstract void onResponseReceived(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras);





	// Methods which can be overridden
	// these methods receive global events..
	// !! not responses from commands

	/**
	 * Called on new messages
	 *
	 * This method informs about received messages and sent messages.
	 * This method won't be called by messages sent by e.g. Android Wear as we're unable to detect these messages.
	 *
	 * @param messenger				The used messenger.
	 * @param strIDMessenger		The ID of the contact, given by the messenger.
	 * @param msg   				The message which was sent or received
	 * @param bConversationOpened
	 */
	protected void onNewMessage(@NonNull Messenger messenger, @NonNull String strIDMessenger, @NonNull Message msg, boolean bConversationOpened) {}

	/**
	 * Called on opening a conversation.
	 *
	 * @param messenger				The used messenger.
	 * @param strIDMessenger		The ID of the contact, given by the messenger. Null if we don't know the contact.
	 * @param strName				The name of the contact.
	 */
	protected void onConversationOpened(@NonNull Messenger messenger, @Nullable String strIDMessenger, @NonNull String strName) {}

	/**
	 * Called on pressing the Smiley Button
	 *
	 * @param messenger				The used messenger.
	 * @param strIDMessenger		The ID of the contact, given by the messenger. Null if we don't know the contact.
	 * @param rect		Size of the smiley overview
	 */
	protected void onEmojiButtonPressed(@NonNull Messenger messenger, @Nullable String strIDMessenger, @NonNull Rect rect) {}

	/**
	 * As Google decides to take away our possibility to determine the current displayed app I'm going to offer a new one.
	 *
	 * @param strPackage		The package of the app which becomes visible.
	 * @param strClass			The class of the component of the app which becomes visible.
	 *                          For example: The class of the activity or the class of the view (=> floating component).
	 */
	protected void onAppComponentBecomesVisible(@NonNull String strPackage, @NonNull String strClass) {}
}
