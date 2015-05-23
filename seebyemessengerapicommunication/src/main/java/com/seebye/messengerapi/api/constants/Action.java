package com.seebye.messengerapi.api.constants;

/**
 * Created by Nico on 11.04.2015.
 */
public enum Action
{
	requestSecret

	, requestAccess

	, requestSendMessage

	, getContacts
	, getMessageCount
	, getLastMessages
	, getContactImage
	, syncContactImage
	, syncAllContactImages

	, informNewMessage
	, informConversationOpened
	,
	informEmojiButtonPressed
	, informAppComponentVisible
	;


	public static Action fromOrdinal(int nOrdinal)
	{
		return Action.values()[nOrdinal];
	}
}
