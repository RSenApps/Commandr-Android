package com.seebye.messengerapi.api.constants;

/**
 * Created by Seebye on 12.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
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