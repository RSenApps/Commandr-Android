/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.survey;

/**
 * This interface is provided so you can get a callback when a survey has been finished.
 *
 * @author Sky Kelsey
 */
public interface OnSurveyFinishedListener {

    /**
     * Called when a survey has been finished.
     *
     * @param completed true if the survey was fully completed. Otherwise false.
     */
    public void onSurveyFinished(boolean completed);
}
