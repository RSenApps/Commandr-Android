/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model.survey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sky Kelsey.
 */
abstract public class BaseQuestion extends JSONObject implements Question {

    public static final String KEY_NAME = "question";
    public static final String KEY_ID = "id";
    private static final String KEY_VALUE = "value";
    private static final String KEY_REQUIRED = "required";
    private static final String KEY_INSTRUCTIONS = "instructions";

    // Internal state that doesn't exist on the server.
    private static final String KEY_ANSWERS = "answers";
    private static final String KEY_METRIC_SENT = "metric_sent";

    protected BaseQuestion(String json) throws JSONException {
        super(json);
    }

    public abstract int getType();

    public String getId() {
        return optString(KEY_ID, null);
    }

    public String getValue() {
        return optString(KEY_VALUE, null);
    }

    public boolean isRequired() {
        return optBoolean(KEY_REQUIRED, false);
    }

    public String getInstructions() {
        return optString(KEY_INSTRUCTIONS, null);
    }

    public int getMinSelections() {
        return 1;
    }

    public int getMaxSelections() {
        return 1;
    }
}
