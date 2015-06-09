package com.seebye.messengerapi.api.constants;

/**
 * Created by Seebye on 11.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public enum Extra
{
	CHECK_HASH				("checkhash")
	,
	/**
	 * Don't use this extra.
	 */
	PKG					("package")
	, ACTION				("action")
	, BROADCASTID			("id")
	, SECRET_OLD			("oldsecret")
	, SECRET				("secret")
	, MESSENGER				("messenger")
	, ID_MESSENGER			("id_messenger")
	, MESSAGE_TYPE			("message_type")
	, DATA					("data")
	, RESPONSE_TYPE			("resp_type")
	, ERROR_CODE			("err_code")
	, CONTACTS				("contacts")
	, REQUEST_ACTION_ID		("request_action_id");

	private String m_strKey;

	private Extra(String strKey)
	{
		m_strKey = strKey;
	}

	public String getKey()
	{
		return m_strKey;
	}
}
