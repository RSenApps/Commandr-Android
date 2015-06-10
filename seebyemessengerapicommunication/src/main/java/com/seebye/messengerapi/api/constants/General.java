package com.seebye.messengerapi.api.constants;

import com.seebye.messengerapi.api.BuildConfig;

/**
 * Created by Seebye on 11.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class General
{
	public static final String PKG_MESSENGERAPI				= BuildConfig.PKG_MESSENGERAPI;
	public static final String ACTION_MESSENGERAPI			= BuildConfig.ACTION_MESSENGERAPI;
	public static final String PERMISSION_MESSENGERAPI		= BuildConfig.PERMISSION_MESSENGERAPI;
	public static final String SIGNATURE_HASH				= BuildConfig.SIGNATURE_HASH_MAPI;

	public static final String PATH_MAPI_WA_PROFILE_IMAGES	= "/sdcard/Android/data/"+ General.PKG_MESSENGERAPI+"/files/Profile Pictures";
}
