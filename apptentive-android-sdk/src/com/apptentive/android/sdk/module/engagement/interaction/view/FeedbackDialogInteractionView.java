/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.engagement.interaction.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.AutomatedMessage;
import com.apptentive.android.sdk.model.Person;
import com.apptentive.android.sdk.model.TextMessage;
import com.apptentive.android.sdk.module.engagement.EngagementModule;
import com.apptentive.android.sdk.module.engagement.interaction.model.FeedbackDialogInteraction;
import com.apptentive.android.sdk.module.messagecenter.MessageManager;
import com.apptentive.android.sdk.module.messagecenter.view.EmailValidationFailedDialog;
import com.apptentive.android.sdk.storage.ApptentiveDatabase;
import com.apptentive.android.sdk.storage.PersonManager;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

/**
 * @author Sky Kelsey
 */
public class FeedbackDialogInteractionView extends InteractionView<FeedbackDialogInteraction> {

    private static final String CODE_POINT_LAUNCH = "launch";
    private static final String CODE_POINT_CANCEL = "cancel";
    private static final String CODE_POINT_DECLINE = "decline";
    private static final String CODE_POINT_SUBMIT = "submit";
    private static final String CODE_POINT_SKIP_VIEW_MESSAGES = "skip_view_messages";
    private static final String CODE_POINT_VIEW_MESSAGES = "view_messages";
    // Don't show the wrong view when we because of rotation.
    private static boolean feedbackDialogVisible = false;
    private static boolean thankYouDialogVisible = false;
    private CharSequence email;
    private CharSequence message;

    public FeedbackDialogInteractionView(FeedbackDialogInteraction interaction) {
        super(interaction);
    }

    public static void createMessageCenterAutoMessage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        boolean shownAutoMessage = prefs.getBoolean(Constants.PREF_KEY_AUTO_MESSAGE_SHOWN_AUTO_MESSAGE, false);

        // Migrate old values if needed.
        boolean shownManual = prefs.getBoolean(Constants.PREF_KEY_AUTO_MESSAGE_SHOWN_MANUAL, false);
        boolean shownNoLove = prefs.getBoolean(Constants.PREF_KEY_AUTO_MESSAGE_SHOWN_NO_LOVE, false);
        if (!shownAutoMessage) {
            if (shownManual || shownNoLove) {
                shownAutoMessage = true;
                prefs.edit().putBoolean(Constants.PREF_KEY_AUTO_MESSAGE_SHOWN_AUTO_MESSAGE, true).commit();
            }
        }

        AutomatedMessage message = null;

        if (!shownAutoMessage) {
            prefs.edit().putBoolean(Constants.PREF_KEY_AUTO_MESSAGE_SHOWN_AUTO_MESSAGE, true).commit();
            message = AutomatedMessage.createWelcomeMessage(context);
        }
        if (message != null) {
            ApptentiveDatabase db = ApptentiveDatabase.getInstance(context);
            db.addOrUpdateMessages(message);
            db.addPayload(message);
        }
    }

    private static void cleanup() {
        feedbackDialogVisible = false;
        thankYouDialogVisible = false;
    }

    @Override
    public void show(final Activity activity) {
        super.show(activity);
        activity.setContentView(R.layout.apptentive_feedback_dialog_interaction);

        // Legacy support: We can remove this when we switch over to 100% interaction based Message Center.
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.PREF_KEY_MESSAGE_CENTER_SHOULD_SHOW_INTRO_DIALOG, false).commit();

        if (!thankYouDialogVisible) {
            if (!feedbackDialogVisible) {
                EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_LAUNCH);
            }

            String title = interaction.getTitle();
            final AutoCompleteTextView emailView = (AutoCompleteTextView) activity.findViewById(R.id.email);
            EditText messageView = (EditText) activity.findViewById(R.id.message);
            Button noButton = (Button) activity.findViewById(R.id.decline);
            final Button sendButton = (Button) activity.findViewById(R.id.submit);

            // Title
            if (title != null) {
                TextView titleView = (TextView) activity.findViewById(R.id.title);
                titleView.setText(title);
            }

            // Body
            String body = interaction.getBody(activity);
            TextView bodyView = (TextView) activity.findViewById(R.id.body);
            bodyView.setText(body);

            // Email
            String personEnteredEmail = PersonManager.loadPersonEmail(activity);
            if (!interaction.isAskForEmail()) {
                emailView.setVisibility(View.GONE);
            } else if (!Util.isEmpty(personEnteredEmail)) {
                emailView.setVisibility(View.GONE);
                email = personEnteredEmail;
            } else {
                String personInitialEmail = PersonManager.loadInitialPersonEmail(activity);
                if (!Util.isEmpty(personInitialEmail)) {
                    emailView.setText(personInitialEmail);
                    email = personInitialEmail;
                }

                String emailHintText = interaction.getEmailHintText();
                if (emailHintText != null) {
                    emailView.setHint(emailHintText);
                } else if (interaction.isEmailRequired()) {
                    emailView.setHint(R.string.apptentive_edittext_hint_email_required);
                }

                // Pre-populate a list of possible emails based on those pulled from the phone.
                ArrayAdapter<String> emailAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, Util.getAllUserAccountEmailAddresses(activity));
                emailView.setAdapter(emailAdapter);
                emailView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        emailView.showDropDown();
                        return false;
                    }
                });
                emailView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        email = charSequence;
                        validateForm(sendButton);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            }

            // Message
            String messageHintText = interaction.getMessageHintText();
            if (messageHintText != null) {
                messageView.setHint(messageHintText);
            }
            messageView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    message = charSequence;
                    validateForm(sendButton);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });


            // No
            String no = interaction.getDeclineText();
            if (no != null) {
                noButton.setText(no);
            }
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cleanup();
                    EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_DECLINE);
                    activity.finish();
                }
            });

            // Send
            String send = interaction.getSubmitText();
            if (send != null) {
                sendButton.setText(send);
            }
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.hideSoftKeyboard(activity, view);

                    if (email != null && email.length() != 0 && !Util.isEmailValid(email.toString())) {
                        EmailValidationFailedDialog dialog = new EmailValidationFailedDialog(activity);
                        dialog.show();
                        return;
                    }

                    // Before we send this message, send an auto message.
                    createMessageCenterAutoMessage(activity);

                    sendMessage(activity);

                    EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_SUBMIT);
                    thankYouDialogVisible = true;
                    feedbackDialogVisible = false;
                    activity.findViewById(R.id.feedback_dialog).setVisibility(View.GONE);
                    activity.findViewById(R.id.thank_you_dialog).setVisibility(View.VISIBLE);
                }
            });
            validateForm(sendButton);
        } else {
            activity.findViewById(R.id.feedback_dialog).setVisibility(View.GONE);
            activity.findViewById(R.id.thank_you_dialog).setVisibility(View.VISIBLE);
        }

        // Thank You Title
        TextView thankYouTitleView = (TextView) activity.findViewById(R.id.thank_you_title);
        String thankYouTitle = interaction.getThankYouTitle();
        if (thankYouTitle != null) {
            thankYouTitleView.setText(thankYouTitle);
        }

        // Thank You Body
        TextView thankYouBodyView = (TextView) activity.findViewById(R.id.thank_you_body);
        String thankYouBody = interaction.getThankYouBody();
        if (thankYouBody != null) {
            thankYouBodyView.setText(thankYouBody);
        }

        // Thank You Close Button
        Button thankYouCloseButton = (Button) activity.findViewById(R.id.thank_you_close);
        String thankYouCloseText = interaction.getThankYouCloseText();
        if (thankYouCloseText != null) {
            thankYouCloseButton.setText(thankYouCloseText);
        }
        thankYouCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanup();
                EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_SKIP_VIEW_MESSAGES);
                activity.finish();
            }
        });

        // Thank You View Messages Button
        Button thankYouViewMessagesButton = (Button) activity.findViewById(R.id.thank_you_view_messages);
        String thankYouViewMessages = interaction.getThankYouViewMessagesText();
        if (thankYouViewMessages != null) {
            thankYouViewMessagesButton.setText(thankYouViewMessages);
        }
        thankYouViewMessagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanup();
                EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_VIEW_MESSAGES);
                activity.finish();
            }
        });
        feedbackDialogVisible = true;
    }

    private void sendMessage(final Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.PREF_KEY_MESSAGE_CENTER_SHOULD_SHOW_INTRO_DIALOG, false).commit();
        // Save the email.
        if (interaction.isAskForEmail()) {
            if (email != null && email.length() != 0) {
                PersonManager.storePersonEmail(activity, email.toString());
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
        textMessage.setBody(message.toString());
        textMessage.setRead(true);
/*
        // TODO: Figure out how to add custom data here.
		textMessage.setCustomData(customData);
		customData = null;
*/
        MessageManager.sendMessage(activity, textMessage);
    }

    private void validateForm(Button sendButton) {
        boolean passedEmail = true;
        if (interaction.isEmailRequired()) {
            passedEmail = !(email == null || email.length() == 0);
        }
        boolean passedMessage = !(message == null || message.length() == 0);

        sendButton.setEnabled(passedEmail && passedMessage);
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onBackPressed(Activity activity) {
        cleanup();
        EngagementModule.engageInternal(activity, interaction.getType().name(), CODE_POINT_CANCEL);
        activity.finish();
    }
}
