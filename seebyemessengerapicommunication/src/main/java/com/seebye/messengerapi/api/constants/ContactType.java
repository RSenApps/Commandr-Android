package com.seebye.messengerapi.api.constants;

/**
 * Created by Nico on 20.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public enum  ContactType
{
	NORMAL
	, GROUP
	, BROADCAST;

	public static ContactType fromOrdinal(int nOrdinal)
	{
		return ContactType.values()[nOrdinal];
	}
}
