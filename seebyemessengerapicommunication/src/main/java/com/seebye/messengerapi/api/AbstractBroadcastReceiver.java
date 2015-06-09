package com.seebye.messengerapi.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.ErrorCode;
import com.seebye.messengerapi.api.constants.Extra;
import com.seebye.messengerapi.api.constants.General;
import com.seebye.messengerapi.api.constants.ResponseType;
import com.seebye.messengerapi.api.constants.SPKey;

/**
 * Created by Seebye on 11.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
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

	private void processResponse(Intent intent)
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

				if(responseType == ResponseType.ERROR
						&& extras.containsKey(Extra.ERROR_CODE.getKey())
						&& ErrorCode.DENIED == ErrorCode.fromOrdinal(extras.getInt(Extra.ERROR_CODE.getKey())))
				{
					App.getSPAPI().set(SPKey.ENABLED, false);
				}

				switch(Action.fromOrdinal(nAction))
				{
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
}
