/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.module.engagement.interaction.model.SurveyInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.Question;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.SurveyState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Sky Kelsey
 */
public class SurveyResponse extends ConversationItem {

    private static final String KEY_SURVEY_ID = "id";

    private static final String KEY_SURVEY_ANSWERS = "answers";

    public SurveyResponse(String json) throws JSONException {
        super(json);
    }

    public SurveyResponse(SurveyInteraction definition, SurveyState surveyState) {
        super();

        try {
            put(KEY_SURVEY_ID, definition.getId());

            JSONObject answers = new JSONObject();
            put(KEY_SURVEY_ANSWERS, answers);

            List<Question> questions = definition.getQuestions();
            for (Question question : questions) {
                String questionId = question.getId();
                Set<String> answersList = surveyState.getAnswers(questionId);
                if (answersList.size() > 1 || question.getType() == Question.QUESTION_TYPE_MULTISELECT) {
                    JSONArray jsonArray = new JSONArray(answersList);
                    answers.put(questionId, jsonArray);
                } else if (answersList.size() == 1) {
                    answers.put(questionId, new ArrayList<String>(answersList).get(0));
                }
            }
        } catch (JSONException e) {
            Log.e("Unable to construct survey payload.", e);
        }
    }

    public String getId() {
        return optString(KEY_SURVEY_ID, "");
    }

    @Override
    protected void initBaseType() {
        setBaseType(BaseType.survey);
    }

    @Override
    public String marshallForSending() {
        // We need to store "id", but it should be used in the POST URL, not in the body.
        //remove(KEY_SURVEY_ID);
        return super.marshallForSending();
    }
}
