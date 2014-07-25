/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.AppRelease;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.JsonDiffer;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public class AppReleaseManager {

    public static AppRelease storeAppReleaseAndReturnDiff(Context context) {
        AppRelease stored = getStoredAppRelease(context);
        AppRelease current = generateCurrentAppRelease(context);

        Object diff = JsonDiffer.getDiff(stored, current);
        if (diff != null) {
            try {
                storeAppRelease(context, current);
                return new AppRelease(diff.toString());
            } catch (JSONException e) {
                Log.e("Error casting to AppRelease.", e);
            }
        }
        return null;
    }

    private static AppRelease generateCurrentAppRelease(Context context) {
        AppRelease appRelease = new AppRelease();

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appRelease.setVersion("" + packageInfo.versionName);
            appRelease.setIdentifier(packageInfo.packageName);
            appRelease.setBuildNumber("" + packageInfo.versionCode);
            appRelease.setTargetSdkVersion("" + packageInfo.applicationInfo.targetSdkVersion);
            appRelease.setAppStore(Util.getInstallerPackageName(context));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Can't load PackageInfo.", e);
        }
        return appRelease;
    }

    public static AppRelease getStoredAppRelease(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String appReleaseString = prefs.getString(Constants.PREF_KEY_APP_RELEASE, null);
        try {
            return new AppRelease(appReleaseString);
        } catch (Exception e) {
        }
        return null;
    }

    private static void storeAppRelease(Context context, AppRelease appRelease) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_APP_RELEASE, appRelease.toString()).commit();
    }
}
