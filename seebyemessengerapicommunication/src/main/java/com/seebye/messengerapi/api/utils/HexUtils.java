package com.seebye.messengerapi.api.utils;

/**
 * Created by Nico on 15.04.2015.
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
