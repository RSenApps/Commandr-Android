/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.model;

import android.content.Context;

import com.apptentive.android.sdk.storage.ApptentiveDatabase;
import com.apptentive.android.sdk.storage.EventStore;
import com.apptentive.android.sdk.storage.PayloadSendWorker;

/**
 * @author Sky Kelsey
 */
public class EventManager {

    private static EventStore getEventStore(Context context) {
        return ApptentiveDatabase.getInstance(context);
    }

    public static void sendEvent(Context context, Event event) {
        sendEvent(context, event, true);
    }

    public static void sendEvent(Context context, Event event, boolean startPayloadSendWorker) {
        getEventStore(context).addPayload(event);
        if (startPayloadSendWorker) {
            PayloadSendWorker.ensureRunning(context);
        }
    }
}
