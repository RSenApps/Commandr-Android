package com.seebye.messengerapi.api.constants;

/**
 * Created by Nico on 11.04.2015.
 * We're going to use enum as Android Studio supports autocomplete on typing the names
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
	, AMOUNT				("amount")
	, SKIP					("skip")
	, RESPONSE_TYPE			("resp_type")
	, ERROR_CODE			("err_code")
	, CONTACTS				("contacts")
	, MESSAGES_AMOUNT		("messages_amount")
	, MESSAGES				("messages")
	, LOCATION				("location")
	, REQUEST_ACTION_ID		("request_action_id")
	, CLASS					("class")
	, NAME					("name")
	, MESSAGE				("msg")
	, CONVERSATIONOPENED	("convopened")
	, RECT					("rect")
	, PKG2					("package2");

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
