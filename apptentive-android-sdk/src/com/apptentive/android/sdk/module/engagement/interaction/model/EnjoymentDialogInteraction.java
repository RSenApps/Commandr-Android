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
public class EnjoymentDialogInteraction extends Interaction {

    private static final String KEY_TITLE = "title";
    private static final String KEY_YES_TEXT = "yes_text";
    private static final String KEY_NO_TEXT = "no_text";

    public EnjoymentDialogInteraction(String json) throws JSONException {
        super(json);
    }

    public String getTitle(Context context) {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_TITLE)) {
            return configuration.optString(KEY_TITLE, null);
        }
        return context.getResources().getString(R.string.apptentive_intro_dialog_body, Configuration.load(context).getAppDisplayName());
    }

    public String getYesText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_YES_TEXT)) {
            return configuration.optString(KEY_YES_TEXT, null);
        }
        return null;
    }

    public String getNoText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_NO_TEXT)) {
            return configuration.optString(KEY_NO_TEXT, null);
        }
        return null;
    }
}
