/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.Sdk;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.JsonDiffer;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class SdkManager {

    public static Sdk storeSdkAndReturnDiff(Context context) {
        Sdk stored = getStoredSdk(context);
        Sdk current = generateCurrentSdk(context);

        Object diff = JsonDiffer.getDiff(stored, current);
        if (diff != null) {
            try {
                storeSdk(context, current);
                return new Sdk(diff.toString());
            } catch (JSONException e) {
                Log.e("Error casting to Sdk.", e);
            }
        }
        return null;
    }

    public static Sdk storeSdkAndReturnIt(Context context) {
        Sdk current = generateCurrentSdk(context);
        storeSdk(context, current);
        return current;
    }

    private static Sdk generateCurrentSdk(Context context) {
        Sdk sdk = new Sdk();

        // First, get all the information we can load from static resources.
        sdk.setVersion(Constants.APPTENTIVE_SDK_VERSION);
        sdk.setPlatform("Android");


        // Distribution and distribution version are optionally set in the manifest by the wrapping platform (trigger, etc.)
        Object distribution = Util.getPackageMetaDataSingleQuotedString(context, Constants.MANIFEST_KEY_SDK_DISTRIBUTION);
        if (distribution != null && distribution.toString().length() != 0) {
            sdk.setDistribution(distribution.toString());
        }
        Object distributionVersion = Util.getPackageMetaDataSingleQuotedString(context, Constants.MANIFEST_KEY_SDK_DISTRIBUTION_VERSION);
        if (distributionVersion != null && distributionVersion.toString().length() != 0) {
            sdk.setDistributionVersion(distributionVersion.toString());
        }

        return sdk;
    }

    public static Sdk getStoredSdk(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String sdkString = prefs.getString(Constants.PREF_KEY_SDK, null);
        try {
            return new Sdk(sdkString);
        } catch (Exception e) {
        }
        return null;
    }

    private static void storeSdk(Context context, Sdk sdk) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_SDK, sdk.toString()).commit();
    }
}
