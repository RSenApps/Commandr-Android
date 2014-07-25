/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.apptentive.android.sdk.module.ActivityContent;
import com.apptentive.android.sdk.module.engagement.interaction.model.AppStoreRatingInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.model.EnjoymentDialogInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.model.FeedbackDialogInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.model.Interaction;
import com.apptentive.android.sdk.module.engagement.interaction.model.RatingDialogInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.model.SurveyInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.model.UpgradeMessageInteraction;
import com.apptentive.android.sdk.module.engagement.interaction.view.AppStoreRatingInteractionView;
import com.apptentive.android.sdk.module.engagement.interaction.view.EnjoymentDialogInteractionView;
import com.apptentive.android.sdk.module.engagement.interaction.view.FeedbackDialogInteractionView;
import com.apptentive.android.sdk.module.engagement.interaction.view.InteractionView;
import com.apptentive.android.sdk.module.engagement.interaction.view.RatingDialogInteractionView;
import com.apptentive.android.sdk.module.engagement.interaction.view.UpgradeMessageInteractionView;
import com.apptentive.android.sdk.module.engagement.interaction.view.survey.SurveyInteractionView;
import com.apptentive.android.sdk.module.messagecenter.ApptentiveMessageCenter;
import com.apptentive.android.sdk.module.messagecenter.view.MessageCenterView;
import com.apptentive.android.sdk.module.metric.MetricModule;
import com.apptentive.android.sdk.util.Constants;

/**
 * For internal use only. Used to launch Apptentive Message Center, Survey, and About views.
 *
 * @author Sky Kelsey
 */
public class ViewActivity extends ApptentiveActivity {

    private ActivityContent activityContent;
    private ActivityContent.Type activeContentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            activeContentType = ActivityContent.Type.parse(getIntent().getStringExtra(ActivityContent.KEY));

            Window window = getWindow();
            window.setFormat(PixelFormat.RGBA_8888);
            window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        } catch (Exception e) {
            Log.e("Error creating ViewActivity.", e);
            MetricModule.sendError(this, e, null, null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            switch (activeContentType) {
                case ABOUT:
                    AboutModule.getInstance().doShow(this);
                    break;
                case MESSAGE_CENTER:
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
                    ApptentiveMessageCenter.doShow(this);
                    break;
                case INTERACTION:
                    String interactionString = getIntent().getExtras().getCharSequence(Interaction.KEY_NAME).toString();
                    Interaction interaction = Interaction.Factory.parseInteraction(interactionString);
                    InteractionView view = null;
                    switch (interaction.getType()) {
                        case UpgradeMessage:
                            view = new UpgradeMessageInteractionView((UpgradeMessageInteraction) interaction);
                            break;
                        case EnjoymentDialog:
                            view = new EnjoymentDialogInteractionView((EnjoymentDialogInteraction) interaction);
                            break;
                        case RatingDialog:
                            view = new RatingDialogInteractionView((RatingDialogInteraction) interaction);
                            break;
                        case AppStoreRating:
                            view = new AppStoreRatingInteractionView((AppStoreRatingInteraction) interaction);
                            break;
                        case FeedbackDialog:
                            view = new FeedbackDialogInteractionView((FeedbackDialogInteraction) interaction);
                            break;
                        case Survey:
                            view = new SurveyInteractionView((SurveyInteraction) interaction);
                            break;
                        case MessageCenter:
                            // For now, we use the old method.
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            finish();
                            Apptentive.showMessageCenter(this);
                            return;
                        default:
                            break;
                    }
                    activityContent = view;
                    if (view == null) {
                        finish();
                    } else {
                        view.show(this);
                    }
                    break;
                default:
                    Log.w("No Activity specified. Finishing...");
                    finish();
                    break;
            }
        } catch (Exception e) {
            Log.e("Error starting ViewActivity.", e);
            MetricModule.sendError(this, e, null, null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        switch (activeContentType) {
            case ABOUT:
                break;
            case MESSAGE_CENTER:
                ApptentiveMessageCenter.onStop(this);
                break;
            case INTERACTION:
                if (activityContent != null) {
                    activityContent.onStop();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (activeContentType) {
            case ABOUT:
                AboutModule.getInstance().onBackPressed(this);
                break;
            case MESSAGE_CENTER:
                ApptentiveMessageCenter.onBackPressed(this);
                break;
            case INTERACTION:
                if (activityContent != null) {
                    activityContent.onBackPressed(this);
                }
                break;
            default:
                break;
        }
        finish();
        overridePendingTransition(0, R.anim.slide_down_out);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_PHOTO_FROM_MESSAGE_CENTER:
                    MessageCenterView.showAttachmentDialog(this, data.getData());
                    break;
                default:
                    break;
            }
        }
    }

    public void showAboutActivity(View view) {
        AboutModule.getInstance().show(this);
    }
}
