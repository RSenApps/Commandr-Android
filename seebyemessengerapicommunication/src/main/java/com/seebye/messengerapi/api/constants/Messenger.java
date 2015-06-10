package com.seebye.messengerapi.api.constants;

/**
 * Created by Seebye on 18.01.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public enum Messenger
{
	WHATSAPP		(0b00000000000000000000000000000001)

	;

	private int m_nFlag;

	private Messenger(int nFlag)
	{
		m_nFlag = nFlag;
	}

	public int getFlag()
	{
		return m_nFlag;
	}
}
