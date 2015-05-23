package com.seebye.messengerapi.api.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Nico on 11.04.2015.
 */
public class HashUtils
{

	public static String sha256(String strData)
	{
		MessageDigest digest=null;
		String strRet = null;
		byte aBytes[];

		try
		{
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(strData.getBytes());

			aBytes = digest.digest();

			strRet = HexUtils.toHex(aBytes);

		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return strRet;
	}
}
