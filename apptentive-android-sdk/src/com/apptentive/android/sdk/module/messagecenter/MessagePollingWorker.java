/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter;

import android.content.Context;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.Configuration;
import com.apptentive.android.sdk.module.metric.MetricModule;

/**
 * @author Sky Kelsey
 */
public class MessagePollingWorker {

    private static Context appContext;
    private static MessagePollingThread messagePollingThread;
    private static boolean running;
    private static boolean foreground = false;
    private static long backgroundPollingInterval = -1;
    private static long foregroundPollingInterval = -1;
    private static long runningActivities = 0;

    public static synchronized void doStart(Context context) {
        appContext = context.getApplicationContext();

        if (!running) {
            Log.i("Starting MessagePollingWorker.");

            if (backgroundPollingInterval == -1 || foregroundPollingInterval == -1) {
                Configuration conf = Configuration.load(context);
                backgroundPollingInterval = conf.getMessageCenterBgPoll() * 1000;
                foregroundPollingInterval = conf.getMessageCenterFgPoll() * 1000;
            }

            running = true;
            messagePollingThread = new MessagePollingThread();
            Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    MetricModule.sendError(appContext, throwable, null, null);
                }
            };
            messagePollingThread.setUncaughtExceptionHandler(handler);
            messagePollingThread.setName("Apptentive-MessagePollingWorker");
            messagePollingThread.start();
        }
    }

    private static void goToSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // This is normal and happens whenever we wake the thread with an interrupt.
        }
    }

    public static void wakeUp() {
        messagePollingThread.interrupt();
    }

    public static void start(Context context) {
        runningActivities++;
        doStart(context);
    }

    public static void stop() {
        runningActivities--;
        // If there are no running activities, wake the thread so it can stop immediately and gracefully.
        if (runningActivities == 0) {
            wakeUp();
        }
    }

    /**
     * If coming from the background, wake the thread so that it immediately starts runs and runs more often. If coming
     * from the foreground, let the polling interval timeout naturally, at which point the polling interval will become
     * the background polling interval.
     *
     * @param foreground true if the worker should be in foreground polling mode, else false.
     */
    public static void setForeground(boolean foreground) {
        boolean enteringForeground = foreground && !MessagePollingWorker.foreground;
        MessagePollingWorker.foreground = foreground;
        if (enteringForeground) {
            wakeUp();
        }
    }

    private static class MessagePollingThread extends Thread {
        public void run() {
            try {
                synchronized (this) {
                    if (appContext == null) {
                        return;
                    }
                    while (runningActivities > 0) {
                        long pollingInterval = foreground ? foregroundPollingInterval : backgroundPollingInterval;
                        Log.i("Checking server for new messages every %d seconds", pollingInterval / 1000);
                        MessageManager.fetchAndStoreMessages(appContext);
                        MessagePollingWorker.goToSleep(pollingInterval);
                    }
                }
            } finally {
                running = false;
            }
        }
    }
}
