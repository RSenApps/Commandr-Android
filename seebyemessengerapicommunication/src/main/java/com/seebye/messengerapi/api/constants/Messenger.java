package com.seebye.messengerapi.api.constants;

import com.seebye.messengerapi.api.utils.BitUtils;

/**
 * Created by Nico on 18.01.2015.
 */
public enum Messenger
{
	WHATSAPP		(0b00000000000000000000000000000001)

	/* unsupported until now
	, THREEMA		(0b00000000000000000000000000000010)
	, VIBER			(0b00000000000000000000000000000100)
	, TELEGRAM		(0b00000000000000000000000000001000)*/
	;


	//public static final int[] LIST			= {WHATSAPP, THREEMA, VIBER, TELEGRAM};
	// use Messenger.values() instead

	static int[] s_anValuesFlags;

	static
	{
		/** initialize {@link #valuesFlags()} */
		Messenger aMessengers[] = values();
		s_anValuesFlags = new int[aMessengers.length];

		for(int i = 0; i < aMessengers.length; i++)
		{
			s_anValuesFlags[i] = aMessengers[i].getFlag();
		}
	}

	private int m_nFlag;

	private Messenger(int nFlag)
	{
		m_nFlag = nFlag;
	}

	public int getFlag()
	{
		return m_nFlag;
	}

	public static Messenger fromOrdinal(int nOrdinal)
	{
		return Messenger.values()[nOrdinal];
	}


	public static int[] valuesFlags()
	{
		return s_anValuesFlags;
	}

	public static Messenger fromFlag(int nFlag)
	{
		if(!BitUtils.onlyOneFlag(nFlag, valuesFlags()))
		{
			throw new RuntimeException("More than one flag was set");
		}

		Messenger aMessenger[] = values();
		Messenger ret = null;

		for(int i = 0;
				i < aMessenger.length
				&& ret == null
				; i++)
		{
			if(nFlag == aMessenger[i].getFlag())
			{
				ret = aMessenger[i];
			}
		}

		return ret;
	}
}
