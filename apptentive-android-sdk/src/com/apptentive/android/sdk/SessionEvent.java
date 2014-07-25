/*
 * Copyright (c) 2012, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk;


import com.apptentive.android.sdk.util.Util;

/**
 * @author Sky Kelsey
 */
public class SessionEvent {
    private long id;
    private long time;
    private Action action;
    private String activityName;

    public SessionEvent() {
    }

    public SessionEvent(long time, Action action, String activityName) {
        this.time = time;
        this.action = action;
        this.activityName = activityName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean isStartEvent() {
        return action == Action.START;
    }

    public boolean isStopEvent() {
        return action == Action.STOP;
    }

    public String getDebugString() {
        return String.format("#%d : %s : %s : %s", id, action.name() + (isStopEvent() ? " " : ""), activityName, Util.dateToIso8601String(time));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SessionEvent)) {
            return false;
        }
        SessionEvent event = (SessionEvent) o;
        return getAction() == event.getAction() &&
                getActivityName().equals(event.getActivityName()) &&
                getTime() == event.getTime();
    }

    public enum Action {
        START,
        STOP
    }
}
