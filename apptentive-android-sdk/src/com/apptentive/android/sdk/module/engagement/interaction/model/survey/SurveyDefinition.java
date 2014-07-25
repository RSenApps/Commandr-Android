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
 * @author Sky Kelsey
 */
public class SurveyDefinition extends JSONObject {

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_REQUIRED = "required";
    private static final String KEY_MULTIPLE_RESPONSES = "multiple_responses";
    private static final String KEY_VIEW_COUNT = "view_count";
    private static final String KEY_VIEW_PERIOD = "view_period";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_SUCCESS_MESSAGE = "success_message";
    private static final String KEY_SHOW_SUCCESS_MESSAGE = "show_success_message";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_QUESTIONS = "questions";


    public SurveyDefinition(String json) throws JSONException {
        super(json);
    }

    public String getId() {
        try {
            if (!isNull(KEY_ID)) {
                return getString(KEY_ID);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public String getName() {
        try {
            if (!isNull(KEY_NAME)) {
                return getString(KEY_NAME);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public String getDescription() {
        try {
            if (!isNull(KEY_DESCRIPTION)) {
                return getString(KEY_DESCRIPTION);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public boolean isRequired() {
        try {
            if (!isNull(KEY_REQUIRED)) {
                return getBoolean(KEY_REQUIRED);
            }
        } catch (JSONException e) {
        }
        return false;
    }

    public boolean isMultipleResponses() {
        try {
            if (!isNull(KEY_MULTIPLE_RESPONSES)) {
                return getBoolean(KEY_MULTIPLE_RESPONSES);
            }
        } catch (JSONException e) {
        }
        return false;
    }

    public Integer getViewCount() {
        try {
            if (!isNull(KEY_VIEW_COUNT)) {
                return getInt(KEY_VIEW_COUNT);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public Double getViewPeriod() {
        try {
            if (!isNull(KEY_VIEW_PERIOD)) {
                return getDouble(KEY_VIEW_PERIOD);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public String getStartTime() {
        try {
            if (!isNull(KEY_START_TIME)) {
                return getString(KEY_START_TIME);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public String getEndTime() {
        try {
            if (!isNull(KEY_END_TIME)) {
                return getString(KEY_END_TIME);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public String getSuccessMessage() {
        try {
            if (!isNull(KEY_SUCCESS_MESSAGE)) {
                return getString(KEY_SUCCESS_MESSAGE);
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public boolean isShowSuccessMessage() {
        try {
            if (!isNull(KEY_SHOW_SUCCESS_MESSAGE)) {
                return getBoolean(KEY_SHOW_SUCCESS_MESSAGE);
            }
        } catch (JSONException e) {
        }
        return false;
    }

    public List<String> getTags() {
        List<String> ret = null;
        JSONArray tags = optJSONArray(KEY_TAGS);
        if (tags != null) {
            ret = new ArrayList<String>();
            for (int i = 0; i < tags.length(); i++) {
                String tag = tags.optString(i);
                if (tag != null) {
                    ret.add(tag);
                }
            }
        }
        return ret;
    }

    public List<Question> getQuestions() {
        try {
            List<Question> questions = new ArrayList<Question>();
            JSONArray questionsArray = getJSONArray(KEY_QUESTIONS);
            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject questionJson = (JSONObject) questionsArray.get(i);
                Type type = Type.valueOf(questionJson.getString("type"));
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
        } catch (JSONException e) {
        }
        return null;
    }

    public enum Type {
        multichoice,
        singleline,
        multiselect
    }
}