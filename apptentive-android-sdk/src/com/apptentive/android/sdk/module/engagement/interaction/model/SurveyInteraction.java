/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model;

import com.apptentive.android.sdk.module.engagement.interaction.model.survey.MultichoiceQuestion;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.MultiselectQuestion;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.Question;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.SinglelineQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sky Kelsey
 */
public class SurveyInteraction extends Interaction {

    // Configuration
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SHOW_SUCCESS_MESSAGE = "show_success_message";
    private static final String KEY_SUCCESS_MESSAGE = "success_message";
    private static final String KEY_QUESTIONS = "questions";

    public SurveyInteraction(String json) throws JSONException {
        super(json);
    }

    public String getName() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_NAME)) {
                return configuration.getString(KEY_NAME);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public String getDescription() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_DESCRIPTION)) {
                return configuration.getString(KEY_DESCRIPTION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public boolean isShowSuccessMessage() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_SHOW_SUCCESS_MESSAGE)) {
                return configuration.getBoolean(KEY_SHOW_SUCCESS_MESSAGE);
            }
        } catch (JSONException e) {
        }
        return false;
    }

    public String getSuccessMessage() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_SUCCESS_MESSAGE)) {
                return configuration.getString(KEY_SUCCESS_MESSAGE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public List<Question> getQuestions() {
        try {
            InteractionConfiguration configuration = getConfiguration();
            if (configuration != null && configuration.has(KEY_QUESTIONS)) {
                List<Question> questions = new ArrayList<Question>();
                JSONArray questionsArray = configuration.getJSONArray(KEY_QUESTIONS);
                for (int i = 0; i < questionsArray.length(); i++) {
                    JSONObject questionJson = (JSONObject) questionsArray.get(i);
                    Question.Type type = Question.Type.valueOf(questionJson.getString("type"));
                    Question question = null;
                    switch (type) {
                        case singleline:
                            question = new SinglelineQuestion(questionJson.toString());
                            break;
                        case multichoice:
                            question = new MultichoiceQuestion(questionJson.toString());
                            break;
                        case multiselect:
                            question = new MultiselectQuestion(questionJson.toString());
                            break;
                        default:
                            break;
                    }
                    if (question != null) {
                        questions.add(question);
                    }
                }
                return questions;
            }
        } catch (JSONException e) {
        }
        return null;
    }
}
