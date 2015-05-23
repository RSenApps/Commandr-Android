package com.seebye.messengerapi.api.utils;

/**
 * Created by Nico on 18.01.2015.
 */
public class BitUtils
{
	/**
	 * Checks if the bits of nFlag are set inside nValue
	 * Returns false for 0 as flag
	 *
	 * @param nValue
	 * @param nFlag
	 * @return true if the bits are set
	 */
	public static boolean isSet(int nValue, int nFlag)
	{
		return nFlag != 0 && (nValue & nFlag) == nFlag;
	}

	public static boolean onlyOneFlag(int nValue, int[] anFlags)
	{
		boolean bOnlyOne = true;

		for(int i = 0; i < anFlags.length && bOnlyOne; i++)
		{
			int nFlag = anFlags[i];

			if(isSet(nValue, nFlag) && (nValue ^ nFlag) != 0)
			{
				bOnlyOne = false;
			}
		}

		return bOnlyOne;
	}
}
