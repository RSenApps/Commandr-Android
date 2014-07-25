/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.apptentive.android.sdk.AboutModule;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.Configuration;
import com.apptentive.android.sdk.model.Event;
import com.apptentive.android.sdk.model.Message;
import com.apptentive.android.sdk.module.messagecenter.MessageManager;
import com.apptentive.android.sdk.module.metric.MetricModule;
import com.apptentive.android.sdk.util.Constants;
import com.apptentive.android.sdk.util.Util;

import java.util.List;

/**
 * @author Sky Kelsey
 */
public class MessageCenterView extends FrameLayout implements MessageManager.OnSentMessageListener {

    static OnSendMessageListener onSendMessageListener;
    /**
     * Used to save the state of the message text box if the user closes Message Center for a moment, attaches a file, etc.
     */
    private static CharSequence message;
    Activity context;
    ListView messageListView;
    MessageAdapter<Message> messageAdapter;
    EditText messageEditText;

    public MessageCenterView(Activity context, OnSendMessageListener onSendMessageListener) {
        super(context);
        this.context = context;
        MessageCenterView.onSendMessageListener = onSendMessageListener;
        this.setId(R.id.apptentive_message_center_view);
        setup(); // TODO: Move this into a configurationchange handler?
    }

    public static void showAttachmentDialog(Context context, final Uri data) {
        if (data == null) {
            Log.d("No attachment found.");
            return;
        }
        AttachmentPreviewDialog dialog = new AttachmentPreviewDialog(context);
        dialog.setImage(data);
        dialog.setOnAttachmentAcceptedListener(new AttachmentPreviewDialog.OnAttachmentAcceptedListener() {
            @Override
            public void onAttachmentAccepted() {
                onSendMessageListener.onSendFileMessage(data);
            }
        });
        dialog.show();
    }

    protected void setup() {
        LayoutInflater inflater = context.getLayoutInflater();
        inflater.inflate(R.layout.apptentive_message_center, this);

        TextView titleTextView = (TextView) findViewById(R.id.apptentive_message_center_header_title);
        String titleText = Configuration.load(context).getMessageCenterTitle();
        if (titleText != null) {
            titleTextView.setText(titleText);
        }

        messageListView = (ListView) findViewById(R.id.apptentive_message_center_list);
        messageListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        messageEditText = (EditText) findViewById(R.id.apptentive_message_center_message);

        if (message != null) {
            messageEditText.setText(message);
            messageEditText.setSelection(message.length());
        }

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                message = editable.toString();
            }
        });
        View send = findViewById(R.id.apptentive_message_center_send);
        send.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String text = messageEditText.getText().toString().trim();
                if (text.length() == 0) {
                    return;
                }
                messageEditText.setText("");
                onSendMessageListener.onSendTextMessage(text);
                message = null;
                Util.hideSoftKeyboard(context, view);
            }
        });

        View aboutApptentive = findViewById(R.id.apptentive_message_center_powered_by_apptentive);
        aboutApptentive.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AboutModule.getInstance().show(context);
            }
        });

        View attachButton = findViewById(R.id.apptentive_message_center_attach_button);
        // Android devices can't take screenshots until version 4+
        boolean canTakeScreenshot = Build.VERSION.RELEASE.matches("^4.*");
        if (canTakeScreenshot) {
            attachButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    MetricModule.sendMetric(context, Event.EventLabel.message_center__attach);
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    context.startActivityForResult(intent, Constants.REQUEST_CODE_PHOTO_FROM_MESSAGE_CENTER);
                }
            });
        } else {
            attachButton.setVisibility(GONE);
        }
        messageAdapter = new MessageAdapter<Message>(context);
        messageListView.setAdapter(messageAdapter);
    }

    public void setMessages(final List<Message> messages) {
        messageListView.post(new Runnable() {
            public void run() {
                messageAdapter.clear();
                for (Message message : messages) {
                    addMessage(message);
                }
            }
        });
    }

    public void addMessage(Message message) {
        if (message.isHidden()) {
            return;
        }
        messageAdapter.add(message);
        messageListView.post(new Runnable() {
            public void run() {
                scrollMessageListViewToBottom();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // We should never get a message passed in that is not appropriate for the view it goes into.
    public synchronized void onSentMessage(final Message message) {
        setMessages(MessageManager.getMessages(context));
    }

    public void scrollMessageListViewToBottom() {
        messageListView.post(new Runnable() {
            public void run() {
                // Select the last row so it will scroll into view...
                messageListView.setSelection(messageAdapter.getCount() - 1);
            }
        });
    }

    public interface OnSendMessageListener {
        void onSendTextMessage(String text);

        void onSendFileMessage(Uri uri);
    }
}
