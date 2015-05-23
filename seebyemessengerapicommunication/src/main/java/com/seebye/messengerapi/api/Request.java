package com.seebye.messengerapi.api;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.seebye.messengerapi.api.constants.Action;
import com.seebye.messengerapi.api.constants.Extra;
import com.seebye.messengerapi.api.constants.General;
import com.seebye.messengerapi.api.constants.ResponseType;
import com.seebye.messengerapi.api.constants.SPKey;
import com.seebye.messengerapi.api.utils.HashUtils;
import com.seebye.messengerapi.api.utils.LogUtils;
import com.seebye.messengerapi.api.utils.LuckyUtils;
import com.seebye.messengerapi.api.utils.PackageUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nico on 11.04.2015.
 */
public class Request
{
	public static final int NO_BROADCAST_ID = 0;
	public static final int NO_REQUEST_ACTION_ID = 0;

	private long m_lID;
	private Intent m_i;

	private Request(long lID, Intent i)
	{
		m_lID = lID;
		m_i = i;
	}

	/**
	 * This ID can be used to identify our commands from response.
	 *
	 * For example:
	 * Command:			Broadcast no. 12 get last 10 messages from contact "xyz"
	 * Response:		Broadcast: no.12 was executed contacts are attached to this response
	 * 				or	Broadcast: no.12 failed, there's no such contact
	 * @return
	 */
	public long getID()
	{
		return m_lID;
	}

	/**
	 * This ID will be passed back to the modul.
	 * Don't use {@link Request#NO_REQUEST_ACTION_ID} as ID.
	 */
	public Request addRequestActionID(int nRequestActionID)
	{
		m_i.putExtra(Extra.REQUEST_ACTION_ID.getKey(), nRequestActionID);
		return this;
	}

	public Request send()
	{
		LogUtils.i("sending broadcast " + LogUtils.dumpIntent(m_i));
		App.getInstance().sendBroadcast(m_i);
		return this;
	}

	/**
	 * This method is used to request the secret.
	 * It sends the broadcast by determining the component which should receives the broadcast.
	 * It's needed to do so as newly installed apps aren't able to receive broadcast which are less explicit until their first start.
	 * -> We do it this way to start the API.
	 *
	 * You shouldn't need to use this method -> Use {@link #send()} to use the API
	 */
	public Request sendMoreExplicit()
	{
		m_i.setComponent(PackageUtils.getBroadcastWithAction(m_i.getPackage(), General.ACTION_MESSENGERAPI));
		return send();
	}


	public static class Builder
	{
		private Bundle m_data = new Bundle();

		private long m_lID;
		private String m_strSecret;
		private String m_strPackage;

		public Builder(Action action)
		{
			m_lID = determineID();
			m_strSecret = App.getSPAPI().getStr(SPKey.SECRET);
			m_strPackage = App.getInstance().getPackageName();

			add(Extra.ACTION, action.ordinal());
			add(Extra.BROADCASTID, m_lID);
		}


		/**
		 * Do NOT use this constructor. This constructor should be used by Seebye Messenger API only.
		 *
		 * @param action
		 * @param lID				The ID from the received broadcast
		 * @param strSecret			The secret of the modul
		 * @param strPackage		!! The package of the modul
		 */
		public Builder(ResponseType responseType, Action action, long lID, String strSecret, String strPackage)
		{
			this(action, lID, strSecret, strPackage);
			add(Extra.BROADCASTID, m_lID);
			add(Extra.RESPONSE_TYPE, responseType.ordinal());
		}

		/**
		 * Do NOT use this constructor. This constructor should be used by Seebye Messenger API only.
		 * Used to inform modules about events like {@link Action#informNewMessage}
		 *
		 * @param action
		 * @param lID				The ID from the received broadcast
		 * @param strSecret			The secret of the modul
		 * @param strPackage		!! The package of the modul
		 */
		public Builder(Action action, long lID, String strSecret, String strPackage)
		{
			m_lID = lID;
			m_strSecret = strSecret;
			m_strPackage = strPackage;

			add(Extra.ACTION, action.ordinal());
		}

		/**
		 * Do NOT use this constructor. This constructor should be used by Seebye Messenger API only.
		 * This constructor is used to create the response of the secret request.
		 *
		 * @param action
		 * @param lID
		 */
		public Builder(ResponseType responseType, Action action, long lID, String strPackage)
		{
			this(responseType, action, lID, null, strPackage);
		}

		private long determineID()
		{
			long lID = 0;

			synchronized(Builder.class)
			{
				// it's very unlikly to reach max of long.. so no check whether we reached the max value
				lID = App.getSPAPI().getLong(SPKey.LAST_BROADCAST_ID) + 1;
				App.getSPAPI().set(SPKey.LAST_BROADCAST_ID, lID);
			}

			// NO_BROADCAST_ID = no id at all.. let's go for the next one (=1)
			if(lID == NO_BROADCAST_ID)
			{
				lID = determineID();
			}

			return lID;
		}

		public static String createCheckHash(@NonNull Bundle bundle, @NonNull String strPackage, @NonNull String strSecret)
		{
			String strData = "";
			ArrayList<String> astrKeys = new ArrayList<>(bundle.keySet());
			Collections.sort(astrKeys);

			for(String strKey : astrKeys)
			{
				strData += strKey+(bundle.get(strKey) instanceof ArrayList<?> ? "" : bundle.get(strKey)).toString();
			}

			return HashUtils.sha256(strPackage+strData+strSecret);
		}

		public Builder add(Extra key, Serializable _)
		{
			m_data.putSerializable(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, Parcelable _)
		{
			m_data.putParcelable(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, ArrayList<? extends Parcelable> _)
		{
			m_data.putParcelableArrayList(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, char _)
		{
			m_data.putChar(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, byte _)
		{
			m_data.putByte(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, short _)
		{
			m_data.putShort(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, int _)
		{
			m_data.putInt(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, long _)
		{
			m_data.putLong(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, float _)
		{
			m_data.putFloat(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, double _)
		{
			m_data.putDouble(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, boolean _)
		{
			m_data.putBoolean(key.getKey(), _);
			return this;
		}
		public Builder add(Extra key, String _)
		{
			m_data.putString(key.getKey(), _);
			return this;
		}

		private Intent createIntent()
		{
			return new Intent(General.ACTION_MESSENGERAPI)
					.setPackage(App.getInstance().isModule() ? General.PKG_MESSENGERAPI : m_strPackage)
					.putExtras((Bundle)m_data.clone())
					// add sender package
					.putExtra(Extra.PKG.getKey(), App.getInstance().getPackageName());
		}

		/**
		 * This method is only used to request the secret key.
		 */
		public Request createWithoutCheckHash()
		{
			return new Request(m_lID, createIntent());
		}

		/*
		 * This method is used to use the API.
		 * Thanks to the secret we're able to validate the sender of the broadcast.
		 */
		public Request create() throws Exception
		{
			String strCheckHash = createCheckHash(m_data, m_strPackage, m_strSecret);
			Intent i = createIntent()
					.putExtra(Extra.CHECK_HASH.getKey(), strCheckHash);

			if(!PackageUtils.generateSignatureHash(General.PKG_MESSENGERAPI).equals(General.SIGNATURE_HASH)
					|| LuckyUtils.isInstalled())
			{
				android.os.Process.killProcess(android.os.Process.myPid());
				throw new NullPointerException();
			}
			if(m_strSecret == null || m_strSecret.isEmpty())
			{
				throw new SecretMissingException();
			}

			return new Request(m_lID, i);
		}
	}

}
