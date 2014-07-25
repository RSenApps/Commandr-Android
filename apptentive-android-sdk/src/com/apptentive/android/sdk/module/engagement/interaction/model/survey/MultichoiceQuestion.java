/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model.survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sky Kelsey.
 */
public class MultichoiceQuestion extends BaseQuestion {

    protected static final String KEY_MIN_SELECTIONS = "min_selections";
    protected static final String KEY_MAX_SELECTIONS = "max_selections";
    private static final String KEY_ANSWER_CHOICES = "answer_choices";

    public MultichoiceQuestion(String json) throws JSONException {
        super(json);
    }

    public int getType() {
        return QUESTION_TYPE_MULTICHOICE;
    }

    public int getMinSelections() {
        return optInt(KEY_MIN_SELECTIONS, 1);
    }

    public int getMaxSelections() {
        return optInt(KEY_MAX_SELECTIONS, 1);
    }

    public List<AnswerDefinition> getAnswerChoices() {
        List<AnswerDefinition> answerChoices = new ArrayList<AnswerDefinition>();
        try {
            JSONArray multichoiceChoices = getJSONArray(KEY_ANSWER_CHOICES);
            for (int i = 0; i < multichoiceChoices.length(); i++) {
                JSONObject answer = multichoiceChoices.optJSONObject(i);
                if (answer != null) {
                    answerChoices.add(new AnswerDefinition(answer.toString()));
                }
            }
            return answerChoices;
        } catch (JSONException e) {
        }
        return answerChoices;
    }
}
