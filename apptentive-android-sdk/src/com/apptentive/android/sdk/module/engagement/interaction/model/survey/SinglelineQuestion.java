/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model.survey;

import org.json.JSONException;

/**
 * @author Sky Kelsey.
 */
public class SinglelineQuestion extends BaseQuestion {

    private static final String KEY_MULTILINE = "multiline";

    public SinglelineQuestion(String json) throws JSONException {
        super(json);
    }

    public int getType() {
        return QUESTION_TYPE_SINGLELINE;
    }

    public boolean isMultiLine() {
        return optBoolean(KEY_MULTILINE, false);
    }
}
