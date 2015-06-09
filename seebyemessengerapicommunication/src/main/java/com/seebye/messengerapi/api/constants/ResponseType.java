package com.seebye.messengerapi.api.constants;

/**
 * Created by Seebye on 19.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public enum ResponseType
{
	SUCCESS
	, ERROR;

	public static ResponseType fromOrdinal(int nOrdinal)
	{
		return ResponseType.values()[nOrdinal];
	}
}
