/*
 * Copyright (c) 2012, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.storage;

import com.apptentive.android.sdk.SessionEvent;

import java.util.List;

/**
 * An interface for storing Activity start and stop events in a queue, and manipulating that queue.
 *
 * @author Sky Kelsey
 */
public interface PersistentSessionQueue {
    public void addEvents(SessionEvent... events);

    public void deleteEvents(SessionEvent... events);

    public void deleteAllEvents();

    public List<SessionEvent> getAllEvents();
}
