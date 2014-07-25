/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk;

import java.util.IllegalFormatException;

/**
 * @author Sky Kelsey
 */
public class Log {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    private static final String TAG = "Apptentive";

    private static void doLog(int level, Throwable throwable, String message, Object... args) {
        if (canLog(level) && message != null) {
            if (args.length > 0) {
                try {
                    message = String.format(message, args);
                } catch (IllegalFormatException e) {
                    message = "Error formatting log message [level=" + level + "]: " + message;
                    level = ERROR;
                }
            }
            android.util.Log.println(level, TAG, message);
            if (throwable != null) {
                if (throwable.getMessage() != null) {
                    android.util.Log.println(level, TAG, throwable.getMessage());
                }
                android.util.Log.println(level, TAG, android.util.Log.getStackTraceString(throwable));
            }
        }
    }

    public static boolean canLog(int level) {
        return GlobalInfo.isAppDebuggable || level > DEBUG; // Don't log below "level" unless we are debugging.
    }

    public static void v(String message, Object... args) {
        doLog(VERBOSE, null, message, args);
    }

    public static void v(String message, Throwable throwable, Object... args) {
        doLog(VERBOSE, throwable, message, args);
    }

    public static void d(String message, Object... args) {
        doLog(DEBUG, null, message, args);
    }

    public static void d(String message, Throwable throwable, Object... args) {
        doLog(DEBUG, throwable, message, args);
    }

    public static void i(String message, Object... args) {
        doLog(INFO, null, message, args);
    }

    public static void i(String message, Throwable throwable, Object... args) {
        doLog(INFO, throwable, message, args);
    }

    public static void w(String message, Object... args) {
        doLog(WARN, null, message, args);
    }

    public static void w(String message, Throwable throwable, Object... args) {
        doLog(WARN, throwable, message, args);
    }

    public static void e(String message, Object... args) {
        doLog(ERROR, null, message, args);
    }

    public static void e(String message, Throwable throwable, Object... args) {
        doLog(ERROR, throwable, message, args);
    }

    public static void a(String message, Object... args) {
        doLog(ASSERT, null, message, args);
    }

    public static void a(String message, Throwable throwable, Object... args) {
        doLog(ASSERT, throwable, message, args);
    }
}
