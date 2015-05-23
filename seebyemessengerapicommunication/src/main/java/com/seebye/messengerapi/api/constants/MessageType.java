package com.seebye.messengerapi.api.constants;

import com.seebye.messengerapi.api.utils.BitUtils;

/**
 * Created by Nico on 12.04.2015.
 */
public enum MessageType
{
	TEXT
	, IMAGE
	, AUDIO
	, VOICEMEMO
	, VIDEO
	, LOCATION
	, CONTACT
	, UNKNOWN;

	public static MessageType fromOrdinal(int nOrdinal)
	{
		return MessageType.values()[nOrdinal];
	}
}