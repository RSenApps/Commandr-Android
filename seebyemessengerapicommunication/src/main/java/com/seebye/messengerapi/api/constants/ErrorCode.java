package com.seebye.messengerapi.api.constants;

/**
 * Created by Nico on 19.04.2015.
 */
public enum ErrorCode
{
	/**
	 * Your modul isn't enabled.
	 */
	DENIED
	,
	/**
	 * The user restricted your modul.<br>
	 * You have no rights to perform this action.<br>
	 * <br>
	 * !! Don't annoy the user with AlertDialogs, etc. to turn off the restriction.<br>
	 * (You're still free to show a (deactivatable/onetime) notification or a message within your app.)<br>
	 * Note: Violating the terms of usage of this API will lead to the exclusion of the usage.
	 */
	RESTRICTED
	,
	/**
	 * User hasn't setup the messenger.<br>
	 * We're not able to perform this action.
	 */
	MESSENGER_SETUP_NEEDED
	,
	/**
	 * The messenger you want to perform the action on isn't installed.
	 */
	MESSENGER_NOT_INSTALLED
	,
	/**
	 * Some methods don't allow to pass more than one messenger.<br>
	 * E.g. {@link Action#requestSendMessage}
	 */
	MORE_THAN_ONE_MESSENGER_PASSED

	,
	/**
	 * possible response by {@link com.seebye.messengerapi.api.MessengerAPI#sendMessage}
	 */
	UNSUPPORTED_MEDIA_TYPE
	,
	/**
	 * possible response by {@link com.seebye.messengerapi.api.MessengerAPI#sendMessage}
	 */
	FILE_NOT_FOUND
	,
	/**
	 * possible response by {@link com.seebye.messengerapi.api.MessengerAPI#sendMessage}
	 * for all types except {@link MessageType#TEXT}
	 *
	 * (WhatsApp: We need to know the contact as we're going to use the share function)
	 */
	UNKNOWN_CONTACT
	,
	/**
	 * IDMessenger doesn't match the format of the ids of the messenger
	 * possible response by {@link com.seebye.messengerapi.api.MessengerAPI#sendMessage}
	 */
	INVALID_IDMESSENGER_FORMAT
	,
	/**
	 * Spam protection
	 * You're not allowed to send more than two X+ percent identical messages within 10 seconds.
	 * Also you're not allowed to send more than five X+ percent identical messages within 300 seconds.
	 *
	 * X = max(1/max(nLen1, nLen2), .1) = Math.max( ((float)1) / ((float)Math.max(nLen1, nLen2) , .1)
	 *
	 * -> "Okay", "Kay"
	 * X = max(1/max(4,3), .1) = max(1/4, .1) = .25
	 * => 75% identical messages
	 */
	SPAM_PROTECTION
	,
	/**
	 * Message Data missing -> Text / File location
	 */
	MISSING_MESSAGE_DATA
	;



	public static ErrorCode fromOrdinal(int nOrdinal)
	{
		return ErrorCode.values()[nOrdinal];
	}
}
