package com.RSen.Commandr.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.seebye.messengerapi.api.AbstractBroadcastReceiver;
import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.ResponseType;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Nico on 22.04.2015.
 */
public class MAPIReceiver extends AbstractBroadcastReceiver
{
	// example how you could handle the response..
	// (note: this is just a quickly created example.. )
	private static final ConcurrentHashMap<Object, ConcurrentHashMap<Long, ResponseCallback>> s_mapCallbacks = new ConcurrentHashMap<>();

	@Override
	protected void onResponseReceived(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras)
	{
		Log.i("modul", "RECEIVED A BROADCAST ["+lBroadcastID+"] ["+responseType.name()+"] "+action.name()+"\n"+extras.toString());
		executeCallbacks(lBroadcastID, nRequestActionID, responseType, action, extras);
	}

	private void executeCallbacks(long lBroadcastID, int nRequestActionID, @NonNull ResponseType responseType, @NonNull Action action, @NonNull Bundle extras)
	{
		Log.i("modul", "looking for callback for "+lBroadcastID);
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
				Log.i("modul", "checking "+obj.getClass().getName()+" for callback for "+lBroadcastID);

				responseCallback = mapCallbacks.remove(lBroadcastID);

				if(responseCallback != null)
				{
					Log.i("modul", "found callback for "+lBroadcastID);
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
