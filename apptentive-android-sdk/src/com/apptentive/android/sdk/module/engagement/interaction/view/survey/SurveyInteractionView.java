/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view.survey;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptentive.android.sdk.ApptentiveInternal;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.SurveyResponse;
import com.apptentive.android.sdk.module.engagement.EngagementModule;
import com.apptentive.android.sdk.module.engagement.interaction.model.SurveyInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.MultichoiceQuestion;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.MultiselectQuestion;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.Question;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.SinglelineQuestion;
import com.apptentive.android.sdk.module.engagement.interaction.model.survey.SurveyState;
import com.apptentive.android.sdk.module.engagement.interaction.view.InteractionView;
import com.apptentive.android.sdk.module.survey.OnSurveyFinishedListener;
import com.apptentive.android.sdk.module.survey.OnSurveyQuestionAnsweredListener;
import com.apptentive.android.sdk.storage.ApptentiveDatabase;
import com.apptentive.android.sdk.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sky Kelsey
 */
public class SurveyInteractionView extends InteractionView<SurveyInteraction> {

    private static final String EVENT_LAUNCH = "launch";
    private static final String EVENT_CANCEL = "cancel";
    private static final String EVENT_SUBMIT = "submit";
    private static final String EVENT_QUESTION_RESPONSE = "question_response";

    private static SurveyState surveyState;
    private static Map<String, String> data;

    //private OnSurveyFinishedListener onSurveyFinishedListener;

    public SurveyInteractionView(SurveyInteraction interaction) {
        super(interaction);
        if (surveyState == null) {
            surveyState = new SurveyState(interaction);
        }
        if (data == null) {
            data = new HashMap<String, String>();
            data.put("id", interaction.getId());
        }

    }

    @Override
    public void show(final Activity activity) {
        super.show(activity);

        if (interaction == null) {
            activity.finish();
            return;
        }

        if (!surveyState.isSurveyLaunchSent()) {
            EngagementModule.engageInternal(activity, interaction.getType().name(), EVENT_LAUNCH, data);
            surveyState.setSurveyLaunchSent();
        }

        activity.setContentView(R.layout.apptentive_survey);

        TextView title = (TextView) activity.findViewById(R.id.title);
        title.setFocusable(true);
        title.setFocusableInTouchMode(true);
        title.setText(interaction.getName());

        String descriptionText = interaction.getDescription();
        if (descriptionText != null) {
            TextView description = (TextView) activity.findViewById(R.id.description);
            description.setText(descriptionText);
            description.setVisibility(View.VISIBLE);
        }

        final Button send = (Button) activity.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.hideSoftKeyboard(activity, view);

                if (interaction.isShowSuccessMessage() && interaction.getSuccessMessage() != null) {
                    SurveyThankYouDialog dialog = new SurveyThankYouDialog(activity);
                    dialog.setMessage(interaction.getSuccessMessage());
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            activity.finish();
                        }
                    });
                    dialog.show();
                } else {
                    activity.finish();
                }

                EngagementModule.engageInternal(activity, interaction.getType().name(), EVENT_SUBMIT, data);
                ApptentiveDatabase.getInstance(activity).addPayload(new SurveyResponse(interaction, surveyState));
                Log.d("Survey Submitted.");
                callListener(true);

                cleanup();
            }
        });

        LinearLayout questions = (LinearLayout) activity.findViewById(R.id.questions);
        questions.removeAllViews();

        // Then render all the questions
        for (final Question question : interaction.getQuestions()) {
            if (question.getType() == Question.QUESTION_TYPE_SINGLELINE) {
                TextSurveyQuestionView textQuestionView = new TextSurveyQuestionView(activity, surveyState, (SinglelineQuestion) question);
                textQuestionView.setOnSurveyQuestionAnsweredListener(new OnSurveyQuestionAnsweredListener() {
                    public void onAnswered() {
                        sendMetricForQuestion(activity, question);
                        send.setEnabled(isSurveyValid());
                    }
                });
                questions.addView(textQuestionView);
            } else if (question.getType() == Question.QUESTION_TYPE_MULTICHOICE) {
                MultichoiceSurveyQuestionView multichoiceQuestionView = new MultichoiceSurveyQuestionView(activity, surveyState, (MultichoiceQuestion) question);
                multichoiceQuestionView.setOnSurveyQuestionAnsweredListener(new OnSurveyQuestionAnsweredListener() {
                    public void onAnswered() {
                        sendMetricForQuestion(activity, question);
                        send.setEnabled(isSurveyValid());
                    }
                });
                questions.addView(multichoiceQuestionView);
            } else if (question.getType() == Question.QUESTION_TYPE_MULTISELECT) {
                MultiselectSurveyQuestionView multiselectQuestionView = new MultiselectSurveyQuestionView(activity, surveyState, (MultiselectQuestion) question);
                multiselectQuestionView.setOnSurveyQuestionAnsweredListener(new OnSurveyQuestionAnsweredListener() {
                    public void onAnswered() {
                        sendMetricForQuestion(activity, question);
                        send.setEnabled(isSurveyValid());
                    }
                });
                questions.addView(multiselectQuestionView);
            }
        }

        send.setEnabled(isSurveyValid());

        // Force the top of the survey to be shown first.
        title.requestFocus();
    }

    public boolean isSurveyValid() {
        for (Question question : interaction.getQuestions()) {
            if (!surveyState.isQuestionValid(question)) {
                return false;
            }
        }
        return true;
    }

    void sendMetricForQuestion(Activity activity, Question question) {
        String questionId = question.getId();
        if (!surveyState.isMetricSent(questionId) && surveyState.isQuestionValid(question)) {
            Map<String, String> answerData = new HashMap<String, String>();
            answerData.put("id", question.getId());
            answerData.put("survey_id", interaction.getId());
            EngagementModule.engageInternal(activity, interaction.getType().name(), EVENT_QUESTION_RESPONSE, answerData);
            surveyState.markMetricSent(questionId);
        }
    }

    private void cleanup() {
        surveyState = null;
        //this.onSurveyFinishedListener = null;
        data = null;
    }


    @Override
    public void onStop() {

    }

    @Override
    public void onBackPressed(Activity activity) {
        EngagementModule.engageInternal(activity, interaction.getType().name(), EVENT_CANCEL, data);
        callListener(false);

        cleanup();
    }

    private void callListener(boolean completed) {
        OnSurveyFinishedListener listener = ApptentiveInternal.getOnSurveyFinishedListener();
        if (listener != null) {
            listener.onSurveyFinished(completed);
        }
    }
}
