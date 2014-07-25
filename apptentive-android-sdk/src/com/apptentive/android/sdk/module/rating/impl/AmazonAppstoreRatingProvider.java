/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.rating.impl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.module.rating.IRatingProvider;
import com.apptentive.android.sdk.module.rating.InsufficientRatingArgumentsException;

import java.util.Map;

/**
 * @author Sky Kelsey
 */
public class AmazonAppstoreRatingProvider implements IRatingProvider {
    public void startRating(Context context, Map<String, String> args) throws InsufficientRatingArgumentsException {
        if (!args.containsKey("package")) {
            throw new InsufficientRatingArgumentsException("Missing required argument 'package'");
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + args.get("package")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String activityNotFoundMessage(Context context) {
        return context.getString(R.string.apptentive_rating_provider_no_amazon_appstore);
    }
}
