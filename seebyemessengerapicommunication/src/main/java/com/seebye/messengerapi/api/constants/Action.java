package com.seebye.messengerapi.api.constants;

/**
 * Created by Nico on 11.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public enum Action
{
	requestSecret

	, requestAccess

	, requestSendMessage

	, getContacts

	, REMOVED
	;


	public static Action fromOrdinal(int nOrdinal)
	{
		try
		{
			return Action.values()[nOrdinal];
		}
		catch(Exception e)
		{
			return REMOVED;
		}
	}
}
