/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.logic;

import android.content.Context;

import org.json.JSONException;

/**
 * @author Sky Kelsey
 */
public abstract class Predicate {

    public static Predicate parse(String key, Object value) throws JSONException {
        if (key == null) {
            // The Root object, and objects inside arrays should be treated as $and.
            key = Operation.$and.name();
        }
        Operation op = Operation.parse(key);
        switch (op) {
            case $or:
                return new CombinationPredicate(key, value);
            case $and:
                return new CombinationPredicate(key, value);
            case $not:
                return new CombinationPredicate(key, value);
            default: // All other keys meant this is a ComparisonPredicate.
                return new ComparisonPredicate(key, value);
        }
    }

    public abstract boolean apply(Context context);

    public enum Operation {
        $and,
        $or,
        $not,
        $exists,
        $lt,
        $lte,
        $ne,
        $eq,
        $gte,
        $gt,
        $contains,
        $starts_with,
        $ends_with,
        unknown;

        public static Operation parse(String name) {
            try {
                return Operation.valueOf(name);
            } catch (IllegalArgumentException e) {
                // This will happen on old clients if we extend the logic syntax, so don't log.
            }
            return unknown;
        }
    }
}
