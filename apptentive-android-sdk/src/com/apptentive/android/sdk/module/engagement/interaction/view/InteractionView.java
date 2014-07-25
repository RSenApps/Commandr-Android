/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view;

import android.app.Activity;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.module.ActivityContent;
import com.apptentive.android.sdk.module.engagement.interaction.model.Interaction;

/**
 * @author Sky Kelsey
 */
public abstract class InteractionView<T extends Interaction> extends ActivityContent {

    protected T interaction;

    public InteractionView(T interaction) {
        this.interaction = interaction;
    }

    public void show(final Activity activity) {
        Log.d("Showing interaction.");
    }
}
