/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */
package com.apptentive.android.sdk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.apptentive.android.sdk.GlobalInfo;
import com.apptentive.android.sdk.Log;

import java.util.List;

/**
 * @author Sky Kelsey
 */
public class ActivityUtil {

    /**
     * <strong>NOTE: This method is for debugging purposes only. Google may reject your app if you ship it with the
     * GET_TASKS permission enabled.</strong>
     * <p/>
     * This method will return true if the Application is going into the background because an Activity that is not defined
     * in the current Application's manifest is going into the foreground. It will return false if another Activity defined
     * in the current Application's manifest is going into the foreground, or if the Application is exiting.
     * <p/> Call this from Activity.onPause(), and it will tell you if the Application is being backgrounded so another
     * Application can run.
     * <p/>Requires permission: android.permission.GET_TASKS
     *
     * @param activity The activity you are calling from.
     * @return true: if another Application is coming to the foreground.<p/>
     * false: if the Activity is merely giving way to another Activity defined in the same Application.
     * @deprecated Marking this deprecated simply so you read this whole javadoc before trying to use this method.
     */
    public static boolean isApplicationBroughtToBackground(final Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks;
        try {
            tasks = activityManager.getRunningTasks(1);
        } catch (SecurityException e) {
            if (GlobalInfo.isAppDebuggable) {
                throw e;
            } else {
                Log.e("Missing required permission: \"android.permission.GET_TASKS\".", e);
            }
            return false;
        }
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            try {
                PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES);
                for (ActivityInfo activityInfo : pi.activities) {
                    if (topActivity.getClassName().equals(activityInfo.name)) {
                        return false;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Package name not found: %s", e, activity.getPackageName());
                return false; // Never happens.
            }
        }
        return true;
    }

    /**
     * Tells you whether the currentActivity is the main Activity of the app. The side effect is that this will register
     * the first Activity is is called form as the main Activity, so make sure it's called from the main Activity before
     * any others.
     *
     * @param currentActivity The Activity from which this method is called.
     * @return true iff currentActivity is the Application's main Activity.
     */
    public static boolean isCurrentActivityMainActivity(Activity currentActivity) {

        SharedPreferences prefs = currentActivity.getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        String currentActivityName = currentActivity.getComponentName().getClassName();
        String mainActivityName = prefs.getString(Constants.PREF_KEY_APP_MAIN_ACTIVITY_NAME, null);

        // The first time this runs, it will be from the main Activity, guaranteed.
        if (mainActivityName == null) {
            mainActivityName = currentActivityName;
            prefs.edit().putString(Constants.PREF_KEY_APP_MAIN_ACTIVITY_NAME, mainActivityName).commit();
        }

        return currentActivityName != null && currentActivityName.equals(mainActivityName);
    }
}