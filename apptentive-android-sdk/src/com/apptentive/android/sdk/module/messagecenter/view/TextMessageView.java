/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.TextMessage;

/**
 * @author Sky Kelsey
 */
public class TextMessageView extends PersonalMessageView<TextMessage> {

    public TextMessageView(Context context, TextMessage message) {
        super(context, message);
    }

    protected void init(TextMessage message) {
        super.init(message);
        LayoutInflater inflater = LayoutInflater.from(context);
        FrameLayout bodyLayout = (FrameLayout) findViewById(R.id.apptentive_message_body);
        inflater.inflate(R.layout.apptentive_message_body_text, bodyLayout);
    }

    public void updateMessage(final TextMessage newMessage) {
        super.updateMessage(newMessage);
        // Set content
        TextView textView = (TextView) findViewById(R.id.apptentive_text_message_text);
        textView.setText(newMessage.getBody());
    }
}
