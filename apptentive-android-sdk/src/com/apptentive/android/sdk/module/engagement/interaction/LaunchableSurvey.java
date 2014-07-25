package com.apptentive.android.sdk.module.engagement.interaction;

import android.app.Activity;

import com.apptentive.android.sdk.model.CodePointStore;
import com.apptentive.android.sdk.module.engagement.EngagementModule;
import com.apptentive.android.sdk.module.engagement.interaction.model.SurveyInteraction;

/**
 * Created by Ryan on 6/20/2014.
 */
public class LaunchableSurvey {
    public SurveyInteraction survey;
    private Activity activity;

    public LaunchableSurvey(Activity activity, SurveyInteraction survey) {
        this.survey = survey;
        this.activity = activity;
    }

    public void launch() {
        EngagementModule.launchInteraction(activity, survey);
    }

    public void hide() {
        CodePointStore.storeInteractionForCurrentAppVersion(activity, survey.getId());
    }
}
