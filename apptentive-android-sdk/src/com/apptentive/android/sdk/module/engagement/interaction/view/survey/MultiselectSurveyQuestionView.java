/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view.survey;

import android.content.Context;

import com.apptentive.android.sdk.module.engagement.interaction.model.survey.MultiselectQuestion;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.SurveyState;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Sky Kelsey.
 */
public class MultiselectSurveyQuestionView extends MultichoiceSurveyQuestionView {

    protected int minSelections;
    protected int maxSelections;

    public MultiselectSurveyQuestionView(Context context, SurveyState surveyState, MultiselectQuestion question) {
        super(context, surveyState, question);
        this.minSelections = question.getMinSelections();
        this.maxSelections = question.getMaxSelections();
        updateValidationState();
    }

    /**
     * Override to change the behavior of clicking this.
     */
    protected void choiceClicked(CheckboxChoice choice) {
        choice.toggle();
        Set<String> checkedChoices = new HashSet<String>();
        for (String id : answersChoices.keySet()) {
            if (answersChoices.get(id).isChecked()) {
                checkedChoices.add(id);
            }
        }
        surveyState.setAnswers(question.getId(), checkedChoices);
        updateValidationState();
        requestFocus();
        fireListener();
    }
}
