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
public class FeedbackDialogInteraction extends Interaction {

    private static final String KEY_ASK_FOR_EMAIL = "ask_for_email";
    private static final String KEY_EMAIL_REQUIRED = "email_required";
    private static final String KEY_MESSAGE_CENTER_ENABLED = "message_center_enabled";

    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_EMAIL_HINT_TEXT = "email_hint_text";
    private static final String KEY_MESSAGE_HINT_TEXT = "message_hint_text";
    private static final String KEY_DECLINE_TEXT = "decline_text";
    private static final String KEY_SUBMIT_TEXT = "submit_text";

    private static final String KEY_THANK_YOU_TITLE = "thank_you_title";
    private static final String KEY_THANK_YOU_BODY = "thank_you_body";
    private static final String KEY_THANK_YOU_CLOSE_TEXT = "thank_you_close_text";
    private static final String KEY_THANK_YOU_VIEW_MESSAGES_TEXT = "thank_you_view_messages_text";

    public FeedbackDialogInteraction(String json) throws JSONException {
        super(json);
    }

    public boolean isAskForEmail() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_ASK_FOR_EMAIL)) {
            return configuration.optBoolean(KEY_ASK_FOR_EMAIL, true);
        }
        return true;
    }

    public boolean isEmailRequired() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_EMAIL_REQUIRED)) {
            return configuration.optBoolean(KEY_EMAIL_REQUIRED, false);
        }
        return false;
    }

    public boolean isMessageCenterEnabled() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_MESSAGE_CENTER_ENABLED)) {
            return configuration.optBoolean(KEY_MESSAGE_CENTER_ENABLED, true);
        }
        return true;
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
        return context.getResources().getString(R.string.apptentive_intro_dialog_body, Configuration.load(context).getAppDisplayName());
    }

    public String getEmailHintText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_EMAIL_HINT_TEXT)) {
            return configuration.optString(KEY_EMAIL_HINT_TEXT, null);
        }
        return null;
    }

    public String getMessageHintText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_MESSAGE_HINT_TEXT)) {
            return configuration.optString(KEY_MESSAGE_HINT_TEXT, null);
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

    public String getSubmitText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_SUBMIT_TEXT)) {
            return configuration.optString(KEY_SUBMIT_TEXT, null);
        }
        return null;
    }

    public String getThankYouTitle() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_THANK_YOU_TITLE)) {
            return configuration.optString(KEY_THANK_YOU_TITLE, null);
        }
        return null;
    }

    public String getThankYouBody() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_THANK_YOU_BODY)) {
            return configuration.optString(KEY_THANK_YOU_BODY, null);
        }
        return null;
    }

    public String getThankYouCloseText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_THANK_YOU_CLOSE_TEXT)) {
            return configuration.optString(KEY_THANK_YOU_CLOSE_TEXT, null);
        }
        return null;
    }

    public String getThankYouViewMessagesText() {
        InteractionConfiguration configuration = getConfiguration();
        if (configuration != null && !configuration.isNull(KEY_THANK_YOU_VIEW_MESSAGES_TEXT)) {
            return configuration.optString(KEY_THANK_YOU_VIEW_MESSAGES_TEXT, null);
        }
        return null;
    }
}
