/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model.survey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sky Kelsey
 */
public class AnswerDefinition extends JSONObject {

    private static final String KEY_ID = "id";
    private static final String KEY_VALUE = "value";

    public AnswerDefinition(String json) throws JSONException {
        super(json);
    }

    public String getId() {
        return optString(KEY_ID, null);
    }

    public String getValue() {
        return optString(KEY_VALUE, null);
    }
}
