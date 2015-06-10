package com.RSen.Commandr.core;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.seebye.messengerapi.api.AbstractBroadcastReceiver;
import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.ResponseType;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Seebye on 22.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class MAPIReceiver extends AbstractBroadcastReceiver
{
	private static final ConcurrentHashMap<Object, ConcurrentHashMap<Long, ResponseCallback>> s_mapCallbacks = new ConcurrentHashMap<>();

	@Override
	protected void onResponseReceived(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras)
	{
		executeCallbacks(lBroadcastID, nRequestActionID, responseType, action, extras);
	}

	private void executeCallbacks(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras)
	{
		synchronized(s_mapCallbacks)
		{
			Set<Object> aObjKeys = s_mapCallbacks.keySet();
			Iterator<Object> i = aObjKeys.iterator();
			Object obj;
			ConcurrentHashMap<Long, ResponseCallback> mapCallbacks;
			ResponseCallback responseCallback;

			while(i.hasNext())
			{
				obj = i.next();
				mapCallbacks = s_mapCallbacks.get(obj);

				responseCallback = mapCallbacks.remove(lBroadcastID);

				if(responseCallback != null)
				{
					responseCallback.onResponseReceived(lBroadcastID, nRequestActionID, responseType, action, extras);
				}

				if(mapCallbacks.isEmpty())
				{
					i.remove();
				}
			}
		}
	}

	public static void registerRequest(@NonNull Object objOwner, long lBroadcastID, @NonNull ResponseCallback callback)
	{
		synchronized(s_mapCallbacks)
		{
			ConcurrentHashMap<Long, ResponseCallback> callbacks = s_mapCallbacks.get(objOwner);
			if(callbacks == null)
			{
				callbacks = new ConcurrentHashMap<>();
				s_mapCallbacks.put(objOwner, callbacks);
			}

			callbacks.put(lBroadcastID, callback);
		}
	}

	public static void unregisterAllRequest(@NonNull Object objOwner)
	{
		synchronized(s_mapCallbacks)
		{
			s_mapCallbacks.remove(objOwner);
		}
	}
	
	public static interface ResponseCallback
	{
		public void onResponseReceived(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras);
	}
}
