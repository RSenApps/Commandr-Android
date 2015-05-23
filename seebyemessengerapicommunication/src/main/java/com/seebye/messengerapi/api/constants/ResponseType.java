package com.seebye.messengerapi.api.constants;

/**
 * Created by Nico on 19.04.2015.
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
