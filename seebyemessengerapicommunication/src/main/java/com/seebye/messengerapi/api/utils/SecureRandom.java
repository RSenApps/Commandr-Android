package com.seebye.messengerapi.api.utils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by Nico on 12.04.2015.
 */
public class SecureRandom
{
	private static java.security.SecureRandom s_instanceSecureRandom = null;

	public static java.security.SecureRandom getInstance()
	{
		try
		{
			if(s_instanceSecureRandom == null)
			{
				s_instanceSecureRandom = java.security.SecureRandom.getInstance("SHA1PRNG");//.getInstance("SHA1PRNG", "SUN");
			}
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		/*catch(NoSuchProviderException e)
		{
			e.printStackTrace();
		}*/

		return s_instanceSecureRandom;
	}

	public static double get()
	{
		return getInstance().nextDouble();
	}
	public static long get(long lMin, long lMax)
	{
		return ((long)(get() * (lMax-lMin))) + lMin;
	}
}
