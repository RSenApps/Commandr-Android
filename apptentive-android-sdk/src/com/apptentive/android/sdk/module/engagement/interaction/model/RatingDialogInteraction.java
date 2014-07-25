/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model;

import android.content.Context;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.Configuration;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class RatingDialogInteraction extends Interaction {

    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_RATE_TEXT = "rate_text";
    private static final String KEY_REMIND_TEXT = "remind_text";
    private static final String KEY_DECLINE_TEXT = "decline_text";

    public RatingDialogInteraction(String json) throws JSONException {
        super(json);
    }

    public String getTitle() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_TITLE)) {
            return configuration.optString(KEY_TITLE, null);
        }
        return null;
    }

    public String getBody(Context context) {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_BODY)) {
            return configuration.optString(KEY_BODY, null);
        }
        return String.format(context.getResources().getString(R.string.apptentive_rating_message_fs), Configuration.load(context).getAppDisplayName());
    }

    public String getRateText(Context context) {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_RATE_TEXT)) {
            return configuration.optString(KEY_RATE_TEXT, null);
        }
        return String.format(context.getResources().getString(R.string.apptentive_rate_this_app), Configuration.load(context).getAppDisplayName());
    }

    public String getRemindText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_REMIND_TEXT)) {
            return configuration.optString(KEY_REMIND_TEXT, null);
        }
        return null;
    }

    public String getDeclineText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_DECLINE_TEXT)) {
            return configuration.optString(KEY_DECLINE_TEXT, null);
        }
        return null;
    }
}
