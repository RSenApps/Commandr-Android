/*
 * Copyright (c) 2011, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */


package com.apptentive.android.sdk.module.rating;

/**
 * Indicates that a implementation of {@link IRatingProvider} was not
 * provided necessary and/or sufficient arguments to successfully kick
 * off a rating workflow.
 */
public class InsufficientRatingArgumentsException extends Exception {
    private static final long serialVersionUID = -4592353045389664388L;

    public InsufficientRatingArgumentsException(String message) {
        super(message);
    }
}