/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement;

import android.app.Activity;
import android.content.Intent;

import com.apptentive.android.sdk.ApptentiveInternal;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.ViewActivity;
import com.apptentive.android.sdk.model.CodePointStore;
import com.apptentive.android.sdk.model.Event;
import com.apptentive.android.sdk.model.EventManager;
import com.apptentive.android.sdk.module.ActivityContent;
import com.apptentive.android.sdk.module.engagement.interaction.InteractionManager;
import com.apptentive.android.sdk.module.engagement.interaction.LaunchableSurvey;
import com.apptentive.android.sdk.module.engagement.interaction.model.Interaction;
import com.apptentive.android.sdk.module.engagement.interaction.model.SurveyInteraction;
import com.apptentive.android.sdk.module.metric.MetricModule;
import com.apptentive.android.sdk.module.survey.OnSurveyAvailableListener;

import java.util.Map;

/**
 * @author Sky Kelsey
 */
public class EngagementModule {

    public static synchronized boolean engageInternal(Activity activity, String eventName) {
        return engage(activity, "com.apptentive", "app", eventName, null);
    }

    public static synchronized boolean engageInternal(Activity activity, String interaction, String eventName) {
        return engage(activity, "com.apptentive", interaction, eventName, null);
    }

    public static synchronized boolean engageInternal(Activity activity, String interaction, String eventName, Map<String, String> data) {
        return engage(activity, "com.apptentive", interaction, eventName, data);
    }

    public static synchronized boolean engage(Activity activity, String vendor, String interaction, String eventName) {
        return engage(activity, vendor, interaction, eventName, null);
    }

    public static synchronized boolean engage(Activity activity, String vendor, String interaction, String eventName, Map<String, String> data) {
        try {
            String eventLabel = generateEventLabel(vendor, interaction, eventName);
            Log.d("engage(%s)", eventLabel);

            CodePointStore.storeCodePointForCurrentAppVersion(activity.getApplicationContext(), eventLabel);
            EventManager.sendEvent(activity.getApplicationContext(), new Event(eventLabel, data));
            return doEngage(activity, eventLabel);
        } catch (Exception e) {
            MetricModule.sendError(activity.getApplicationContext(), e, null, null);
        }
        return false;
    }

    public static boolean doEngage(Activity activity, String eventLabel) {
        Interaction interaction = InteractionManager.getApplicableInteraction(activity.getApplicationContext(), eventLabel);
        if (interaction != null) {

            if (interaction instanceof SurveyInteraction) {
                OnSurveyAvailableListener onSurveyAvailableListener = ApptentiveInternal.getOnSurveyAvailableListener();
                if (onSurveyAvailableListener != null) {
                    onSurveyAvailableListener.onSurveyAvailable(new LaunchableSurvey(activity, (SurveyInteraction) interaction));
                    return true;
                }
            }
            launchInteraction(activity, interaction);
            return true;
        }
        Log.d("No interaction to show.");

        return false;
    }

    public static void launchInteraction(Activity activity, Interaction interaction) {
        CodePointStore.storeInteractionForCurrentAppVersion(activity, interaction.getId());
        if (interaction != null) {
            Log.e("Launching interaction: %s", interaction.getType().toString());
            Intent intent = new Intent();
            intent.setClass(activity, ViewActivity.class);
            intent.putExtra(ActivityContent.KEY, ActivityContent.Type.INTERACTION.toString());
            intent.putExtra(Interaction.KEY_NAME, interaction.toString());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_up_in, 0);
        }
    }

    public static String generateEventLabel(String vendor, String interaction, String eventName) {
        return String.format("%s#%s#%s", encodeEventLabelPart(vendor), encodeEventLabelPart(interaction), encodeEventLabelPart(eventName));
    }

    /**
     * Used only for encoding event names. DO NOT modify this method.
     */
    private static String encodeEventLabelPart(String input) {
        return input.replace("%", "%25").replace("/", "%2F").replace("#", "%23");
    }
}
