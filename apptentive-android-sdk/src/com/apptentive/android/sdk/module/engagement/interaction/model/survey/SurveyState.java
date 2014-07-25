/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model.survey;


import com.apptentive.android.sdk.module.engagement.interaction.model.SurveyInteraction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Sky Kelsey
 */
public class SurveyState {

    private boolean surveyLaunchSent;

    private Map<String, Set<String>> questionToAnswersMap;
    private Map<String, Integer> questionToMinAnswersMap;
    private Map<String, Integer> questionToMaxAnswersMap;
    private Map<String, Boolean> questionToMetricSentMap;

    public SurveyState(SurveyInteraction surveyInteraction) {
        questionToAnswersMap = new HashMap<String, Set<String>>();
        questionToMinAnswersMap = new HashMap<String, Integer>();
        questionToMaxAnswersMap = new HashMap<String, Integer>();
        questionToMetricSentMap = new HashMap<String, Boolean>();

        for (Question question : surveyInteraction.getQuestions()) {
            String questionId = question.getId();
            questionToAnswersMap.put(questionId, new HashSet<String>());
            questionToMinAnswersMap.put(questionId, question.getMinSelections());
            questionToMaxAnswersMap.put(questionId, question.getMaxSelections());
            questionToMetricSentMap.put(questionId, false);
        }
    }

    public void setSurveyLaunchSent() {
        surveyLaunchSent = true;
    }

    public boolean isSurveyLaunchSent() {
        return surveyLaunchSent;
    }

    public void addAnswer(String questionId, String answer) {
        Set<String> answers = questionToAnswersMap.get(questionId);
        if (answers == null) {
            answers = new HashSet<String>();
            questionToAnswersMap.put(questionId, answers);
        }
        answers.add(answer);
    }

    public Set<String> getAnswers(String questionId) {
        return questionToAnswersMap.get(questionId);
    }

    public void setAnswers(String questionId, Set<String> answers) {
        questionToAnswersMap.put(questionId, answers);
    }

    public void clearAnswers(String questionId) {
        questionToAnswersMap.put(questionId, new HashSet<String>());
    }

    public boolean isQuestionValid(Question question) {
        String questionId = question.getId();
        int min = questionToMinAnswersMap.get(questionId);
        int max = questionToMaxAnswersMap.get(questionId);
        int size = questionToAnswersMap.get(questionId).size();
        return (!question.isRequired() && size == 0) || (size >= min) && (size <= max);
    }

    public boolean isMetricSent(String questionId) {
        Boolean ret = questionToMetricSentMap.get(questionId);
        return ret == null ? false : ret;
    }

    public void markMetricSent(String questionId) {
        questionToMetricSentMap.put(questionId, true);
    }
}
