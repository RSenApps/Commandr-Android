/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apptentive.android.sdk.ApptentiveInternal;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.ViewActivity;
import com.apptentive.android.sdk.model.Configuration;
import com.apptentive.android.sdk.model.Event;
import com.apptentive.android.sdk.model.FileMessage;
import com.apptentive.android.sdk.model.Message;
import com.apptentive.android.sdk.model.Person;
import com.apptentive.android.sdk.model.TextMessage;
import com.apptentive.android.sdk.module.ActivityContent;
import com.apptentive.android.sdk.module.messagecenter.view.MessageCenterIntroDialog;
import com.apptentive.android.sdk.module.messagecenter.view.MessageCenterThankYouDialog;
import com.apptentive.android.sdk.module.messagecenter.view.MessageCenterView;
import com.apptentive.android.sdk.module.metric.MetricModule;
import com.apptentive.android.sdk.storage.ApptentiveDatabase;
import com.apptentive.android.sdk.storage.PersonManager;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


/**
 * @author Sky Kelsey
 */
public class ApptentiveMessageCenter {

    protected static MessageCenterView messageCenterView;
    private static Trigger trigger;
    private static Map<String, String> customData;

    public static void show(Activity activity, boolean forced, Map<String, String> customData) {
        MessageManager.createMessageCenterAutoMessage(activity, forced);
        ApptentiveMessageCenter.trigger = (forced ? Trigger.message_center : Trigger.enjoyment_dialog);
        ApptentiveMessageCenter.customData = customData;
        show(activity);
    }

    protected static void show(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        Configuration conf = Configuration.load(activity);
        boolean enableMessageCenter = conf.isMessageCenterEnabled(activity);
        boolean emailRequired = conf.isMessageCenterEmailRequired(activity);
        boolean shouldShowIntroDialog = !enableMessageCenter || prefs.getBoolean(Constants.PREF_KEY_MESSAGE_CENTER_SHOULD_SHOW_INTRO_DIALOG, true);
        // TODO: What if there is an incoming message that is unread? Shouldn't they see the Message Center right away?
        if (shouldShowIntroDialog) {
            showIntroDialog(activity, emailRequired);
        } else {
            Intent intent = new Intent();
            intent.setClass(activity, ViewActivity.class);
            intent.putExtra(ActivityContent.KEY, ActivityContent.Type.MESSAGE_CENTER.toString());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
        }
    }

    /**
     * @param context The Activity Context that launched this view.
     */
    public static void doShow(final Context context) {
        if (!(context instanceof Activity)) {
            Log.e(ApptentiveMessageCenter.class.getSimpleName() + " must be initialized with an Activity Context.");
            return;
        }

        MetricModule.sendMetric(context, Event.EventLabel.message_center__launch, (trigger == null ? null : trigger.name()));

        MessageCenterView.OnSendMessageListener onSendMessagelistener = new MessageCenterView.OnSendMessageListener() {
            public void onSendTextMessage(String text) {
                final TextMessage message = new TextMessage();
                message.setBody(text);
                message.setRead(true);
                message.setCustomData(customData);
                customData = null;
                MessageManager.sendMessage(context, message);
                messageCenterView.post(new Runnable() {
                    public void run() {
                        messageCenterView.addMessage(message);
                    }
                });
                scrollToBottom();
            }

            public void onSendFileMessage(Uri uri) {
                // First, create the file, and populate some metadata about it.
                final FileMessage message = new FileMessage();
                boolean successful = message.internalCreateStoredImage(context, uri.toString());
                if (successful) {
                    message.setRead(true);
                    message.setCustomData(customData);
                    customData = null;
                    // Finally, send out the message.
                    MessageManager.sendMessage(context, message);
                    messageCenterView.post(new Runnable() {
                        public void run() {
                            messageCenterView.addMessage(message);
                        }
                    });
                    scrollToBottom();
                } else {
                    Log.e("Unable to send file.");
                    Toast.makeText(messageCenterView.getContext(), "Unable to send file.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        messageCenterView = new MessageCenterView((Activity) context, onSendMessagelistener);

        // Remove an existing MessageCenterView and replace it with this, if it exists.
        if (messageCenterView.getParent() != null) {
            ((ViewGroup) messageCenterView.getParent()).removeView(messageCenterView);
        }
        ((Activity) context).setContentView(messageCenterView);

        // Display the messages we already have for starters.
        messageCenterView.setMessages(MessageManager.getMessages(context));

        // This listener will run when messages are retrieved from the server, and will start a new thread to update the view.
        MessageManager.setInternalOnMessagesUpdatedListener(new MessageManager.OnNewMessagesListener() {
            public void onMessagesUpdated() {
                messageCenterView.post(new Runnable() {
                    public void run() {
                        List<Message> messages = MessageManager.getMessages(context);
                        messageCenterView.setMessages(messages);
                        scrollToBottom();
                    }
                });
            }
        });

        // Change to foreground polling, which polls more often.
        MessagePollingWorker.setForeground(true);

        // Give the MessageCenterView a callback when a message is sent.
        MessageManager.setSentMessageListener(messageCenterView);

        scrollToBottom();
    }

    /**
     * Provided only for debugging.
     */
    static void showIntroDialog(final Activity activity, Trigger trigger, boolean emailRequired) {
        ApptentiveMessageCenter.trigger = trigger;
        showIntroDialog(activity, emailRequired);
    }

    static void showIntroDialog(final Activity activity, boolean emailRequired) {
        final MessageCenterIntroDialog dialog = new MessageCenterIntroDialog(activity);
        dialog.setEmailRequired(emailRequired);

        String personEnteredEmail = PersonManager.loadPersonEmail(activity);
        String personInitialEmail = PersonManager.loadInitialPersonEmail(activity);
        if (Util.isEmpty(personEnteredEmail)) {
            if (!Util.isEmpty(personInitialEmail)) {
                dialog.setEmailFieldHidden(false);
                dialog.prePopulateEmail(personInitialEmail);
            }
        } else {
            dialog.setEmailFieldHidden(true);
        }
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                MetricModule.sendMetric(activity, Event.EventLabel.message_center__intro__cancel);
                dialog.dismiss();
            }
        });

        dialog.setOnSendListener(new MessageCenterIntroDialog.OnSendListener() {
            @Override
            public void onSend(String email, String message) {
                SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                prefs.edit().putBoolean(Constants.PREF_KEY_MESSAGE_CENTER_SHOULD_SHOW_INTRO_DIALOG, false).commit();
                // Save the email.
                if (dialog.isEmailFieldVisible()) {
                    if (email != null && email.length() != 0) {
                        PersonManager.storePersonEmail(activity, email);
                        Person person = PersonManager.storePersonAndReturnDiff(activity);
                        if (person != null) {
                            Log.d("Person was updated.");
                            Log.v(person.toString());
                            ApptentiveDatabase.getInstance(activity).addPayload(person);
                        } else {
                            Log.d("Person was not updated.");
                        }
                    }
                }
                // Send the message.
                final TextMessage textMessage = new TextMessage();
                textMessage.setBody(message);
                textMessage.setRead(true);
                textMessage.setCustomData(customData);
                customData = null;
                MessageManager.sendMessage(activity, textMessage);
                MetricModule.sendMetric(activity, Event.EventLabel.message_center__intro__send);
                dialog.dismiss();

                final MessageCenterThankYouDialog messageCenterThankYouDialog = new MessageCenterThankYouDialog(activity);
                messageCenterThankYouDialog.setValidEmailProvided(email != null && Util.isEmailValid(email));
                messageCenterThankYouDialog.setOnChoiceMadeListener(new MessageCenterThankYouDialog.OnChoiceMadeListener() {
                    @Override
                    public void onNo() {
                        MetricModule.sendMetric(activity, Event.EventLabel.message_center__thank_you__close);
                    }

                    @Override
                    public void onYes() {
                        MetricModule.sendMetric(activity, Event.EventLabel.message_center__thank_you__messages);
                        show(activity);
                    }
                });
                MetricModule.sendMetric(activity, Event.EventLabel.message_center__thank_you__launch);
                messageCenterThankYouDialog.show();
            }
        });

        String appDisplayName = Configuration.load(activity).getAppDisplayName();
        switch (trigger) {
            case enjoyment_dialog:
                dialog.setTitle(R.string.apptentive_intro_dialog_title_no_love);
                dialog.setBody(activity.getResources().getString(R.string.apptentive_intro_dialog_body, appDisplayName));
                break;
            case message_center:
                dialog.setTitle(R.string.apptentive_intro_dialog_title_default);
                dialog.setBody(activity.getResources().getString(R.string.apptentive_intro_dialog_body, appDisplayName));
                break;
            default:
                return;
        }
        MetricModule.sendMetric(activity, Event.EventLabel.message_center__intro__launch, trigger.name());
        dialog.show();
    }

    public static void clearPendingMessageCenterPushNotification(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String pushData = prefs.getString(Constants.PREF_KEY_PENDING_PUSH_NOTIFICATION, null);
        if (pushData != null) {
            try {
                JSONObject pushJson = new JSONObject(pushData);
                ApptentiveInternal.PushAction action = ApptentiveInternal.PushAction.unknown;
                if (pushJson.has(ApptentiveInternal.PUSH_ACTION)) {
                    action = ApptentiveInternal.PushAction.parse(pushJson.getString(ApptentiveInternal.PUSH_ACTION));
                }
                switch (action) {
                    case pmc:
                        Log.i("Clearing pending Message Center push notification.");
                        prefs.edit().remove(Constants.PREF_KEY_PENDING_PUSH_NOTIFICATION).commit();
                        break;
                }
            } catch (JSONException e) {
                Log.w("Error parsing JSON from push notification.", e);
                MetricModule.sendError(activity.getApplicationContext(), e, "Parsing Push notification", pushData);
            }
        }
    }

    public static void scrollToBottom() {
        messageCenterView.scrollMessageListViewToBottom();
    }

    public static void onStop(Activity activity) {
        clearPendingMessageCenterPushNotification(activity);
        // Remove listener here.
        MessagePollingWorker.setForeground(false);
    }

    public static void onBackPressed(Activity activity) {
        clearPendingMessageCenterPushNotification(activity);
        MetricModule.sendMetric(activity, Event.EventLabel.message_center__close);
    }

    enum Trigger {
        enjoyment_dialog,
        message_center
    }
}
