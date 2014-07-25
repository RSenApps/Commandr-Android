/*
 * Copyright (c) 2011, Apptentive, Inc. All Rights Reserved.
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
 * Implements ratings using the MiKandi market. At the moment this is just a
 * placeholder while changes are made to mikandi to allow a direct inward link
 * to a ratings dialog.
 */
public class MiKandiMarketRatingProvider implements IRatingProvider {

    private int mAppId;

    public MiKandiMarketRatingProvider(int AppId) {
        this.mAppId = AppId;
    }

    public void startRating(Context context, Map<String, String> args) throws InsufficientRatingArgumentsException {
        final Uri launch = Uri.parse("mikandi://link.mikandi.com/app?app_id=" + this.mAppId + "&referrer=apptentive");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(launch);
        context.startActivity(i);
    }

    public String activityNotFoundMessage(Context ctx) {
        return ctx.getString(R.string.apptentive_rating_provider_no_mikandi);
    }
}
