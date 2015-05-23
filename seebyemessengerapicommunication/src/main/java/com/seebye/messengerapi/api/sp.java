package com.seebye.messengerapi.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.seebye.messengerapi.api.constants.SPKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Nico on 29.05.13.
 */
public class sp
{
	private static HashMap<String, sp> s_instances = new HashMap<>();

	private SharedPreferences m_sp;


	public static sp SP(Context ctx, String strKey)
	{
		if(s_instances.get(strKey) == null)
		{
			s_instances.put(strKey, new sp(ctx, strKey));
		}

		return s_instances.get(strKey);
	}

	private sp(Context ctx, String strSPName)
	{
		m_sp = ctx.getApplicationContext().getSharedPreferences(strSPName, Context.MODE_PRIVATE);
	}

	public sp set(SPKey key, String strVal)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putString(key.getKey(), strVal);
		editor.apply();
		return this;
	}
	public sp set(SPKey key, boolean bVal)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putBoolean(key.getKey(), bVal);
		editor.apply();
		return this;
	}
	public sp set(SPKey key, int nVal)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putInt(key.getKey(), nVal);
		editor.apply();
		return this;
	}
	public sp set(SPKey key, long lVal)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putLong(key.getKey(), lVal);
		editor.apply();
		return this;
	}
	public sp set(SPKey key, float fVal)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putFloat(key.getKey(), fVal);
		editor.apply();
		return this;
	}

	public String getStr(SPKey key)
	{
		return m_sp.getString(key.getKey(), "");
	}
	public boolean getBool(SPKey key)
	{
		return m_sp.getBoolean(key.getKey(), false);
	}
	public int getInt(SPKey key)
	{
		return m_sp.getInt(key.getKey(), 0);
	}
	public long getLong(SPKey key)
	{
		return m_sp.getLong(key.getKey(), 0);
	}
	public float getFloat(SPKey key)
	{
		return m_sp.getFloat(key.getKey(), 0);
	}
}
