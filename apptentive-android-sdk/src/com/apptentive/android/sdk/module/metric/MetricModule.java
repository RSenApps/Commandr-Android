/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.metric;

import android.content.Context;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.Configuration;
import com.apptentive.android.sdk.model.Event;
import com.apptentive.android.sdk.model.EventManager;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Sky Kelsey.
 */
public class MetricModule {

    private static final String KEY_EXCEPTION = "exception";

    public static void sendMetric(Context context, Event.EventLabel type) {
        sendMetric(context, type, null);
    }

    public static void sendMetric(Context context, Event.EventLabel type, String trigger) {
        sendMetric(context, type, trigger, null);
    }

    public static void sendMetric(Context context, Event.EventLabel type, String trigger, Map<String, String> data) {
        Configuration config = Configuration.load(context);
        if (config.isMetricsEnabled()) {
            Log.v("Sending Metric: %s, trigger: %s, data: %s", type.getLabelName(), trigger, data != null ? data.toString() : "null");
            Event event = new Event(type.getLabelName(), trigger);
            event.putData(data);
            EventManager.sendEvent(context, event);
        }
    }

    /**
     * Used for internal error reporting when we intercept a Throwable that may have otherwise caused a crash.
     *
     * @param context     The context from which this method was called.
     * @param throwable   An optional throwable that was caught, and which we want to log.
     * @param description An optional description of what happened.
     * @param extraData   Any extra data that may have contributed to the Throwable being thrown.
     */
    public static void sendError(Context context, Throwable throwable, String description, String extraData) {
        Event.EventLabel type = Event.EventLabel.error;
        try {
            JSONObject data = new JSONObject();
            data.put("thread", Thread.currentThread().getName());
            if (throwable != null) {
                JSONObject exception = new JSONObject();
                exception.put("message", throwable.getMessage());
                exception.put("stackTrace", Util.stackTraceAsString(throwable));
                data.put(KEY_EXCEPTION, exception);
            }
            if (description != null) {
                data.put("description", description);
            }
            if (extraData != null) {
                data.put("extraData", extraData);
            }
            Configuration config = Configuration.load(context);
            if (config.isMetricsEnabled()) {
                Log.v("Sending Error Metric: %s, data: %s", type.getLabelName(), data.toString());
                Event event = new Event(type.getLabelName(), data);
                EventManager.sendEvent(context, event, false); // Don't restart the send worker because of an error.
            }
        } catch (JSONException e) {
            Log.e("Error creating Error Metric.", e);
        }
    }
}
