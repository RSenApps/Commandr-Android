/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view.survey;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.Question;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.SurveyState;
import com.apptentive.android.sdk.module.survey.OnSurveyQuestionAnsweredListener;
import com.apptentive.android.sdk.util.Util;

/**
 * @author Sky Kelsey.
 */
abstract public class BaseSurveyQuestionView<Q extends Question> extends FrameLayout {

    protected Q question;
    protected SurveyState surveyState;

    protected OnSurveyQuestionAnsweredListener listener;

    protected BaseSurveyQuestionView(Context context, SurveyState surveyState, Q question) {
        super(context);
        this.question = question;
        this.surveyState = surveyState;

        // Required to remove focus from any EditTexts.
        setFocusable(true);
        setFocusableInTouchMode(true);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        inflater.inflate(R.layout.apptentive_survey_question_base, this);

        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getContext() instanceof Activity) {
                    Util.hideSoftKeyboard((Activity) getContext(), BaseSurveyQuestionView.this);
                }
                return false;
            }
        });

        TextView title = (TextView) findViewById(R.id.question_title);
        title.setText(question.getValue());

        String instructionsText = question.getInstructions();
        setInstructions(instructionsText);


        updateValidationState();
    }

    protected void setInstructions(String instructionsText) {
        TextView instructions = (TextView) findViewById(R.id.question_instructions);
        FrameLayout topSeparator = (FrameLayout) findViewById(R.id.question_top_separater);
        if (instructionsText != null && instructionsText.length() > 0) {
            instructions.setText(instructionsText);
            topSeparator.setVisibility(View.GONE);
            instructions.setVisibility(View.VISIBLE);
        } else {
            topSeparator.setVisibility(View.VISIBLE);
            instructions.setVisibility(View.GONE);
        }

    }

    protected LinearLayout getAnswerContainer() {
        return (LinearLayout) findViewById(R.id.answer_container);
    }

    public void setOnSurveyQuestionAnsweredListener(OnSurveyQuestionAnsweredListener listener) {
        this.listener = listener;
    }

    protected void fireListener() {
        if (listener != null) {
            listener.onAnswered();
        }
    }

    protected void updateValidationState() {
        Resources resources = getContext().getResources();
        TextView instructions = (TextView) findViewById(R.id.question_instructions);
        View validationFrame = findViewById(R.id.question_background_validation);
        if (question != null && !surveyState.isQuestionValid(question)) {
            instructions.setTextColor(resources.getColor(R.color.apptentive_survey_question_instruction_text_invalid));
            instructions.setBackgroundColor(resources.getColor(R.color.apptentive_survey_question_instruction_background_invalid));
            instructions.setTypeface(Typeface.DEFAULT_BOLD);
            validationFrame.setBackgroundDrawable(resources.getDrawable(R.drawable.apptentive_survey_question_background_invalid));
        } else {
            instructions.setTextColor(resources.getColor(R.color.apptentive_survey_question_instruction_text_valid));
            instructions.setBackgroundColor(resources.getColor(R.color.apptentive_survey_question_instruction_background_valid));
            instructions.setTypeface(Typeface.DEFAULT);
            validationFrame.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
