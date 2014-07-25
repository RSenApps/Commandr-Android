/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptentive.android.sdk.SessionEvent;
import com.apptentive.android.sdk.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A persistent queue of Activity starts and stops backed with SharedPreferences. I used SharedPreferences instead of
 * a SQLite database because these events will be generated often in the app, and a SQLite database save or retrieve
 * operation takes a significant fraction of a second to complete. SharedPreferences are less elegant, but they can be
 * modified and read quickly.
 *
 * @author Sky Kelsey
 */
public class SharedPreferencesPersistentSessionQueue implements PersistentSessionQueue {

    private static final String EVENT_SEP = ";";
    private static final String FIELD_SEP = ":";

    private Context appContext;

    public SharedPreferencesPersistentSessionQueue(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void addEvents(SessionEvent... events) {
        SharedPreferences prefs = getPrefs();
        StringBuilder builder = new StringBuilder(prefs.getString(Constants.PREF_KEY_APP_ACTIVITY_STATE_QUEUE, ""));
        for (SessionEvent event : events) {
            builder.append(generateStorableEventString(event)).append(EVENT_SEP);
        }
        prefs.edit().putString(Constants.PREF_KEY_APP_ACTIVITY_STATE_QUEUE, builder.toString()).commit();
    }

    public void deleteEvents(SessionEvent... events) {
        List<SessionEvent> storedEvents = getAllEvents();
        for (SessionEvent event : events) {
            for (SessionEvent storedEvent : storedEvents) {
                if (event.equals(storedEvent)) {
                    storedEvents.remove(storedEvent);
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (SessionEvent event : storedEvents) {
            builder.append(generateStorableEventString(event)).append(";");
        }
        getPrefs().edit().putString(Constants.PREF_KEY_APP_ACTIVITY_STATE_QUEUE, builder.toString()).commit();
    }

    public void deleteAllEvents() {
        getPrefs().edit().remove(Constants.PREF_KEY_APP_ACTIVITY_STATE_QUEUE).commit();
    }

    public List<SessionEvent> getAllEvents() {
        String[] queue = getPrefs().getString(Constants.PREF_KEY_APP_ACTIVITY_STATE_QUEUE, "").split(";");
        List<SessionEvent> events = new ArrayList<SessionEvent>(queue.length);
        for (String eventString : queue) {
            if (!eventString.equals("")) { // Needed because we always Append a semi-colon to the queue.
                events.add(parseStorableEventString(eventString));
            }
        }
        return events;
    }

    private String generateStorableEventString(SessionEvent event) {
        return String.format("%s%s%s%s%s", event.getTime(), FIELD_SEP, event.getAction().name(), FIELD_SEP, event.getActivityName());
    }

    private SessionEvent parseStorableEventString(String eventString) {
        String[] parts = eventString.split(FIELD_SEP);
        if (parts.length != 3) {
            throw new RuntimeException("Corrupt SessionEvent in Queue: " + eventString);
        }
        return new SessionEvent(Long.parseLong(parts[0]), SessionEvent.Action.valueOf(parts[1]), parts[2]);
    }

    private SharedPreferences getPrefs() {
        return appContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
