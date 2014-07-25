/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.lifecycle;

import android.app.Activity;
import android.content.Context;

import com.apptentive.android.sdk.ApptentiveInternal;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.SessionEvent;
import com.apptentive.android.sdk.model.Event;
import com.apptentive.android.sdk.module.engagement.EngagementModule;
import com.apptentive.android.sdk.storage.PersistentSessionQueue;
import com.apptentive.android.sdk.storage.SharedPreferencesPersistentSessionQueue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class provides two methods: activityStarted(), and activityStopped(). Both of these methods should only
 * be called internally, and need to be called during the onStart() and onStop() of each Activity that constitute the
 * Application we are integrated into.
 * <p/><p/>
 * The way this works:
 * <p/>
 * Each time an Activity is started or stopped, we generate an event and stick it into a queue. The event keeps track
 * of the Activity's object instance, whether it was started or stopped, and a timestamp of when the start or stop
 * occurred. Each time we start an Activity, we look back into this queue and remove events that no longer provide
 * information to us. For instance, we need to keep track of any events that have started, but don't have a
 * corresponding stop event, but we also need to keep the pair of events that contains the last stop event. This is
 * because when the Activity starts, we look at the last stop event, and if it has been longer than a certain amount of
 * time since that stop, we decide the new start constitutes a new app use.
 * <p/>
 * The logic is further complicated because the order that Activities call their onStart() and onStop() can not be
 * relied on. Sometimes it may be A.onStart(), A.onStop(), B.onStart(), B.onStop(), and sometimes it mey be A.onStart(),
 * B.onStart(), A.onStop(), B.onStop().
 * <p/>
 * Additionally, we have to provide a way to detect when the app has crashed, and reset the queue. But because of our
 * inability to be sure what the actual ordering of the events is, we can't tell an app has crashed until the second
 * time an Activity.onStart() is called after the crash. We need to see that there are two onStart() calls
 * that are missing their corresponding onStop() calls (one from before the crash, one from after). Caveat: If a push
 * notification for this app is opened while the app is running, a crash will be detected. This is due to the fact that
 * certain Activity start/stop behavior is ambiguous. Erring on the side of this case being a crash is necessary so that
 * when the app is operating normally without crashes and push notifications, we can trust the data.
 * <p/>
 * There are two implementations for queue storage. One is backed by SQLite, but it was too slow. The one we use is
 * backed by SharedPreferences, and is much faster.
 *
 * @author Sky Kelsey
 */
public class ActivityLifecycleManager {

    /**
     * A timeout in seconds for determining if the previous app session has stopped, and a new one has started. Timeout
     * will occur if the number of seconds between one Activity defined in the Application calling onStop(), and another
     * calling onStart() exceeds this value.
     * <p/>
     * Ten seconds was choosen because it is unlikely that it would take an Activity more than that amount of time to
     * be created and started, but it is also unlikely that we would incorrectly decide an Application session was still
     * in affect after ten seconds had passed.
     */
    private static final int SESSION_TIMEOUT_SECONDS = 10;

    private static Context appContext = null;
    private static PersistentSessionQueue queue = null;

    private static void sendEvent(Activity activity, SessionEvent event) {
        sendEvent(activity, event, false);
    }

    private static void sendEvent(Activity activity, SessionEvent event, boolean crash) {
        Log.d("Sending " + event.getDebugString());
        switch (event.getAction()) {
            case START:
                if (!crash) {
                    // Don't trigger a launch in this case, to prevent possible looping crashes.
                    ApptentiveInternal.onAppLaunch(activity);
                }
                break;
            case STOP:
                EngagementModule.engageInternal(activity, Event.EventLabel.app__exit.getLabelName());
                break;
            default:
                break;
        }
    }

    /**
     * Internal use only.
     */
    public static void activityStarted(Activity activity) {
        try {
            init(activity);
            SessionEvent start = new SessionEvent(new Date().getTime(), SessionEvent.Action.START, activity.toString());

            // Get last stop
            SessionEvent lastStop = getLastEvent(SessionEvent.Action.STOP);
            SessionEvent lastStart = getLastEvent(SessionEvent.Action.START);

            // Remove extra pairs
            removePairs(1);

            // Count Starts, pairs.
            int starts = countStarts();
            int pairs = countPairsInQueue();

            // Due to the nature of the Activity Lifecycle, onStart() of the next Activity may be called before or after
            // the onStart() of the current previous Activity. This complicated the detection. This is the distilled logic.
            if (pairs == 0 && starts == 0) {
                Log.v("First start.");
                addEvents(start);
                sendEvent(activity, start);
            } else if (pairs == 0 && starts == 1) {
                Log.v("Continuation Start. (1)");
                addEvents(start);
            } else if (pairs == 0 && starts == 2) {
                Log.i("Starting new session after crash. (1)");
                removeAllEvents();
                sendEvent(activity, lastStart != null ? lastStart : start, true);
                addEvents(lastStart, start);
            } else if (pairs == 1 && starts == 1) {
                long expiration = lastStop.getTime() + (SESSION_TIMEOUT_SECONDS * 1000);
                boolean expired = expiration < start.getTime();
                addEvents(start);
                if (expired) {
                    Log.d("Session expired. Starting new session.");
                    sendEvent(activity, lastStop);
                    sendEvent(activity, start);
                } else {
                    Log.v("Continuation Start. (2)");
                }
            } else if (pairs == 1 && starts == 2) {
                Log.v("Continuation start. (3)");
                addEvents(start);
            } else if (pairs == 1 && starts == 3) {
                Log.i("Starting new session after crash. (2)");
                sendEvent(activity, lastStart != null ? lastStart : start, true);
                // Reconstruct Queue.
                removeAllEvents();
                addEvents(lastStart, start);
            } else {
                Log.w("ERROR: Unexpected state in LifecycleManager: " + getQueueAsString());
            }
        } catch (Exception e) {
            Log.e("Error while handling activity start.", e);
        }
    }

    /**
     * Internal use only.
     */
    public static void activityStopped(Activity activity) {
        try {
            init(activity);
            addEvents(new SessionEvent(new Date().getTime(), SessionEvent.Action.STOP, activity.toString()));
        } catch (Exception e) {
            Log.e("Error while handling activity stop.", e);
        }
    }

    private static void init(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
        if (queue == null) {
            queue = new SharedPreferencesPersistentSessionQueue(appContext);
        }
    }

    private static String getQueueAsString() {
        StringBuilder builder = new StringBuilder("Queue: ");

        List<SessionEvent> events = queue.getAllEvents();
        for (SessionEvent event : events) {
            builder.append("\n  ").append(event.getDebugString());
        }
        return builder.toString();
    }

    private static void addEvents(SessionEvent... events) {
        queue.addEvents(events);
    }

    private static int countStarts() {
        int starts = 0;
        List<SessionEvent> events = getAllEvents();
        for (SessionEvent event : events) {
            if (event.isStartEvent()) {
                starts++;
            }
        }
        return starts;
    }

    private static void removeEvent(SessionEvent... events) {
        queue.deleteEvents(events);
    }

    private static void removeAllEvents() {
        queue.deleteAllEvents();
    }

    private static void removePairs(int pairsToLeave) {
        List<SessionEvent> events = getAllEvents();
        List<SessionEvent> starts = new ArrayList<SessionEvent>();
        List<SessionEvent> eventsToDelete = new ArrayList<SessionEvent>();

        int pairsToDelete = Math.max(countPairsInQueue() - pairsToLeave, 0);
        if (pairsToDelete == 0) {
            return;
        }

        // Get all the start events.
        for (SessionEvent event : events) {
            if (event.isStartEvent()) {
                starts.add(event);
            }
        }
        // Mark pairs for deletion.
        outerLoop:
        for (SessionEvent start : starts) {
            for (SessionEvent event : events) {
                if (event.isStopEvent() && start.getActivityName().equals(event.getActivityName())) {
                    pairsToDelete--;
                    events.remove(start);
                    events.remove(event);
                    eventsToDelete.add(start);
                    eventsToDelete.add(event);
                    if (pairsToDelete == 0) {
                        break outerLoop;
                    }
                    break;
                }
            }
        }
        // Do the actual deletion.
        removeEvent(eventsToDelete.toArray(new SessionEvent[eventsToDelete.size()]));
    }

    private static int countPairsInQueue() {
        List<SessionEvent> events = getAllEvents();
        List<SessionEvent> starts = new ArrayList<SessionEvent>();
        int pairs = 0;

        // Get all the start events.
        for (SessionEvent event : events) {
            if (event.isStartEvent()) {
                starts.add(event);
            }
        }
        // Then see if they have corresponding stop events.
        for (SessionEvent start : starts) {
            for (SessionEvent event : events) {
                if (event.isStopEvent() && start.getActivityName().equals(event.getActivityName())) {
                    pairs++;
                    events.remove(start);
                    events.remove(event);
                    break;
                }
            }
        }
        return pairs;
    }

    private static List<SessionEvent> getAllEvents() {
        return queue.getAllEvents();
    }

    private static SessionEvent getLastEvent(SessionEvent.Action action) {
        List<SessionEvent> events = getAllEvents();
        SessionEvent ret = null;
        for (SessionEvent event : events) {
            if (event.getAction() == action) {
                ret = event;
            }
        }
        return ret;
    }
}
