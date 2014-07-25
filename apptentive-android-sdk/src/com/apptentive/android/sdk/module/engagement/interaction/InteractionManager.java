/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction;

import android.content.Context;
import android.content.SharedPreferences;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.comm.ApptentiveClient;
import com.apptentive.android.sdk.comm.ApptentiveHttpResponse;
import com.apptentive.android.sdk.module.engagement.interaction.model.Interaction;
import com.apptentive.android.sdk.module.engagement.interaction.model.Interactions;
import com.apptentive.android.sdk.module.metric.MetricModule;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;

import java.util.List;

/**
 * @author Sky Kelsey
 */
public class InteractionManager {

    public static void asyncFetchAndStoreInteractions(final Context context) {

        if (hasCacheExpired(context)) {
            Log.d("Interaction cache has expired. Fetching new interactions.");
            Thread thread = new Thread() {
                public void run() {
                    fetchAndStoreInteractions(context);
                }
            };
            Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    Log.w("UncaughtException in InteractionManager.", throwable);
                    MetricModule.sendError(context.getApplicationContext(), throwable, null, null);
                }
            };
            thread.setUncaughtExceptionHandler(handler);
            thread.setName("Apptentive-FetchInteractions");
            thread.start();
        } else {
            Log.d("Interaction cache has not expired. Using existing interactions.");
        }
    }

    public static void fetchAndStoreInteractions(Context context) {
        ApptentiveHttpResponse response = ApptentiveClient.getInteractions();

        if (response != null && response.isSuccessful()) {
            String interactionsString = response.getContent();

            // Store new integration cache expiration.
            String cacheControl = response.getHeaders().get("Cache-Control");
            Integer cacheSeconds = Util.parseCacheControlHeader(cacheControl);
            if (cacheSeconds == null) {
                cacheSeconds = Constants.CONFIG_DEFAULT_INTERACTION_CACHE_EXPIRATION_DURATION_SECONDS;
            }
            updateCacheExpiration(context, cacheSeconds);
            storeInteractions(context, interactionsString);
        }
    }

    public static Interaction getApplicableInteraction(Context context, String fullCodePoint) {
        Interactions interactions = loadInteractions(context);
        if (interactions != null) {
            List<Interaction> list = interactions.getInteractionList(fullCodePoint);
            for (Interaction interaction : list) {
                if (interaction.canRun(context)) {
                    return interaction;
                }
            }
        }
        return null;
    }

    private static String loadInteractionsString(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREF_KEY_INTERACTIONS, null);
    }

    public static Interactions loadInteractions(Context context) {
        String interactionsString = loadInteractionsString(context);
        if (interactionsString != null) {
            try {
                return new Interactions(interactionsString);
            } catch (JSONException e) {
                Log.w("Exception creating Interactions object.", e);
            }
        }
        return null;
    }

    /**
     * Made public for testing. There is no other reason to use this method directly.
     */
    public static void storeInteractions(Context context, String interactionsString) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREF_KEY_INTERACTIONS, interactionsString).commit();
    }

    private static boolean hasCacheExpired(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        long expiration = prefs.getLong(Constants.PREF_KEY_INTERACTIONS_CACHE_EXPIRATION, 0);
        return expiration < System.currentTimeMillis();
    }

    private static void updateCacheExpiration(Context context, long duration) {
        long expiration = System.currentTimeMillis() + (duration * 1000);
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(Constants.PREF_KEY_INTERACTIONS_CACHE_EXPIRATION, expiration).commit();
    }
}
