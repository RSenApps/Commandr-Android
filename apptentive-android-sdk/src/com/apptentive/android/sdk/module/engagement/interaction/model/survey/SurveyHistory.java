/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model.survey;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Sky Kelsey
 */
public class SurveyHistory {

    private static final String KEY_SURVEYS = "surveys";
    private static final String KEY_SURVEY_ID = "id";
    private static final String KEY_SURVEY_DISPLAYS = "displays";

    private SurveyHistory() {
    }

    public static void recordSurveyDisplay(Context context, String surveyId, long currentTimeMillis) {
        String json = load(context);
        try {
            JSONObject root;
            if (json == null || json.length() == 0) {
                root = new JSONObject();
            } else {
                root = new JSONObject(json);
            }
            JSONArray surveys = root.optJSONArray(KEY_SURVEYS);
            if (surveys == null) {
                surveys = new JSONArray();
                root.put(KEY_SURVEYS, surveys);
            }
            JSONObject survey = null;
            for (int i = 0; i < surveys.length(); i++) {
                JSONObject temp = surveys.getJSONObject(i);
                String id = temp.optString(KEY_SURVEY_ID);
                if (id != null && id.equals(surveyId)) {
                    survey = temp;
                    break;
                }
            }
            if (survey == null) {
                survey = new JSONObject();
                survey.put(KEY_SURVEY_ID, surveyId);
                surveys.put(survey);
            }
            JSONArray displays = survey.optJSONArray(KEY_SURVEY_DISPLAYS);
            if (displays == null) {
                displays = new JSONArray();
                survey.put(KEY_SURVEY_DISPLAYS, displays);
            }
            displays.put(currentTimeMillis);
            String jsonResult = root.toString();
            Log.v("Logging survey display for %s at %d", surveyId, currentTimeMillis);
            store(context, jsonResult);
        } catch (JSONException e) {
            Log.e("Unable to record Survey Display.", e);
        }
    }

    private static int getSurveyDisplaysWithinWindow(Context context, String surveyId, Double viewPeriod) {
        int count = 0;
        String json = load(context);
        long startTime = System.currentTimeMillis() - (long) (viewPeriod * 1000);
        try {

            JSONObject root;
            if (json == null || json.length() == 0) {
                root = new JSONObject();
            } else {
                root = new JSONObject(json);
            }
            JSONArray surveys = root.optJSONArray(KEY_SURVEYS);
            if (surveys != null) {
                JSONObject survey = null;
                for (int i = 0; i < surveys.length(); i++) {
                    JSONObject temp = surveys.getJSONObject(i);
                    String id = temp.optString(KEY_SURVEY_ID);
                    if (id != null && id.equals(surveyId)) {
                        survey = temp;
                        break;
                    }
                }
                if (survey != null) {
                    JSONArray displays = survey.optJSONArray(KEY_SURVEY_DISPLAYS);
                    if (displays != null) {
                        for (int j = 0; j < displays.length(); j++) {
                            if (!displays.isNull(j)) {
                                long display = displays.getLong(j);
                                if (display > startTime) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Unable to retrieve Survey Displays within window.", e);
        }
        Log.v("Survey %s has been displayed %d times in the current view period.", surveyId, count);
        return count;
    }

    private static int getTotalSurveyDisplays(Context context, String surveyId) {
        int count = 0;
        String json = load(context);
        try {
            JSONObject root;
            if (json == null || json.length() == 0) {
                root = new JSONObject();
            } else {
                root = new JSONObject(json);
            }
            JSONArray surveys = root.optJSONArray(KEY_SURVEYS);
            if (surveys != null) {
                JSONObject survey = null;
                for (int i = 0; i < surveys.length(); i++) {
                    JSONObject temp = surveys.getJSONObject(i);
                    String id = temp.optString(KEY_SURVEY_ID);
                    if (id != null && id.equals(surveyId)) {
                        survey = temp;
                        break;
                    }
                }
                if (survey != null) {
                    JSONArray displays = survey.optJSONArray(KEY_SURVEY_DISPLAYS);
                    if (displays != null) {
                        for (int j = 0; j < displays.length(); j++) {
                            if (!displays.isNull(j)) {
                                count++;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Unable to retrieve total Survey Displays.", e);
        }
        Log.v("Survey %s has been displayed %d times in the current view period.", surveyId, count);
        return count;
    }

    public static boolean isSurveyLimitMet(Context context, SurveyDefinition survey) {
        // Can survey be shown multiple times?
        if (!survey.isMultipleResponses()) {
            int actualViewCountTotal = SurveyHistory.getTotalSurveyDisplays(context, survey.getId());
            if (actualViewCountTotal > 0) {
                return true;
            }
        }

        // Has the survey exceeded rate limiting?
        if (survey.getViewCount() != null && survey.getViewPeriod() != null) {
            int actualViewCountWithinWindow = SurveyHistory.getSurveyDisplaysWithinWindow(context, survey.getId(), survey.getViewPeriod());
            if (actualViewCountWithinWindow >= survey.getViewCount()) {
                return true;
            }
        }
        return false;
    }

    private static String load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREF_KEY_SURVEYS_HISTORY, null);
    }

    private static void store(Context context, String json) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_SURVEYS_HISTORY, json).commit();
    }
}
