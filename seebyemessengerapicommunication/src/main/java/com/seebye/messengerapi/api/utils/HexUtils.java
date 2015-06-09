package com.seebye.messengerapi.api.utils;

/**
 * Created by Seebye on 15.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class HexUtils
{
	public static String toHex(byte aBytes[])
	{
		StringBuffer strBuffer = new StringBuffer();

		for (byte by : aBytes)
		{
			String h = Integer.toHexString(0xFF & by);

			while (h.length() < 2)
			{
				h = "0" + h;
			}

			strBuffer.append(h);
		}

		return strBuffer.toString();
	}
}
