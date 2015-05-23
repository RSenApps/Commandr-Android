package com.seebye.messengerapi.api.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.seebye.messengerapi.api.BuildConfig;

/**
 * Created by Nico on 03.05.2015.
 */
public class LogUtils
{
	public static void i(String strText, Object... aArgs)
	{
		if(BuildConfig.DEBUG)
		{
			Log.i("MAPIM", String.format(strText, aArgs));
		}
	}

	public static String dumpIntent(Intent intent)
	{
		String strDump = "";
		for (String key : intent.getExtras().keySet()) {
			Object value = intent.getExtras().get(key);
			strDump += String.format("\n%s %s (%s)", key, value.toString(), value.getClass().getName());
		}

		return intent.toString() + strDump;
	}
}
