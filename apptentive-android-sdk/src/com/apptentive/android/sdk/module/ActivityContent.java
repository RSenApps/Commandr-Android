/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module;

import android.app.Activity;

import com.apptentive.android.sdk.Log;

/**
 * @author Sky Kelsey
 */
public abstract class ActivityContent {

    public static final String KEY = "activityContent";

    protected Type type;

    public abstract void onStop();

    public abstract void onBackPressed(Activity activity);

    public abstract void show(Activity activity);

    public Type getType() {
        return type;
    }

    public enum Type {
        ABOUT,
        MESSAGE_CENTER,
        INTERACTION,
        unknown;

        public static Type parse(String type) {
            try {
                return Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                Log.v("Error parsing unknown ActivityContent.Type: " + type);
            }
            return unknown;
        }
    }
}
