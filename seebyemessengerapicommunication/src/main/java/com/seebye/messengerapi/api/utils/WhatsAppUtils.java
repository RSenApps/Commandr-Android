package com.seebye.messengerapi.api.utils;

import com.seebye.messengerapi.api.constants.General;

import java.io.File;

/**
 * Created by Nico on 07.05.2015.
 */
public class WhatsAppUtils
{
	public static String getProfileImagePath(String strJID)
	{
		return new File(new File(General.PATH_MAPI_WA_PROFILE_IMAGES), strJID.split("@")[0]+".jpg").getAbsolutePath();
	}
}
