package com.seebye.messengerapi.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.seebye.messengerapi.api.constants.General;
import com.seebye.messengerapi.api.constants.SPKey;
import com.seebye.messengerapi.api.utils.SecureRandom;

/**
 * Created by Seebye on 11.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class App extends android.support.multidex.MultiDexApplication
//Application
{
	private static App s_instance;

	@Override
	public void onCreate()
	{
		super.onCreate();
		s_instance = this;

		if(isModule())
		{
			InstallReceiver.register();

			if(MessengerAPI.isInstalled())
			{
				initializeModul();
			}
		}
	}

	public boolean isModule() { return true; }

	public static App getInstance()
	{
		return s_instance;
	}

	/**
	 * This method should be used by the API only.
	 */
	public static sp getSPAPI()
	{
		return sp.SP(App.getInstance(), "smapi");
	}

	private void initializeModul()
	{
		if(!App.getSPAPI().getBool(SPKey.SETUP))
		{
			App.getSPAPI().set(SPKey.SETUP, true);
			setup();
		}

		App.getSPAPI().set(SPKey.SECRET_REQUEST_ID, MessengerAPI.requestSecret().sendMoreExplicit().getID());
	}

	private void setup()
	{
		// we're going to set the last broadcast id to a random value
		// worst case scenario: Long.MAX_VALUE-Integer.MAX_VALUE = 9223372036854775807-2147483647 = 9 223 372 034 707 292 160 broadcast ids
		// aim = protect modules from fake broadcasts - example pass wrong secret to stop the app from working
		App.getSPAPI().set(SPKey.LAST_BROADCAST_ID, SecureRandom.get(Integer.MIN_VALUE, Integer.MAX_VALUE));
	}

	private static class InstallReceiver extends BroadcastReceiver
	{
		public static void register()
		{
			try
			{
				IntentFilter filter = new IntentFilter();
				filter.addAction(Intent.ACTION_PACKAGE_ADDED);
				filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
				filter.addDataScheme("package");
				App.getInstance().registerReceiver(new InstallReceiver(), filter);
			}
			catch(Exception e) {}
		}

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String strPackage = intent.getDataString() == null ? null : intent.getDataString().replaceAll("package:", "");

			if(intent.getAction() != null
					&& (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(Intent.ACTION_PACKAGE_DATA_CLEARED))
					&& General.PKG_MESSENGERAPI.equals(strPackage))
			{
				// our API was installed so let's request our secret to interact with it
				// (We're going to ignore that this broadcast is also send on updates)
				App.getInstance().initializeModul();

				if(intent.getAction().equals(Intent.ACTION_PACKAGE_DATA_CLEARED))
				{
					// Data of our API was cleared this module isn't enabled anymore
					getSPAPI().set(SPKey.ENABLED, false);
				}
			}
		}
	}
}
