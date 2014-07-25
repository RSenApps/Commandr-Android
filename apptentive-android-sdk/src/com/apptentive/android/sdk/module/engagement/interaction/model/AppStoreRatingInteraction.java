/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.model;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class AppStoreRatingInteraction extends Interaction {

    // TODO: When the time comes, we should actually use the data sent from the server.

    public AppStoreRatingInteraction(String json) throws JSONException {
        super(json);
    }
}
