/*
 * Copyright (c) 2011, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.rating;

import android.content.Context;

import java.util.Map;

/**
 * Provides a common interface for ratings providers to
 * allow Apptentive to work with the multiple markets available
 * on the Android platform.
 */
public interface IRatingProvider {
    /**
     * Starts the rating process. Implementations should gracefully
     * handle cases where not all required arguments are provided.
     *
     * @param args    A list of keys and values which may have been
     *                provided at app initialization to allow the ratings provider
     *                access to additional information. The hash will, at a minimum,
     *                contain the 'name' of the app as passed to apptentive and the
     *                'package' identifier of the app.
     * @param context An Android {@link Context} used to launch the rating
     *                or provide dialogs or notifications.
     * @throws InsufficientRatingArgumentsException Thrown when the implementation needs an argument that isn't provided.
     */
    public void startRating(Context context, Map<String, String> args) throws InsufficientRatingArgumentsException;

    /**
     * Called if the startRating process does not successfully finish launching an activityContext.
     *
     * @param context The current Activity Context
     * @return The error message to display to users.
     */
    public String activityNotFoundMessage(Context context);
}