/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;

import com.apptentive.android.sdk.comm.ApptentiveClient;
import com.apptentive.android.sdk.comm.ApptentiveHttpResponse;
import com.apptentive.android.sdk.comm.NetworkStateListener;
import com.apptentive.android.sdk.comm.NetworkStateReceiver;
import com.apptentive.android.sdk.lifecycle.ActivityLifecycleManager;
import com.apptentive.android.sdk.model.AppRelease;
import com.apptentive.android.sdk.model.Configuration;
import com.apptentive.android.sdk.model.ConversationTokenRequest;
import com.apptentive.android.sdk.model.CustomData;
import com.apptentive.android.sdk.model.Device;
import com.apptentive.android.sdk.model.FileMessage;
import com.apptentive.android.sdk.model.Person;
import com.apptentive.android.sdk.model.Sdk;
import com.apptentive.android.sdk.model.TextMessage;
import com.apptentive.android.sdk.module.engagement.EngagementModule;
import com.apptentive.android.sdk.module.engagement.interaction.InteractionManager;
import com.apptentive.android.sdk.module.messagecenter.ApptentiveMessageCenter;
import com.apptentive.android.sdk.module.messagecenter.MessageManager;
import com.apptentive.android.sdk.module.messagecenter.MessagePollingWorker;
import com.apptentive.android.sdk.module.messagecenter.UnreadMessagesListener;
import com.apptentive.android.sdk.module.metric.MetricModule;
import com.apptentive.android.sdk.module.rating.IRatingProvider;
import com.apptentive.android.sdk.module.survey.OnSurveyAvailableListener;
import com.apptentive.android.sdk.module.survey.OnSurveyFinishedListener;
import com.apptentive.android.sdk.storage.AppReleaseManager;
import com.apptentive.android.sdk.storage.ApptentiveDatabase;
import com.apptentive.android.sdk.storage.DeviceManager;
import com.apptentive.android.sdk.storage.PayloadSendWorker;
import com.apptentive.android.sdk.storage.PersonManager;
import com.apptentive.android.sdk.storage.SdkManager;
import com.apptentive.android.sdk.storage.VersionHistoryStore;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class contains the complete API for accessing Apptentive features from within your app.
 *
 * @author Sky Kelsey
 */
public class Apptentive {

    /**
     * The key to use to store a Map of Urban Airship configuration settings.
     */
    public static final String INTEGRATION_URBAN_AIRSHIP = "urban_airship";

    // ****************************************************************************************
    // DELEGATE METHODS
    // ****************************************************************************************
    /**
     * The key to use to specify the Urban Airship APID withing the Urban Airship configuration settings Map.
     */
    public static final String INTEGRATION_URBAN_AIRSHIP_APID = "token";
    public static final String INTEGRATION_AWS_SNS = "aws_sns";


    // ****************************************************************************************
    // GLOBAL DATA METHODS
    // ****************************************************************************************
    public static final String INTEGRATION_AWS_SNS_TOKEN = "token";
    /**
     * The key that is used to store extra data on an Apptentive push notification.
     */
    public static final String APPTENTIVE_PUSH_EXTRA_KEY = "apptentive";

    private Apptentive() {
    }

    /**
     * Call this method from each of your Activities' onStart() methods. Must be called before using other Apptentive APIs
     * methods
     *
     * @param activity The Activity from which this method is called.
     */
    public static void onStart(Activity activity) {
        try {
            init(activity);
            ActivityLifecycleManager.activityStarted(activity);
            PayloadSendWorker.activityStarted(activity.getApplicationContext());
            MessagePollingWorker.start(activity.getApplicationContext());
        } catch (Exception e) {
            Log.w("Error starting Apptentive Activity.", e);
            MetricModule.sendError(activity.getApplicationContext(), e, null, null);
        }
    }

    /**
     * Call this method from each of your Activities' onStop() methods.
     *
     * @param activity The Activity from which this method is called.
     */
    public static void onStop(Activity activity) {
        try {
            ActivityLifecycleManager.activityStopped(activity);
            NetworkStateReceiver.clearListeners();
            PayloadSendWorker.activityStopped();
            MessagePollingWorker.stop();
        } catch (Exception e) {
            Log.w("Error stopping Apptentive Activity.", e);
            MetricModule.sendError(activity.getApplicationContext(), e, null, null);
        }
    }

    /**
     * Sets the initial user email address. This email address will be sent to the Apptentive server to allow out of app
     * communication, and to help provide more context about this user. This email will be the definitive email address
     * for this user, unless one is provided directly by the user through an Apptentive UI. Calls to this method are
     * idempotent.
     *
     * @param context The context from which this method is called.
     * @param email   The user's email address.
     */
    public static void setInitialUserEmail(Context context, String email) {
        PersonManager.storeInitialPersonEmail(context, email);
    }

    /**
     * <p>Allows you to pass arbitrary string data to the server along with this device's info. This method will replace all
     * custom device data that you have set for this app. Calls to this method are idempotent.</p>
     * <p>To add a single piece of custom device data, use {@link #addCustomDeviceData}</p>
     * <p>To remove a single piece of custom device data, use {@link #removeCustomDeviceData}</p>
     *
     * @param context          The context from which this method is called.
     * @param customDeviceData A Map of key/value pairs to send to the server.
     */
    public static void setCustomDeviceData(Context context, Map<String, String> customDeviceData) {
        try {
            CustomData customData = new CustomData();
            for (String key : customDeviceData.keySet()) {
                customData.put(key, customDeviceData.get(key));
            }
            DeviceManager.storeCustomDeviceData(context, customData);
        } catch (JSONException e) {
            Log.w("Unable to set custom device data.", e);
        }
    }


    // ****************************************************************************************
    // THIRD PARTY INTEGRATIONS
    // ****************************************************************************************

    /**
     * Add a piece of custom data to the device's info. This info will be sent to the server.  Calls to this method are
     * idempotent.
     *
     * @param context The context from which this method is called.
     * @param key     The key to store the data under.
     * @param value   The value of the data.
     */
    public static void addCustomDeviceData(Context context, String key, String value) {
        if (key == null || key.trim().length() == 0) {
            return;
        }
        CustomData customData = DeviceManager.loadCustomDeviceData(context);
        if (customData != null) {
            try {
                customData.put(key, value);
                DeviceManager.storeCustomDeviceData(context, customData);
            } catch (JSONException e) {
                Log.w("Unable to add custom device data.", e);
            }
        }
    }

    /**
     * Remove a piece of custom data from the device's info. Calls to this method are idempotent.
     *
     * @param context The context from which this method is called.
     * @param key     The key to remove.
     */
    public static void removeCustomDeviceData(Context context, String key) {
        CustomData customData = DeviceManager.loadCustomDeviceData(context);
        if (customData != null) {
            customData.remove(key);
            DeviceManager.storeCustomDeviceData(context, customData);
        }
    }

    /**
     * <p>Allows you to pass arbitrary string data to the server along with this person's info. This method will replace all
     * custom person data that you have set for this app. Calls to this method are idempotent.</p>
     * <p>To add a single piece of custom person data, use {@link #addCustomPersonData}</p>
     * <p>To remove a single piece of custom person data, use {@link #removeCustomPersonData}</p>
     *
     * @param context          The context from which this method is called.
     * @param customPersonData A Map of key/value pairs to send to the server.
     */
    public static void setCustomPersonData(Context context, Map<String, String> customPersonData) {
        Log.w("Setting custom person data: %s", customPersonData.toString());
        try {
            CustomData customData = new CustomData();
            for (String key : customPersonData.keySet()) {
                customData.put(key, customPersonData.get(key));
            }
            PersonManager.storeCustomPersonData(context, customData);
        } catch (JSONException e) {
            Log.e("Unable to set custom person data.", e);
        }
    }

    /**
     * Add a piece of custom data to the person's info. This info will be sent to the server. Calls to this method are
     * idempotent.
     *
     * @param context The context from which this method is called.
     * @param key     The key to store the data under.
     * @param value   The value of the data.
     */
    public static void addCustomPersonData(Context context, String key, String value) {
        if (key == null || key.trim().length() == 0) {
            return;
        }
        CustomData customData = PersonManager.loadCustomPersonData(context);
        if (customData != null) {
            try {
                customData.put(key, value);
                PersonManager.storeCustomPersonData(context, customData);
            } catch (JSONException e) {
                Log.w("Unable to add custom person data.", e);
            }
        }
    }

    /**
     * Remove a piece of custom data from the person's info. Calls to this method are idempotent.
     *
     * @param context The context from which this method is called.
     * @param key     The key to remove.
     */
    public static void removeCustomPersonData(Context context, String key) {
        CustomData customData = PersonManager.loadCustomPersonData(context);
        if (customData != null) {
            customData.remove(key);
            PersonManager.storeCustomPersonData(context, customData);
        }
    }

    /**
     * Allows you to pass in third party integration details. Each integration that is supported at the time this version
     * of the SDK is published is listed below.
     * <p/>
     * <ul>
     * <li>
     * Urban Airship push notifications
     * </li>
     * </ul>
     *
     * @param context     The Context from which this method is called.
     * @param integration The name of the integration. Integrations known at the time this SDK was released are listed below.
     * @param config      A String to String Map of key/value pairs representing all necessary configuration data Apptentive needs
     *                    to use the specific third party integration.
     */
    public static void addIntegration(Context context, String integration, Map<String, String> config) {
        if (integration == null || config == null) {
            return;
        }
        CustomData integrationConfig = DeviceManager.loadIntegrationConfig(context);
        try {
            JSONObject configJson = null;
            if (!integrationConfig.isNull(integration)) {
                configJson = integrationConfig.getJSONObject(integration);
            } else {
                configJson = new JSONObject();
                integrationConfig.put(integration, configJson);
            }
            for (String key : config.keySet()) {
                configJson.put(key, config.get(key));
            }
            Log.d("Adding integration config: %s", config.toString());
            DeviceManager.storeIntegrationConfig(context, integrationConfig);
            syncDevice(context);
        } catch (JSONException e) {
            Log.e("Error adding integration: %s, %s", e, integration, config.toString());
        }
    }

    /**
     * Configures Apptentive to work with Urban Airship push notifications. You must first set up your app to work with
     * Urban Airship to use this integration. This method must be called when you finish initializing Urban Airship. Since
     * Urban Airship creates an APID after it connects to its server, the APID may be null at first. The preferred method
     * of retrieving the APID is to listen to the <code>PushManager.ACTION_REGISTRATION_FINISHED</code> Intent in your
     * {@link android.content.BroadcastReceiver}. You can alternately find the APID by calling
     * <a href="http://docs.urbanairship.com/reference/libraries/android/latest/reference/com/urbanairship/push/PushManager.html#getAPID%28%29">PushManager.shared().getAPID()</a>
     * <p/>
     * Note: Initializing Urban Airship may take a few seconds. You may need to close and reopen the app in order to force
     * the APID to be sent to our server. Push notifications will not be delivered to this app install until our server
     * receives the APID.
     *
     * @param context The Context from which this method is called.
     * @param apid    The Airship Push ID (APID).
     */
    public static void addUrbanAirshipPushIntegration(Context context, String apid) {
        if (apid != null) {
            Log.i("Setting Urban Airship APID: %s", apid);
            Map<String, String> config = new HashMap<String, String>();
            config.put(Apptentive.INTEGRATION_URBAN_AIRSHIP_APID, apid);
            addIntegration(context, Apptentive.INTEGRATION_URBAN_AIRSHIP, config);
        }
    }


    // ****************************************************************************************
    // PUSH NOTIFICATIONS
    // ****************************************************************************************

    /**
     * Configures Apptentive to work with Amazon Web Services (AWS) Simple Notification Service (SNS) push notifications.
     * You must first set up your app to work with AWS SNS to use this integration. This method must be called when you
     * finish initializing AWS SNS using
     * <a href="http://developer.android.com/reference/com/google/android/gms/gcm/GoogleCloudMessaging.html#register%28java.lang.String...%29">
     * GoogleCloudMessaging.register(String... senderIds)</a>,
     * which returns the Registration ID. You will need to pass this returned Registration ID into this method.
     * <p/>
     * Note: You may need to close and reopen the app in order to force the Registration ID to be sent to our server.
     * Push notifications will not be delivered to this app install until our server receives the Registration ID.
     *
     * @param context        The Context from which this method was called.
     * @param registrationId The registrationId returned from
     *                       <a href="http://developer.android.com/reference/com/google/android/gms/gcm/GoogleCloudMessaging.html#register%28java.lang.String...%29">
     *                       GoogleCloudMessaging.register(String... senderIds)</a>.
     */
    public static void addAmazonSnsPushIntegration(Context context, String registrationId) {
        if (registrationId != null) {
            Log.i("Setting Amazon AWS token: %s", registrationId);
            Map<String, String> config = new HashMap<String, String>();
            config.put(Apptentive.INTEGRATION_AWS_SNS_TOKEN, registrationId);
            addIntegration(context, Apptentive.INTEGRATION_AWS_SNS, config);
        }
    }

    /**
     * Saves Apptentive specific data form a push notification Intent. In your BroadcastReceiver, if the push notification
     * came from Apptentive, it will have data that needs to be saved before you launch your Activity. You must call this
     * method <strong>every time</strong> you get a push opened Intent, and before you launch your Activity. If the push
     * notification did not come from Apptentive, this method has no effect.
     *
     * @param context The Context from which this method is called.
     * @param intent  The Intent that you received when the user opened a push notification.
     */
    public static void setPendingPushNotification(Context context, Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(Apptentive.APPTENTIVE_PUSH_EXTRA_KEY)) {
                Log.i("Saving pending push intent.");
                String extra = extras.getString(Apptentive.APPTENTIVE_PUSH_EXTRA_KEY);
                SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                prefs.edit().putString(Constants.PREF_KEY_PENDING_PUSH_NOTIFICATION, extra).commit();
            }
        }
    }

    /**
     * Launches Apptentive features based on a push notification Intent. Before you call this, you must call
     * {@link Apptentive#setPendingPushNotification(Context, Intent)} in your Broadcast receiver when a push notification
     * is opened by the user. Call this method must be called from the Activity that you launched from the
     * BroadcastReceiver. This method will only handle Apptentive originated push notifications, so call is anytime you
     * receive a push.
     *
     * @param activity The Activity from which this method is called.
     * @return True if a call to this method resulted in Apptentive displaying a View.
     */
    public static boolean handleOpenedPushNotification(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String pushData = prefs.getString(Constants.PREF_KEY_PENDING_PUSH_NOTIFICATION, null);
        prefs.edit().remove(Constants.PREF_KEY_PENDING_PUSH_NOTIFICATION).commit(); // Remove our data so this won't run twice.
        if (pushData != null) {
            Log.e("Handling Apptentive Push Intent.");
            try {
                JSONObject pushJson = new JSONObject(pushData);
                ApptentiveInternal.PushAction action = ApptentiveInternal.PushAction.unknown;
                if (pushJson.has(ApptentiveInternal.PUSH_ACTION)) {
                    action = ApptentiveInternal.PushAction.parse(pushJson.getString(ApptentiveInternal.PUSH_ACTION));
                }
                switch (action) {
                    case pmc:
                        Apptentive.showMessageCenter(activity);
                        return true;
                    default:
                        Log.v("Unknown Push Notification Action \"%s\"", action.name());
                }
            } catch (JSONException e) {
                Log.w("Error parsing JSON from push notification.", e);
                MetricModule.sendError(activity.getApplicationContext(), e, "Parsing Push notification", pushData);
            }
        }
        return false;
    }

    /**
     * Determines whether a push was sent by Apptentive. Apptentive push notifications will result in an Intent
     * containing a string extra key of {@link Apptentive#APPTENTIVE_PUSH_EXTRA_KEY}.
     *
     * @param intent The push notification Intent you received in your BroadcastReceiver.
     * @return True if the Intent contains Apptentive push information.
     */
    public static boolean isApptentivePushNotification(Intent intent) {
        return intent != null && intent.getExtras() != null && intent.getExtras().getString(APPTENTIVE_PUSH_EXTRA_KEY) != null;
    }


    // ****************************************************************************************
    // RATINGS
    // ****************************************************************************************

    /**
     * Use this to choose where to send the user when they are prompted to rate the app. This should be the same place
     * that the app was downloaded from.
     *
     * @param ratingProvider A {@link IRatingProvider} value.
     */

    public static void setRatingProvider(IRatingProvider ratingProvider) {
        ApptentiveInternal.setRatingProvider(ratingProvider);
    }

    /**
     * If there are any properties that your {@link IRatingProvider} implementation requires, populate them here. This
     * is not currently needed with the Google Play and Amazon Appstore IRatingProviders.
     *
     * @param key   A String
     * @param value A String
     */
    public static void putRatingProviderArg(String key, String value) {
        ApptentiveInternal.putRatingProviderArg(key, value);
    }

    // ****************************************************************************************
    // MESSAGE CENTER
    // ****************************************************************************************


    /**
     * Opens the Apptentive Message Center UI Activity
     *
     * @param activity The Activity from which to launch the Message Center
     */
    public static void showMessageCenter(Activity activity) {
        ApptentiveMessageCenter.show(activity, true, null);
    }

    /**
     * Opens the Apptentive Message Center UI Activity, and allows custom data to be sent with the next message the user
     * sends. If the user sends multiple messages, this data will only be sent with the first message sent after this
     * method is invoked. Additional invocations of this method with custom data will repeat this process.
     *
     * @param activity   The Activity from which to launch the Message Center
     * @param customData A Map of key/value Strings that will be sent with the next message.
     */
    public static void showMessageCenter(Activity activity, Map<String, String> customData) {
        try {
            ApptentiveMessageCenter.show(activity, true, customData);
        } catch (Exception e) {
            Log.w("Error starting Apptentive Activity.", e);
            MetricModule.sendError(activity.getApplicationContext(), e, null, null);
        }
    }

    /**
     * Set a listener to be notified when the number of unread messages in the Message Center changes.
     *
     * @param listener An UnreadMessageListener that you instantiate.
     */
    public static void setUnreadMessagesListener(UnreadMessagesListener listener) {
        MessageManager.setHostUnreadMessagesListener(listener);
    }

    /**
     * Returns the number of unread messages in the Message Center.
     *
     * @param context The Context from which this method is called.
     * @return The number of unread messages.
     */
    public static int getUnreadMessageCount(Context context) {
        try {
            return MessageManager.getUnreadMessageCount(context);
        } catch (Exception e) {
            MetricModule.sendError(context.getApplicationContext(), e, null, null);
        }
        return 0;
    }

    /**
     * Sends a text message to the server. This message will be visible in the conversation view on the server, but will
     * not be shown in the client's Message Center.
     *
     * @param context The Context from which this method is called.
     * @param text    The message you wish to send.
     */
    public static void sendAttachmentText(Context context, String text) {
        try {
            TextMessage message = new TextMessage();
            message.setBody(text);
            message.setHidden(true);
            MessageManager.sendMessage(context, message);
        } catch (Exception e) {
            Log.w("Error sending attachment text.", e);
            MetricModule.sendError(context, e, null, null);
        }
    }

    /**
     * Sends a file to the server. This file will be visible in the conversation view on the server, but will not be shown
     * in the client's Message Center. A local copy of this file will be made until the message is transmitted, at which
     * point the temporary file will be deleted.
     *
     * @param context The Context from which this method was called.
     * @param uri     The URI of the local resource file.
     */
    public static void sendAttachmentFile(Context context, String uri) {
        try {
            FileMessage message = new FileMessage();
            message.setHidden(true);

            boolean successful = message.createStoredFile(context, uri);
            if (successful) {
                message.setRead(true);
                // Finally, send out the message.
                MessageManager.sendMessage(context, message);
            }
        } catch (Exception e) {
            Log.w("Error sending attachment file.", e);
            MetricModule.sendError(context, e, null, null);
        }
    }

    /**
     * Sends a file to the server. This file will be visible in the conversation view on the server, but will not be shown
     * in the client's Message Center. A local copy of this file will be made until the message is transmitted, at which
     * point the temporary file will be deleted.
     *
     * @param context  The Context from which this method was called.
     * @param content  A byte array of the file contents.
     * @param mimeType The mime type of the file.
     */
    public static void sendAttachmentFile(Context context, byte[] content, String mimeType) {
        try {
            FileMessage message = new FileMessage();
            message.setHidden(true);

            boolean successful = message.createStoredFile(context, content, mimeType);
            if (successful) {
                message.setRead(true);
                // Finally, send out the message.
                MessageManager.sendMessage(context, message);
            }
        } catch (Exception e) {
            Log.w("Error sending attachment file.", e);
            MetricModule.sendError(context, e, null, null);
        }
    }

    /**
     * Sends a file to the server. This file will be visible in the conversation view on the server, but will not be shown
     * in the client's Message Center. A local copy of this file will be made until the message is transmitted, at which
     * point the temporary file will be deleted.
     *
     * @param context  The Context from which this method was called.
     * @param is       An InputStream from the desired file.
     * @param mimeType The mime type of the file.
     */
    public static void sendAttachmentFile(Context context, InputStream is, String mimeType) {
        try {
            FileMessage message = new FileMessage();
            message.setHidden(true);

            boolean successful = false;
            try {
                successful = message.createStoredFile(context, is, mimeType);
            } catch (IOException e) {
                Log.e("Error creating local copy of file attachment.");
            }
            if (successful) {
                message.setRead(true);
                // Finally, send out the message.
                MessageManager.sendMessage(context, message);
            }
        } catch (Exception e) {
            Log.w("Error sending attachment file.", e);
            MetricModule.sendError(context, e, null, null);
        }
    }

    /**
     * This method takes a unique event string, stores a record of that event having been visited, figures out
     * if there is an interaction that is able to run for this event, and then runs it. If more than one interaction
     * can run, then the most appropriate interaction takes precedence. Only one interaction at most will run per
     * invocation of this method.
     *
     * @param activity The Activity from which this method is called.
     * @param event    A unique String representing the line this method is called on. For instance, you may want to have
     *                 the ability to target interactions to run after the user uploads a file in your app. You may then
     *                 call <strong><code>engage(activity, "finished_upload");</code></strong>
     * @return true if the an interaction was shown, else false.
     */
    public static synchronized boolean engage(Activity activity, String event) {
        return EngagementModule.engage(activity, "local", "app", event);
    }

    /**
     * Pass in a listener. The listener will be called whenever a survey is finished.
     *
     * @param listener The {@link com.apptentive.android.sdk.module.survey.OnSurveyFinishedListener} listener to call when the survey is finished.
     */
    public static void setOnSurveyFinishedListener(OnSurveyFinishedListener listener) {
        ApptentiveInternal.setOnSurveyFinishedListener(listener);
    }

    public static void setSurveyAvailableListener(OnSurveyAvailableListener listener) {
        ApptentiveInternal.setOnSurveyAvailableListener(listener);
    }
    // ****************************************************************************************
    // INTERNAL METHODS
    // ****************************************************************************************

    private static void init(final Context context) {

        //
        // First, initialize data relies on synchronous reads from local resources.
        //

        if (!GlobalInfo.initialized) {
            SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
            NetworkStateReceiver.clearListeners();

            // First, Get the api key, and figure out if app is debuggable.
            GlobalInfo.isAppDebuggable = false;
            String apiKey = null;
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                Bundle metaData = ai.metaData;
                apiKey = metaData.getString(Constants.MANIFEST_KEY_APPTENTIVE_API_KEY);

                boolean debugFlagSet = (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                boolean apptentiveDebugSet = metaData.getBoolean(Constants.MANIFEST_KEY_APPTENTIVE_DEBUG);
                GlobalInfo.isAppDebuggable = debugFlagSet || apptentiveDebugSet;
            } catch (Exception e) {
                Log.e("Unexpected error while reading application info.", e);
            }

            Log.i("Debug mode enabled? %b", GlobalInfo.isAppDebuggable);

            // If we are in debug mode, but no api key is found, throw an exception. Otherwise, just assert log. We don't want to crash a production app.
            String errorString = "No Apptentive api key specified. Please make sure you have specified your api key in your AndroidManifest.xml";
            if ((apiKey == null || apiKey.equals(""))) {
                if (GlobalInfo.isAppDebuggable) {
                    throw new RuntimeException(errorString);
                } else {
                    Log.e(errorString);
                }
            }
            GlobalInfo.apiKey = apiKey;

            Log.i("API Key: %s", GlobalInfo.apiKey);

            // Grab app info we need to access later on.
            GlobalInfo.appPackage = context.getPackageName();
            GlobalInfo.androidId = Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            // Check the host app version, and notify modules if it's changed.
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

                Integer currentVersionCode = packageInfo.versionCode;
                String currentVersionName = packageInfo.versionName;
                VersionHistoryStore.VersionHistoryEntry lastVersionEntrySeen = VersionHistoryStore.getLastVersionSeen(context);
                if (lastVersionEntrySeen == null) {
                    onVersionChanged(context, null, currentVersionCode, null, currentVersionName);
                } else {
                    if (!currentVersionCode.equals(lastVersionEntrySeen.versionCode) || !currentVersionName.equals(lastVersionEntrySeen.versionName)) {
                        onVersionChanged(context, lastVersionEntrySeen.versionCode, currentVersionCode, lastVersionEntrySeen.versionName, currentVersionName);
                    }
                }

                GlobalInfo.appDisplayName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageInfo.packageName, 0)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                // Nothing we can do then.
                GlobalInfo.appDisplayName = "this app";
            }

            // Listen for network state changes.
            NetworkStateListener networkStateListener = new NetworkStateListener() {
                public void stateChanged(NetworkInfo networkInfo) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        Log.v("Network connected.");
                        PayloadSendWorker.ensureRunning(context);
                    }
                    if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                        Log.v("Network disconnected.");
                    }
                }
            };
            NetworkStateReceiver.addListener(networkStateListener);

            // Grab the conversation token from shared preferences.
            if (prefs.contains(Constants.PREF_KEY_CONVERSATION_TOKEN) && prefs.contains(Constants.PREF_KEY_PERSON_ID)) {
                GlobalInfo.conversationToken = prefs.getString(Constants.PREF_KEY_CONVERSATION_TOKEN, null);
                GlobalInfo.personId = prefs.getString(Constants.PREF_KEY_PERSON_ID, null);
            }

            GlobalInfo.initialized = true;
            Log.v("Done initializing...");
        } else {
            Log.v("Already initialized...");
        }

        // Initialize the Conversation Token, or fetch if needed. Fetch config it the token is available.
        if (GlobalInfo.conversationToken == null || GlobalInfo.personId == null) {
            asyncFetchConversationToken(context);
        } else {
            asyncFetchAppConfiguration(context);
            InteractionManager.asyncFetchAndStoreInteractions(context);
        }

        // TODO: Do this on a dedicated thread if it takes too long. Some devices are slow to read device data.
        syncDevice(context);
        syncSdk(context);
        syncPerson(context);

        Log.d("Default Locale: %s", Locale.getDefault().toString());
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        Log.d("Conversation id: %s", prefs.getString(Constants.PREF_KEY_CONVERSATION_ID, "null"));
    }

    private static void onVersionChanged(Context context, Integer previousVersionCode, Integer currentVersionCode, String previousVersionName, String currentVersionName) {
        Log.i("Version changed: Name: %s => %s, Code: %d => %d", previousVersionName, currentVersionName, previousVersionCode, currentVersionCode);
        VersionHistoryStore.updateVersionHistory(context, currentVersionCode, currentVersionName);
        AppRelease appRelease = AppReleaseManager.storeAppReleaseAndReturnDiff(context);
        if (appRelease != null) {
            Log.d("App release was updated.");
            ApptentiveDatabase.getInstance(context).addPayload(appRelease);
        }
    }

    private synchronized static void asyncFetchConversationToken(final Context context) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                fetchConversationToken(context);
            }
        };
        Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.w("Caught UncaughtException in thread \"%s\"", throwable, thread.getName());
                MetricModule.sendError(context.getApplicationContext(), throwable, null, null);
            }
        };
        thread.setUncaughtExceptionHandler(handler);
        thread.setName("Apptentive-FetchConversationToken");
        thread.start();
    }

    /**
     * First looks to see if we've saved the ConversationToken in memory, then in SharedPreferences, and finally tries to get one
     * from the server.
     */
    private static void fetchConversationToken(Context context) {
        // Try to fetch a new one from the server.
        ConversationTokenRequest request = new ConversationTokenRequest();

        // Send the Device and Sdk now, so they are available on the server from the start.
        request.setDevice(DeviceManager.storeDeviceAndReturnIt(context));
        request.setSdk(SdkManager.storeSdkAndReturnIt(context));
        request.setPerson(PersonManager.storePersonAndReturnIt(context));

        // TODO: Allow host app to send a user id, if available.
        ApptentiveHttpResponse response = ApptentiveClient.getConversationToken(request);
        if (response == null) {
            Log.w("Got null response fetching ConversationToken.");
            return;
        }
        if (response.isSuccessful()) {
            try {
                JSONObject root = new JSONObject(response.getContent());
                String conversationToken = root.getString("token");
                Log.d("ConversationToken: " + conversationToken);
                String conversationId = root.getString("id");
                SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                if (conversationToken != null && !conversationToken.equals("")) {
                    GlobalInfo.conversationToken = conversationToken;
                    prefs.edit().putString(Constants.PREF_KEY_CONVERSATION_TOKEN, conversationToken).commit();
                    prefs.edit().putString(Constants.PREF_KEY_CONVERSATION_ID, conversationId).commit();
                }
                String personId = root.getString("person_id");
                Log.d("PersonId: " + personId);
                if (personId != null && !personId.equals("")) {
                    GlobalInfo.personId = personId;
                    prefs.edit().putString(Constants.PREF_KEY_PERSON_ID, personId).commit();
                }
                // Try to fetch app configuration, since it depends on the conversation token.
                asyncFetchAppConfiguration(context);
                InteractionManager.asyncFetchAndStoreInteractions(context);
            } catch (JSONException e) {
                Log.e("Error parsing ConversationToken response json.", e);
            }
        }
    }

    /**
     * Fetches the app configuration from the server and stores the keys into our SharedPreferences.
     *
     * @param force If true, will always fetch configuration. If false, only fetches configuration if the cached
     *              configuration has expired.
     */
    private static void fetchAppConfiguration(Context context, boolean force) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        // Don't get the app configuration unless forced, or the cache has expired.
        if (!force) {
            Configuration config = Configuration.load(prefs);
            Long expiration = config.getConfigurationCacheExpirationMillis();
            if (System.currentTimeMillis() < expiration) {
                Log.v("Using cached configuration.");
                return;
            }
        }

        Log.v("Fetching new configuration.");
        ApptentiveHttpResponse response = ApptentiveClient.getAppConfiguration();
        if (!response.isSuccessful()) {
            return;
        }

        try {
            String cacheControl = response.getHeaders().get("Cache-Control");
            Integer cacheSeconds = Util.parseCacheControlHeader(cacheControl);
            if (cacheSeconds == null) {
                cacheSeconds = Constants.CONFIG_DEFAULT_APP_CONFIG_EXPIRATION_DURATION_SECONDS;
            }
            Log.e("Caching configuration for %d seconds.", cacheSeconds);
            Configuration config = new Configuration(response.getContent());
            config.setConfigurationCacheExpirationMillis(System.currentTimeMillis() + cacheSeconds * 1000);
            config.save(context);
        } catch (JSONException e) {
            Log.e("Error parsing app configuration from server.", e);
        }
    }

    private static void asyncFetchAppConfiguration(final Context context) {
        Thread thread = new Thread() {
            public void run() {
                fetchAppConfiguration(context, GlobalInfo.isAppDebuggable);
            }
        };
        Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e("Caught UncaughtException in thread \"%s\"", throwable, thread.getName());
                MetricModule.sendError(context.getApplicationContext(), throwable, null, null);
            }
        };
        thread.setUncaughtExceptionHandler(handler);
        thread.setName("Apptentive-FetchAppConfiguration");
        thread.start();
    }

    /**
     * Sends current Device to the server if it differs from the last time it was sent.
     *
     * @param context
     */
    private static void syncDevice(Context context) {
        Device deviceInfo = DeviceManager.storeDeviceAndReturnDiff(context);
        if (deviceInfo != null) {
            Log.d("Device info was updated.");
            Log.v(deviceInfo.toString());
            ApptentiveDatabase.getInstance(context).addPayload(deviceInfo);
        } else {
            Log.d("Device info was not updated.");
        }
    }

    /**
     * Sends current Sdk to the server if it differs from the last time it was sent.
     *
     * @param context
     */
    private static void syncSdk(Context context) {
        Sdk sdk = SdkManager.storeSdkAndReturnDiff(context);
        if (sdk != null) {
            Log.d("Sdk was updated.");
            Log.v(sdk.toString());
            ApptentiveDatabase.getInstance(context).addPayload(sdk);
        } else {
            Log.d("Sdk was not updated.");
        }
    }

    /**
     * Sends current Person to the server if it differs from the last time it was sent.
     *
     * @param context
     */
    private static void syncPerson(Context context) {
        Person person = PersonManager.storePersonAndReturnDiff(context);
        if (person != null) {
            Log.d("Person was updated.");
            Log.v(person.toString());
            ApptentiveDatabase.getInstance(context).addPayload(person);
        } else {
            Log.d("Person was not updated.");
        }
    }
}