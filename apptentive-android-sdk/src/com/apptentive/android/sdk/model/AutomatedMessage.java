/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import android.content.Context;
import android.content.res.Resources;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;

import org.json.JSONException;

/**
 * A dummy Message that the client can create on behalf of the server. It will never be sent to the server.
 *
 * @author Sky Kelsey
 */
public class AutomatedMessage extends TextMessage {

    private static final String KEY_TITLE = "title";

    public AutomatedMessage() {
        super();
    }

    public AutomatedMessage(String json) throws JSONException {
        super(json);
    }

    public static AutomatedMessage createAutoMessage(String title, String body) {
        AutomatedMessage message = new AutomatedMessage();
        message.setTitle(title);
        message.setBody(body);
        return message;
    }

    public static AutomatedMessage createWelcomeMessage(Context context) {
        Resources resources = context.getResources();
        String title = resources.getString(R.string.apptentive_give_feedback);
        String body = resources.getString(R.string.apptentive_message_auto_body_manual);
        return createAutoMessage(title, body);
    }

    public static AutomatedMessage createNoLoveMessage(Context context) {
        Resources resources = context.getResources();
        String title = resources.getString(R.string.apptentive_were_sorry);
        String body = resources.getString(R.string.apptentive_message_auto_body_no_love);
        return createAutoMessage(title, body);
    }

    @Override
    protected void initType() {
        setType(Type.AutomatedMessage);
    }

    public String getTitle() {
        try {
            return getString(KEY_TITLE);
        } catch (JSONException e) {
        }
        return null;
    }

    public void setTitle(String title) {
        try {
            put(KEY_TITLE, title);
        } catch (JSONException e) {
            Log.e("Unable to set title.");
        }
    }

}
