/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.util;

import android.view.ViewGroup;

/**
 * @author Sky Kelsey
 */
public class Constants {

    public static final String APPTENTIVE_SDK_VERSION = "1.5.0";


    public static final int REQUEST_CODE_PHOTO_FROM_MESSAGE_CENTER = 1000;

    public static final String PREF_NAME = "APPTENTIVE";

    public static final String PREF_KEY_CONVERSATION_TOKEN = "conversationToken";
    public static final String PREF_KEY_CONVERSATION_ID = "conversationId";
    public static final String PREF_KEY_PERSON_ID = "personId";

    public static final String PREF_KEY_DEVICE = "device";
    public static final String PREF_KEY_DEVICE_DATA = "deviceData";

    public static final String PREF_KEY_DEVICE_INTEGRATION_CONFIG = "integrationConfig";

    public static final String PREF_KEY_SDK = "sdk";
    public static final String PREF_KEY_APP_RELEASE = "app_release";
    public static final String PREF_KEY_PERSON = "person";
    public static final String PREF_KEY_PERSON_DATA = "personData";
    public static final String PREF_KEY_PERSON_INITIAL_EMAIL = "personInitialEmail";
    public static final String PREF_KEY_PERSON_EMAIL = "personEmail";

    public static final String PREF_KEY_APP_ACTIVITY_STATE_QUEUE = "appActivityStateQueue";

    public static final String PREF_KEY_APP_MAIN_ACTIVITY_NAME = "mainActivityName";

    public static final String PREF_KEY_AUTO_MESSAGE_SHOWN_AUTO_MESSAGE = "autoMessageShownAutoMessage";
    public static final String PREF_KEY_AUTO_MESSAGE_SHOWN_NO_LOVE = "autoMessageShownNoLove";
    public static final String PREF_KEY_AUTO_MESSAGE_SHOWN_MANUAL = "autoMessageShownManual";
    public static final String PREF_KEY_MESSAGE_CENTER_SHOULD_SHOW_INTRO_DIALOG = "messageCenterShouldShowIntroDialog";

    public static final String PREF_KEY_APP_CONFIG_PREFIX = "appConfiguration.";
    public static final String PREF_KEY_APP_CONFIG_JSON = PREF_KEY_APP_CONFIG_PREFIX + "json";
    // OLD KEYS USED IN PREVIOUS SDK VERSIONS
    public static final String PREF_KEY_APP_CONFIG_EXPIRATION = PREF_KEY_APP_CONFIG_PREFIX + "cache-expiration";
    public static final String PREF_KEY_SURVEYS = "surveys";
    public static final String PREF_KEY_SURVEYS_CACHE_EXPIRATION = "surveyCacheExpiration";
    public static final String PREF_KEY_SURVEYS_HISTORY = "surveyHistory";
    public static final String PREF_KEY_VERSION_HISTORY = "versionHistory";
    public static final String PREF_KEY_PENDING_PUSH_NOTIFICATION = "pendingPushNotification";
    // Engagement
    public static final String PREF_KEY_INTERACTIONS = "interactions";
    public static final String PREF_KEY_INTERACTIONS_CACHE_EXPIRATION = "interactionsCacheExpiration";
    public static final String PREF_KEY_CODE_POINT_STORE = "codePointStore";
    // Config Defaults
    public static final int CONFIG_DEFAULT_INTERACTION_CACHE_EXPIRATION_DURATION_SECONDS = 28800; // 8 hours
    public static final int CONFIG_DEFAULT_SURVEY_CACHE_EXPIRATION_DURATION_SECONDS = 86400; // 24 hours
    public static final int CONFIG_DEFAULT_APP_CONFIG_EXPIRATION_MILLIS = 0;
    public static final int CONFIG_DEFAULT_APP_CONFIG_EXPIRATION_DURATION_SECONDS = 86400; // 24 hours
    public static final int CONFIG_DEFAULT_DAYS_BEFORE_PROMPT = 30;
    public static final int CONFIG_DEFAULT_USES_BEFORE_PROMPT = 5;
    public static final int CONFIG_DEFAULT_SIGNIFICANT_EVENTS_BEFORE_PROMPT = 10;
    public static final int CONFIG_DEFAULT_DAYS_BEFORE_REPROMPTING = 5;
    public static final String CONFIG_DEFAULT_RATING_PROMPT_LOGIC = "{\"and\": [\"uses\",\"days\",\"events\"]}";
    public static final int CONFIG_DEFAULT_MESSAGE_CENTER_FG_POLL_SECONDS = 15;
    public static final int CONFIG_DEFAULT_MESSAGE_CENTER_BG_POLL_SECONDS = 60;
    public static final boolean CONFIG_DEFAULT_MESSAGE_CENTER_ENABLED = true;
    public static final boolean CONFIG_DEFAULT_MESSAGE_CENTER_EMAIL_REQUIRED = false;
    // Manifest keys
    public static final String MANIFEST_KEY_APPTENTIVE_DEBUG = "apptentive_debug";
    public static final String MANIFEST_KEY_APPTENTIVE_API_KEY = "apptentive_api_key";
    public static final String MANIFEST_KEY_SDK_DISTRIBUTION = "apptentive_sdk_distribution";
    public static final String MANIFEST_KEY_SDK_DISTRIBUTION_VERSION = "apptentive_sdk_distribution_version";
    public static final String MANIFEST_KEY_MESSAGE_CENTER_ENABLED = "apptentive_message_center_enabled";
    public static final String MANIFEST_KEY_EMAIL_REQUIRED = "apptentive_email_required";
    // View layout shortcuts
    public static final ViewGroup.LayoutParams ROW_LAYOUT = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    public static final ViewGroup.LayoutParams ITEM_LAYOUT = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    public static final String PREF_KEY_USER_ENTERED_EMAIL = "userEnteredEmail";
    public static final String PREF_KEY_APP_VERSION_CODE = "app_version_code";
    public static final String PREF_KEY_APP_VERSION_NAME = "app_version_name";
    public static final String PREF_KEY_DEVICE_DATA_SENT = "deviceDataSent"; // Keeps track of whether we have ever sent device data.
    public static final String PREF_KEY_START_OF_RATING_PERIOD = "startOfRatingPeriod";
    public static final String PREF_KEY_RATING_STATE = "ratingState";
    public static final String PREF_KEY_RATING_EVENTS = "events";
    public static final String PREF_KEY_RATING_USES = "uses";

    /**
     * A list of mobile carrier network types as Strings.
     * From {@link android.telephony.TelephonyManager TelephonyManager}
     *
     * @see android.telephony.TelephonyManager
     */
    private static final String[] networkTypeLookup = {
            "UNKNOWN", //  0
            "GPRS",    //  1
            "EDGE",    //  2
            "UMTS",    //  3
            "CDMA",    //  4
            "EVDO_0",  //  5
            "EVDO_A",  //  6
            "1xRTT",   //  7
            "HSDPA",   //  8
            "HSUPA",   //  9
            "HSPA",    // 10
            "IDEN",    // 11
            "EVDO_B",  // 12
            "LTE",     // 13
            "EHRPD",   // 14
            "HSPAP"    // 15
    };

    public static String networkTypeAsString(int networkTypeAsInt) {
        try {
            return networkTypeLookup[networkTypeAsInt];
        } catch (ArrayIndexOutOfBoundsException e) {
            return networkTypeLookup[0];
        }
    }

}
